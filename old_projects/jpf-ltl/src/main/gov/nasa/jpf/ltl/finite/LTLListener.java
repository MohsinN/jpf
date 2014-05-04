/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.jvm.AnnotationInfo;
import gov.nasa.jpf.jvm.Area;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.StaticArea;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.ltl.finite.trans.Edge;
import gov.nasa.jpf.ltl.finite.trans.Graph;
import gov.nasa.jpf.ltl.finite.trans.Node;
import gov.nasa.jpf.ltl.property.Field;
import gov.nasa.jpf.ltl.property.LTLProperty;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.ltl.graphio.Writer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

/**
 * This is an auto load listener that gets the LTL specification from
 * annotations and checks for the satisfiability of a finite run sequence. Two
 * annotations correspond to this listener are {@link gov.nasa.jpf.ltl.LTLSpec
 * @LTLSpec} and {@link gov.nasa.jpf.ltl.LTLSpecFile @LTLSpecFile}. This uses
 * {@link gov.nasa.jpf.ltl.finite.trans.Translator Translator} to convert the
 * LTL specification to a finite state automaton then checks the satisfiability
 * with this automaton.
 * 
 * <p>
 * This is only designed to be used with the
 * {@link gov.nasa.jpf.search.DFSearch DFSearch} but is modified to run in
 * parallel with the {@link gov.nasa.jpf.ltl.ddfs.DDFSearch DDFSearch} and
 * doesn't affect the latter. In order to do that the users must specify the
 * following option in the JPF's configuration.
 * </p>
 * <p>
 * +finite=true
 * </p>
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public class LTLListener extends PropertyListenerAdapter {
  private LTLProperty ltlSpec; // The linear temporal property
  public static LTLListener instance = null;

  public LTLListener() {
    super();
    assert instance == null;
    instance = this;
  }

  /**
   * Extracts the FieldInfo instances of fields which are appeared in the LTL
   * formula
   * 
   * @param ci
   */
  public void updateFieldList(ClassInfo ci) {
    filterFields(ci.getDeclaredInstanceFields());
    filterFields(ci.getDeclaredStaticFields());
  }

  /**
   * Gets the LTL formula specification from <code>Annotation</code> in the main
   * class of SUT and then translates it into a finite state automaton and gets
   * the {@code FieldInfo} object of the instance fields which appeared in the
   * LTL formula.
   */
  @Override
  public void classLoaded(JVM vm) {
    ClassInfo ci = vm.getClassInfo();
    /*
     * Determines the SUT is finite or infinite according to configuration
     * options
     */
    Config conf = vm.getConfig();
    if (!isEmptinessChecking) { // isEmptinessChecking=false
      String isReverse = conf.getProperty("finite");
      if (isReverse != null) {
        if (isReverse.equals("false")) {// finite=false
          isEmptinessChecking = true;
          return;
        }
      }
    }
    if (isEmptinessChecking) // infinite mode
      return;

    String isPrintTrace = conf.getProperty("printTrace");
    if (isPrintTrace != null)
      if (isPrintTrace.equals("true"))
        printTrace = true;

    // Gets the LTL formula from annotation in the main class
    AnnotationInfo ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpec");
    if (ai == null)
      ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpecFile");
    if (ai != null) {
      ltlSpec = new LTLProperty(ai.valueAsString(), isEmptinessChecking, ai
          .getName().endsWith("File"), ci.getName());
      gov.nasa.ltl.graph.Graph<String> graph = null;
      Graph buchi;
      if (!isEmptinessChecking) {
        // buchi = ltlSpec.getActualAutomata();
        graph = ltlSpec.getActualBuchi();
        buchi = ltlSpec.getFSA();
        
        System.out
            .println("\n\nBuchi translated by ltl2automata for finite trace");
        buchi.print();
      } else {
        buchi = ltlSpec.getNegatedAutomata();
        graph = ltlSpec.getNegatedBuchi();
      }

      Writer<String> v = Writer.getWriter(Writer.Format.SPIN, System.out);
      if (graph != null) {
        System.out.println("\n\nBuchi translated by ltl2buchi");
        v.write(graph);
      }

      // Sets the automata to jpf-automata
      setAutomaton(buchi);
      setFields(ltlSpec.getFields());
      updateFieldList(ci);
      
      //automataObjectRef = vm.getDynamicArea().newObject(new AutomatonClassInfo(currentState), null);
      automataObjectRef = vm.getDynamicArea().newString("", null);
      ElementInfo ei = vm.getDynamicArea().get(automataObjectRef);
      ei.pinDown(true);
      ei.setObjectAttrNoClone(currentStates);

      // Use Jung to visualize the buchi automata
      if (buchi != null) {
        String isShowBuchi = conf.getProperty("show_buchi");
        if (isShowBuchi != null)
          if (isShowBuchi.equals("true"))
            ltlSpec.showGraph(graph);
      }

    }
  }

  @Override
  public String getErrorMessage() {
    return "LTL violated: " + ltlSpec.getProperty();
  }

  /**
   * Returns the information of a field or a local variable at JPF runtime. This
   * method is used when we check the guard condition.
   * 
   * @param fullName
   *          Full name of the needed field or variable.
   * @return A {@link Field} object representing this field, {@code null}
   *         otherwise.
   * @see gov.nasa.jpf.ltl.atom.Operand.Var#Var(String)
   */
  public static Field getField(String fullName) {
    return fieldList.get(fullName);
  }

  @SuppressWarnings("unchecked")
  public static Object getSymbolicValue(FieldInfo fi) {
    Area<?> iterable;
    if (fi.isStatic())
      iterable = StaticArea.getStaticArea();
    else
      iterable = DynamicArea.getHeap();
    for (ElementInfo ei : (Area<ElementInfo>) iterable) {
      if (!ei.getClassInfo().isInstanceOf(fi.getClassInfo()))
        continue;
      return ei.getFieldAttr(fi);
    }
    return null;
  }

  private Vector<String> fields;

  public void setFields(Vector<String> fields) {
    this.fields = fields;
  }

  private static Hashtable<String, Field> fieldList = new Hashtable<String, Field>();

  private Set<Node> currentStates;
  private boolean isRequestBacktracked;
  private boolean forcedVal = false;
  private boolean retainVal = false;
  private int automataObjectRef;
  private boolean isViolated;
  private boolean isPropertyAdded;

  boolean isEmptinessChecking = false;

  protected boolean printTrace;

  @Override
  public boolean check(Search search, JVM vm) {
    // System.out.println("Property checked");
    return !isViolated;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final void executeInstruction(JVM vm) {
    if (isEmptinessChecking)
      return;
    ThreadInfo ti = vm.getLastThreadInfo();
    Instruction insn = ti.getPC();
    Set<Node> temp = currentStates;
    
    currentStates = (Set<Node>) vm.getDynamicArea().get(automataObjectRef).getObjectAttr();;
    
    if (printTrace && !temp.equals(currentStates)) {
      for (Node state : currentStates)
        System.out.println("S---------" + state.getId());
    }

    // DummyInstruction is only used to hold the first slot in the transition
    // so we always skip it
    if (insn instanceof DummyInstruction) {
      ti.skipInstruction();
      ti.setNextPC(insn.getNext());      
    } else if (insn.isFirstInstruction() && isEmptinessChecking)
      isRequestBacktracked = false;
  }

  /**
   * Extracts the local variable information which appeared in the LTL spec. at
   * JPF runtime
   * 
   * @param sf
   *          The stack frame which store the local variable
   */
  private void extractLocalVar(StackFrame sf) {
    if (sf == null)
      return;
    MethodInfo mi = sf.getMethodInfo();
    String methodName = mi.getClassName() + "." + mi.getLongName();
    String[] localNames = sf.getLocalVariableNames();
    if (localNames == null || fields == null)
      return;
    for (String localField : fields) {
      int position = 0; // the index of local variable in its stack frame
      for (String localVarName : localNames) {
        String localFullName = methodName + "." + localVarName;
        if (localField.equals(localFullName)) {
          // if a local variable is already seen formerly, we only need to
          // update its stack frame
          Field field = fieldList.get(localFullName);
          if (field == null) {
            field = new Field(sf, localVarName,
                sf.getLocalVariableType(localVarName), position);
            fieldList.put(localFullName, field);
          } else
            field.setStackFrame(sf); // stack frame of a local variable is not
          // fixed
        }
        position++;
      }
    }
  }

  /**
   * Gets the field that appear in the LTL formulae from the {@code FieldInfo}
   * list
   * 
   * @param fieldInfoList
   *          All the fields in a loaded class
   */
  protected void filterFields(FieldInfo[] fieldInfoList) {
    if (fields == null)
      return;
    for (FieldInfo field : fieldInfoList) {
      if (fields.contains(field.getFullName())) {
        fieldList.put(field.getFullName(), new Field(field));
      }
    }
  }

  private PathCondition getCurrentPC(SystemState ss) {
    PCChoiceGenerator cg = ss
        .getLastChoiceGeneratorOfType(PCChoiceGenerator.class);
    if (cg != null) {
      PathCondition pc = cg.getCurrentPC();
      if (pc == null) {
        pc = new PathCondition();
        cg.setCurrentPC(pc);
      }
      // System.out.println("current pc: " + pc.toString());

      return pc;
    } else {
      cg = new PCChoiceGenerator(1);
      PathCondition pc = new PathCondition();
      cg.advance();
      cg.setCurrentPC(pc);
      // ss.setNextChoiceGenerator(cg);
      return pc;
    }
    // return null;
  }

  public String invokedMethodName(JVM vm) {
    Instruction insn = vm.getLastInstruction();
    if (!(insn instanceof InvokeInstruction))
      return null;
    MethodInfo mi = ((InvokeInstruction) insn).getInvokedMethod();
    return mi.getClassName() + "." + mi.getLongName();
  }

  /**
   * Checks if we can't make a progress in the automaton after executing an
   * instruction.
   */
  @Override
  public final void instructionExecuted(JVM vm) {
    ThreadInfo ti = vm.getLastThreadInfo();
    // Extract local variables
    extractLocalVar(ti.getTopFrame());

    if (isEmptinessChecking)
      return;
    Instruction insn = vm.getLastInstruction();

    MethodInfo mi = insn.getMethodInfo();
    String methodFullName = "";
    if (mi != null) {
      methodFullName = mi.getFullName();
    }

    if (isIgnoreIns(methodFullName)) // skip initializing instructions of JVM
      return;

    if (ti.isInstructionSkipped())
      return;

    // just for debugging purpose

    if (printTrace) {
      System.out.println(ti.getIndex() + "******");
      System.out.println(insn.getMethodInfo().getFullName());
      System.out.println(insn.getClass());
      printFields();
    }

    // If a ChoiceGenerator is created after this instruction, we'll ignore
    // checking this instruction because it will be re-executed immediately
    // after this instruction.
    SystemState ss = vm.getSystemState();
    if (ss.getNextChoiceGenerator() != null) {
      if (printTrace) {
        System.out.println("Choice generator ---------: "
            + ss.getNextChoiceGenerator().getClass());
      }
      return;
    }

    turnoffConcreteStateMatching(vm, insn);

    // Get the guard constraints in every out-going transitions from the current
    // state then check for satisfiability in conjunction with the current path
    // condition
    Vector<PathCondition> satisfiedPC = new Vector<PathCondition>();
    Set<Node> successors = new TreeSet<Node>();
    PathCondition currentPC = getCurrentPC(ss);
    if (printTrace)
      System.out.println(currentPC);
    Vector<Edge> transitions = new Vector<Edge>();
    boolean hasNewPC = false;

    for (Node state : currentStates)
      transitions.addAll(state.getOutgoingEdges());

    // The checker should stop when there is a path in SUT following a path
    // not enabled by any transitions in automaton. That means there is a path
    // in SUT which automaton cannot move to a new state following that path.
    PathCondition pc = currentPC.make_copy();
    boolean hasAPath = false;
    for (Edge transition : transitions) {
      AutomataGuard guard = new AutomataGuard(transition.getGuard(), currentPC,
          invokedMethodName(vm));
      if (!guard.getConstraints().isEmpty()) {
        Iterator<Constraint> iter = guard.getConstraints().iterator();
        pc.prependUnlessRepeated(iter.next().not());
      }
      else if (guard.isSatisfiable()) {
        hasAPath = true;
        break;
      }
    }
    if (!hasAPath && pc.simplify()) {
      //System.out.println("NEW ERROR #####################");
      isViolated = true;
      ti.breakTransition();
      return;
    }

    for (Edge transition : transitions) {
      AutomataGuard guard = new AutomataGuard(transition.getGuard(), currentPC,
          invokedMethodName(vm));
      if (guard.isSatisfiable()) {
        successors.add(transition.getNext());

        if (guard.isPCChanged()) {
          satisfiedPC.add(guard.getPathCondition());
          hasNewPC = true;
        } else if (!satisfiedPC.contains(currentPC))
          satisfiedPC.add(currentPC);
      }
    }

    if (successors.size() == 0) {
      if (isEmptinessChecking) {
        isRequestBacktracked = true;
        ss.setIgnored(true);
        if (printTrace)
          System.out.println("request backtrack");
      } else {
        isViolated = true;
        ti.breakTransition();
      }
      return;
    }
    // Get the next successor
    else {
      Instruction nextPc = ti.getNextPC();
      if (nextPc != null) 
        vm.getDynamicArea().get(automataObjectRef).setObjectAttrNoClone(successors);

      if (hasNewPC) {
        DummyInstruction dummy = new DummyInstruction(nextPc);
        ti.setNextPC(dummy);
        PCChoiceGenerator ltlCG = new AutomataChoiceGenerator(satisfiedPC);
        ss.setNextChoiceGenerator(ltlCG);
        ss.setForced(true);
      }
    }
  }

  /**
   * Checks if there is at least one current state of the automaton is
   * accepting.
   * 
   * @return {@code true} if there exists at least one accepting state,
   *         {@code false} otherwise.
   */
  public final boolean isAcceptingState() {
    for (Node state : currentStates)
      if (state.isAccepting())
        return true;
    return false;
  }

  private boolean isIgnoreIns(String name) {
    if (name.startsWith("java.") || name.startsWith("sun.")
        || name.startsWith("javax.") || name.startsWith("com.")
        || name.startsWith("[clinit]") || name.endsWith("<clinit>()V"))
      return true;
    return false;
  }

  public boolean isReverseChecking() {
    return isEmptinessChecking;
  }
  
  public void objectCreated(JVM vm) {
    if(isEmptinessChecking)
      return;
    ElementInfo ei = vm.getLastElementInfo();
    //if(printTrace)
      //System.out.println("Object created --- " + ei);
    Enumeration<Field> fields = fieldList.elements();
    while(fields.hasMoreElements()) {
      Field field = fields.nextElement();
      if(field.isLocalVar())
        continue;
      if(field.getFieldInfo().getClassInfo().getUniqueId() == ei.getClassInfo().getUniqueId())
        field.addElementInfo(ei);
    }
  }
  public void objectReleased(JVM vm) {
    if(isEmptinessChecking)
      return;
    ElementInfo ei = vm.getLastElementInfo();
    //if(printTrace)
      //System.out.println("Object released --- " + ei);
    Enumeration<Field> fields = fieldList.elements();
    while(fields.hasMoreElements()) {
      Field field = fields.nextElement();
      if(field.isLocalVar())
        continue;
      if(field.getFieldInfo().getClassInfo().getUniqueId() == ei.getClassInfo().getUniqueId())
        field.removeElementInfo(ei);
    }
  }
  
  

  /*
   * Just for debugging purpose
   */
  public void printFields() {
    Enumeration<Field> values = fieldList.elements();
    while (values.hasMoreElements())
      System.out.println(values.nextElement());
  }

  @Override
  public void searchStarted(Search search) {
    // System.out.println("search started");
    if (isEmptinessChecking)
      return;
    if (!isPropertyAdded) {
      search.addProperty(this);
      isPropertyAdded = true;
    }
  }

  /**
   * Initializes the automaton that needs to be checked with.
   * 
   * @param automaton
   *          The automaton translated from LTL specification.
   */
  public void setAutomaton(Graph automaton) {
    currentStates = new TreeSet<Node>();
    currentStates.add(automaton.getInitState());
  }

  @Override
  public void stateAdvanced(Search search) {
    // isStarted = true;
    if (isEmptinessChecking)
      return;
    if (printTrace) {
      System.out.println("----------- state advanced -------");
      if (search.isEndState())
        System.out.println("is end state");
    }

    if (!isPropertyAdded) {
      search.addProperty(this);
      isPropertyAdded = true;
    }

    if (isEmptinessChecking && search.isEndState() && isAcceptingState()
        && !isRequestBacktracked)
      isViolated = true;
    else if (!isEmptinessChecking && search.isEndState() && !isAcceptingState())
      isViolated = true;
  }

  /**
   * Backtracks the previous states of the automaton and keeps the concrete
   * state matching to be turned off when checking a symbolic method.
   * 
   * @param search
   *          The {@code Search} object
   */
  @Override
  public void stateBacktracked(Search search) {
    if (isEmptinessChecking)
      return;
    if (printTrace)
      System.out.println("------backtracked------");
    JVM vm = search.getVM();
    currentStates = (Set<Node>) vm.getDynamicArea().get(automataObjectRef).getObjectAttr();
    Config conf = vm.getConfig();
    ChoiceGenerator<?> cg = vm.getChoiceGenerator();
    Instruction insn = cg.getInsn();
    SystemState ss = vm.getSystemState();
    MethodInfo mi = insn.getMethodInfo();
    String className = mi.getClassName();
    String methodName = mi.getFullName();
    int numberOfArgs = mi.getNumberOfArguments();

    if (BytecodeUtils.isClassSymbolic(conf, className, mi, methodName)
        || BytecodeUtils.isMethodSymbolic(conf, methodName, numberOfArgs, null)) {
      // get the original values and save them for restoration after
      // we are done with symbolic execution
      retainVal = ss.getRetainAttributes();
      forcedVal = ss.isForced();
      // turn off state matching
      ss.setForced(true);
      // make sure it stays turned off when a new state is created
      ss.retainAttributes(true);
    }
  }

  /*
   * For symbolic execution, we don't don't use the concrete state matching. The
   * checking for symbolic state matching must include the PC subsumption and
   * the heap shape.
   */
  private void turnoffConcreteStateMatching(JVM vm, Instruction insn) {
    ThreadInfo ti = vm.getLastThreadInfo();
    SystemState ss = vm.getSystemState();
    Config conf = vm.getConfig();
    if (insn instanceof InvokeInstruction) {
      InvokeInstruction md = (InvokeInstruction) insn;
      String methodName = md.getInvokedMethodName();
      int numberOfArgs = md.getArgumentValues(ti).length;
      MethodInfo mi = md.getInvokedMethod();
      ClassInfo ci = mi.getClassInfo();
      String className = ci.getName();
      if (BytecodeUtils.isClassSymbolic(conf, className, mi, methodName)
          || BytecodeUtils.isMethodSymbolic(conf, mi.getFullName(),
              numberOfArgs, null)) {
        // get the original attribute of the system state to restore later
        retainVal = ss.getRetainAttributes();
        forcedVal = ss.isForced();
        // turn off concrete state matching
        ss.setForced(true);
        // make sure it stays turned off when a new state is created
        ss.retainAttributes(true);
      }
    } else if (insn instanceof ReturnInstruction) {
      MethodInfo mi = insn.getMethodInfo();
      ClassInfo ci = mi.getClassInfo();
      if (null != ci) {
        String className = ci.getName();
        String methodName = mi.getName();
        int numberOfArgs = mi.getNumberOfArguments();
        if (BytecodeUtils.isClassSymbolic(conf, className, mi, methodName)
            || BytecodeUtils.isMethodSymbolic(conf, mi.getFullName(),
                numberOfArgs, null)) {
          // restore the prior attribute of system state
          ss.retainAttributes(retainVal);
          ss.setForced(forcedVal);
        }
      }
    }
  }
}
