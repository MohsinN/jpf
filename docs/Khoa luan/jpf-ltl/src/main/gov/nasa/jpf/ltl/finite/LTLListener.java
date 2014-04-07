/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JPasswordField;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.jvm.AnnotationInfo;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DynamicArea;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.FieldInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.StaticArea;
import gov.nasa.jpf.jvm.StaticElementInfo;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Area.Iterator;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.ltl.finite.trans.Edge;
import gov.nasa.jpf.ltl.finite.trans.Graph;
import gov.nasa.jpf.ltl.finite.trans.Node;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.ltl.graphio.Writer;

/**
 * This is an autoload listener that gets the LTL formula (f) from annotation,
 * and checks for the satisfiability whenever an instruction of SUT executed. In
 * the finite trace, f is translated into Buchi automaton but in the infinite
 * trace, the negated one (!f) is translated. Then, this uses jpf-automata to
 * check the translated Buchi automata.
 * 
 * @author Phuc Nguyen Dinh
 * 
 */
public class LTLListener extends PropertyListenerAdapter {
  private LTLProperty ltlSpec; // The linear temporal property

  public LTLListener() {
    super();
  }

  /**
   * Gets the LTL formula specification from annotation in the main class of SUT
   * and translates into Buchi automata. Gets the FieldInfo instances of the
   * instance fields which appeared in the ltl formula.
   */
  @Override
  public void classLoaded(JVM vm) {
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
    if(isEmptinessChecking)
      return;

    String isPrintTrace = conf.getProperty("printTrace");
    if(isPrintTrace != null)
      if(isPrintTrace.equals("true"))
        printTrace = true;
    
    // Gets the LTL formula from annotation in the main class
    ClassInfo ci = vm.getClassInfo();
    AnnotationInfo ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpec");
    if (ai == null)
      ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpecFile");
    if (ai != null) {
      ltlSpec = new LTLProperty(ai.valueAsString(), isEmptinessChecking);
      gov.nasa.ltl.graph.Graph<String> graph = null;
      Graph buchi;
      if (!isEmptinessChecking) {
        // buchi = ltlSpec.getActualAutomata();
        graph = ltlSpec.getActualBuchi();
        buchi = ltlSpec.getFiniteBuchi(); // In the finite trace, f is
                                          // translated
        System.out
            .println("\n\nBuchi translated by ltl2automata for finite trace");
        buchi.print();
      } else {
        buchi = ltlSpec.getNegatedAutomata(); // In the infinite trace, !f is
                                              // translated
        graph = ltlSpec.getNegatedBuchi();

      }

      Writer<String> v = Writer.getWriter(Writer.Format.SPIN, System.out);
      if (graph != null) {
        System.out.println("\n\nBuchi translated by ltl2buchi");
        v.write(graph);
      }

      // Sets the automata to jpf-automata
      setAutomaton(buchi);
      fields = ltlSpec.getFields();

      // Use Jung to visualize the buchi automata
      if (buchi != null) {
        String isShowBuchi = conf.getProperty("show_buchi");
        if (isShowBuchi != null)
          if (isShowBuchi.equals("true"))
            ltlSpec.showGraph(graph);
      }

    }

    // Extracts the FieldInfo instances of fields which are appeared in the LTL
    // formula
    FieldInfo[] declaredInstanceFields = ci.getDeclaredInstanceFields();
    filterFields(declaredInstanceFields);

    FieldInfo[] staticInstanceFields = ci.getDeclaredStaticFields();
    filterFields(staticInstanceFields);
       
    
  }

  @Override
  public String getErrorMessage() {
    return "SUT violated the LTL property: " + ltlSpec.getProperty();
  }
  
  public static Field getField(String fullName) {
      if (ddfsMode) {
        String[] parts = new StringBuffer (fullName).reverse ().
            toString ().split ("\\.", 2);
        String var = new StringBuffer (parts[0]).reverse ().toString (),
               cls = new StringBuffer (parts[1]).reverse ().toString ();
        JVM vm = JVM.getVM ();
        StaticElementInfo clsEi = vm.getClassReference (cls);
        if (clsEi == null)
          return null;
        FieldInfo fin = clsEi.getClassInfo ().getStaticField (var);
        if (fin == null)
          return null;
        Field f = new Field (fin);
        f.setValue (fin.getValueObject (clsEi.getFields ()));
        return f;
      }
    
    return fieldList.get(fullName);
  }

  public static Object getSymbolicValue(FieldInfo fi) {
    Iterator iterator;
    if (fi.isStatic())
      iterator = StaticArea.getStaticArea().iterator();
    else
      iterator = DynamicArea.getHeap().iterator();
    while (iterator.hasNext()) {
      ElementInfo ei = iterator.next();
      if (!ei.getClassInfo().isInstanceOf(fi.getClassInfo()))
        continue;
      return ei.getFieldAttr(fi);
    }
    return null;
  }

  private Graph automaton;

  private Vector<String> fields;

  private static Hashtable<String, Field> fieldList = new Hashtable<String, Field>();

  private Set<Node> currentState;
  private boolean isRequestBacktracked;
  private boolean forcedVal = false;
  private boolean retainVal = false;
  private boolean isViolated;
  private boolean isPropertyAdded;
  private Stack<Set<Node>> trackedStates = new Stack<Set<Node>>();
  
  // Indicate whether we check the emptiness or inclusion of the two languages.
  // Default 'isEmptinessChecking = false' means checking inclusion of two languages.
  boolean isEmptinessChecking = false;
  
  public static boolean ddfsMode;
  
  protected boolean printTrace;

  @Override
  public boolean check(Search search, JVM vm) {
    //System.out.println("Property checked");       
    return !isViolated;
  }

  @Override
  public final void executeInstruction(JVM vm) {
    if(isEmptinessChecking)
      return;
    ThreadInfo ti = vm.getLastThreadInfo();
    Instruction insn = ti.getPC();
    SystemState ss = vm.getSystemState();

    // DummyInstruction is only used to hold the first slot in the transition
    // so we always skip it
    if (insn instanceof DummyInstruction) {
      ti.skipInstruction();
      ti.setNextPC(insn.getNext());
      currentState = ((DummyInstruction) insn).successors;
      if(printTrace)
      {
        for(Node state: currentState)
        System.out.println("S---------" + state.getId());
      }
    } else if (insn.isFirstInstruction() && isEmptinessChecking)
      isRequestBacktracked = false;
  }

  /**
   * Extract the symbolic and concrete value of instance field which appeared in
   * the LTL formulae at JPF runtime. We must distinguish between the static
   * field and the non-static field
   * 
   * @param heap
   *          the dynamic area
   * @param staticArea
   *          the static area
   * @param filter
   *          the instance fields which need to be updated
   */  
  private void updateFieldValues(DynamicArea heap, StaticArea staticArea, Enumeration<Field> filter) {
    while(filter.hasMoreElements()) {
      Field field = filter.nextElement();
      if(field.isLocalVar())
        continue;
      FieldInfo fi = field.getFieldInfo();
      ElementInfo ei = null;
      if(fi.isStatic()) {
        Iterator staticItr = staticArea.iterator();
        while(staticItr.hasNext()) {
          ei = staticItr.next();
          if(ei.getClassInfo().getUniqueId() == fi.getClassInfo().getUniqueId())
            break;
        }
      } else {
        Iterator dynamicItr = heap.iterator();
        while(dynamicItr.hasNext()) {
          ei = dynamicItr.next();
          if(ei.getClassInfo().getUniqueId() == fi.getClassInfo().getUniqueId())
            break;
        }
      }
      if(ei != null) {
        field.setAttr(ei.getFieldAttr(fi));
        field.setValue(ei);
      }
    }
  }

  /**
   * Extract the local variable information which appeared in the LTL formulae
   * at JPF runtime
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
    if (localNames == null)
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
            field = new Field(sf, localVarName, sf
                .getLocalVariableType(localVarName), position);
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
   * Get the field that appear in the LTL formulae from the FieldInfo list
   * 
   * @param fieldInfoList
   *          All the fields in a loaded class
   */
  protected void filterFields(FieldInfo[] fieldInfoList) {
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
      //ss.setNextChoiceGenerator(cg);
      return pc;
    }
    // return null;
  }

  @Override
  public final void instructionExecuted(JVM vm) {
    if(isEmptinessChecking)
      return;
    Instruction insn = vm.getLastInstruction();

    MethodInfo mi = insn.getMethodInfo();
    String methodFullName = "";
    if (mi != null) {
      methodFullName = mi.getFullName();
    }

    if (isIgnoreIns(methodFullName)) //skip initializing instructions of JVM
      return;

    ThreadInfo ti = vm.getLastThreadInfo();
    if (ti.isInstructionSkipped())
      return;
    
    // just for debugging purpose
    
    if(printTrace) {
      System.out.println(ti.getIndex() + "******");
      System.out.println(insn.getMethodInfo().getFullName());
      System.out.println(insn.getClass());
      printFields();
    }

    // If a ChoiceGenerator is created after this instruction, we'll ignore
    // checking this instruction because it will be re-executed immediately after this instruction.
    SystemState ss = vm.getSystemState();
    if (ss.getNextChoiceGenerator() != null) {
      if(printTrace) {
        System.out.println("Choice generator ---------: "
          + ss.getNextChoiceGenerator().getClass() + "\n state tracked");
      }
      trackedStates.add(currentState);
      return;
    }
   
    turnoffConcreteStateMatching(vm, insn);
    
    

    updateFieldValues(DynamicArea.getHeap(), StaticArea.getStaticArea(), fieldList.elements());

    // Extract local variables
    extractLocalVar(ti.getTopFrame());
    

    // Get the guard constraints in every out-going transitions from the current
    // state then check for satisfiability in conjunction with the current path
    // condition
    Vector<PathCondition> satisfiedPC = new Vector<PathCondition>();
    Set<Node> successors = new TreeSet<Node>();
    PathCondition currentPC = getCurrentPC(ss);
    if(printTrace)
      System.out.println(currentPC);
    Vector<Edge> transitions = new Vector<Edge>();
    boolean hasNewPC = false;
    for(Node state: currentState)
      transitions.addAll(state.getOutgoingEdges());
    for (Edge transition : transitions) {
      String invokedMethodName = null;
      if(insn instanceof InvokeInstruction) {
        MethodInfo invokedMethod = ((InvokeInstruction) insn).getInvokedMethod();
        invokedMethodName = invokedMethod.getClassName() + "." + invokedMethod.getLongName();
      }
      
      AutomataGuard guard = new AutomataGuard(transition.getGuard(), currentPC, invokedMethodName);
      if (guard.isSatisfiable()) {
        successors.add(transition.getNext());
        
        if(guard.isPCChanged()) {
          satisfiedPC.add(guard.getPathCondition());
          hasNewPC = true;
        }
        else if(!satisfiedPC.contains(currentPC))
          satisfiedPC.add(currentPC);
      }
    }

    if (successors.size() == 0) {
      if (isEmptinessChecking) {
        isRequestBacktracked = true;
        ss.setIgnored(true);
        if(printTrace)
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
      if (nextPc != null) {
        DummyInstruction dummy = new DummyInstruction(nextPc, successors);
        ti.setNextPC(dummy);
      }
      
      if(hasNewPC) {
        PCChoiceGenerator ltlCG = new AutomataChoiceGenerator(satisfiedPC, 
            successors, currentState);
        ss.setNextChoiceGenerator(ltlCG);
        ss.setForced(true);
      }
    }
  }

  public final boolean isAcceptingState() {
    for(Node state: currentState)
      if(state.isAccepting())
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
    //System.out.println("search started");
    if(isEmptinessChecking)
      return;
    if(!isPropertyAdded) {
      search.addProperty(this);
      isPropertyAdded = true;
    }
  }

  public void setAutomaton(Graph automaton) {
    this.automaton = automaton;
    currentState = new TreeSet<Node>();
    currentState.add(automaton.getInitState());
    trackedStates.push(currentState);
  }

  @Override
  public void stateAdvanced(Search search) {
    // isStarted = true;
    if(isEmptinessChecking)
      return;
    if(printTrace) {
      System.out.println("----------- state advanced -------");
      if(search.isEndState())
        System.out.println("is end state");
    }
    
    if(!isPropertyAdded) {
      search.addProperty(this);
      isPropertyAdded = true;
    }
    
    
    
    if(isEmptinessChecking && search.isEndState() && isAcceptingState() && !isRequestBacktracked)
      isViolated = true;
    else if(!isEmptinessChecking && search.isEndState() && !isAcceptingState())
      isViolated = true;
  }

  @Override
  public void stateBacktracked(Search search) {
    if(isEmptinessChecking)
      return;
    if(printTrace)
      System.out.println("------backtracked------");
    JVM vm = search.getVM();
    Config conf = vm.getConfig();
    ChoiceGenerator<?> cg = vm.getChoiceGenerator();
    if (cg instanceof AutomataChoiceGenerator)
      currentState = ((AutomataChoiceGenerator) cg).getPrevState();
    else if(!trackedStates.isEmpty()){
      if(!cg.hasMoreChoices()) {
        currentState = trackedStates.pop();
        if(printTrace) {
          for(Node state: currentState) 
            System.out.println(" S ...." +  state.getId());
          System.out.println("popped");
          }
      }
      else {
        currentState = trackedStates.peek();
        if(printTrace) {
        for(Node state: currentState) 
          System.out.println(" S ...." +  state.getId());
        System.out.println("peeked");
        }
      }
    }
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
