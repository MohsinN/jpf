/**
 * 
 */
package gov.nasa.jpf.ltl.ddfs;

import gov.nasa.jpf.Config;
import gov.nasa.ltl.graph.Edge;
import gov.nasa.ltl.trans.ParseErrorException;

import java.util.LinkedList;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.bytecode.ASTORE;
import gov.nasa.jpf.jvm.bytecode.GETSTATIC;
import gov.nasa.jpf.jvm.bytecode.ICONST;
import gov.nasa.jpf.jvm.bytecode.ISTORE;
import gov.nasa.jpf.vm.AnnotationInfo;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
// import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.PUTSTATIC;
import gov.nasa.jpf.ltl.Antlr.DetectParamLexer;
import gov.nasa.jpf.ltl.Antlr.DetectParamParser;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ApplicationContext;
import gov.nasa.jpf.vm.LocalVarInfo;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graph.Literal;
import gov.nasa.ltl.graph.Node;
import gov.nasa.ltl.trans.LTL2Buchi;
import gov.nasa.ltl.graphio.Writer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 * @author Michele Lombardi
 * 
 */
public class InfiniteListener extends ListenerAdapter {

  protected int insnCount = 0;
  protected boolean atDirectCallStackFrame = false, methodMatchNoNegate = false;
  protected LinkedList<Boolean> nonProgressBoundaryStack = new LinkedList<Boolean>();
  protected DDFSearch search = null;
  protected Graph<String> saveSpec = null;
  protected String specText = null, edge;
  protected ClassInfo specSource = null;
  protected boolean passedPropToLTLListener = false, methodMatchNegate = false, DEBUG=false;
  protected HashSet<String> LTLatoms = new HashSet<String>();
  VM vm;
  String ltl = null;

  
  public InfiniteListener(Config conf) {
    String isReverse = conf.getProperty("finite");
    if (isReverse != null) {
      if (isReverse.equals("true")) {
        try {
          throw new EndException("Set finite=false for this Listener and this Search.");
        } catch (EndException ex) {
          Logger.getLogger(InfiniteListener.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    

    String debugString = conf.getProperty("debug");
    if(debugString != null) {
      if (debugString.equals("true")) {
        DEBUG=true;
      }
    }
    
    if (DEBUG) {
      System.out.println("(DEBUG) InfiniteListener: end of constructor");
    }

  }
  @Override
  public void classLoaded(VM vm, ClassInfo loadedClass) {
    this.vm = vm;
    
//    if (DEBUG) {
//        System.out.println("(DEBUG) InfiniteListener: in classLoaded");
//    }
    
    ClassInfo ci = loadedClass;
    AnnotationInfo ai;
    ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpec");
    if (ai == null) {
      ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpecFile");
    }
    // System.err.println ("cL: ci = " + ci + ", ai = " + ai);
    if (ai == null) {
//      if (DEBUG) {
//        System.out.println("(DEBUG) InfiniteListener: returning without annotation");
//      }
      return;
    }
    if (ltl != null) {
//      if (DEBUG) {
//        System.out.println("(DEBUG) InfiniteListener: returning without formula");
//      }
      // TODO: Warn?
      return;
    }
        
    ltl = ai.valueAsString();
    
    if (DEBUG) {
      System.out.println("(DEBUG) InfiniteListener: in classLoaded, the LTL formula is "+ltl);
    }
    
    // TODO: we should probably raise an error if we have an LTL formula and we
    // try to set another one
    // LTL2Buchi doesn't translate a wrong LTL formula
    
    Graph<String> translate = null;
    gov.nasa.jpf.ltl.finite.trans.Graph translateFinite = null;
    try {
      translate = LTL2Buchi.translate("!(" + ltl + ")");
      if (DEBUG) {
        Writer<String> w = Writer.getWriter (Writer.Format.FSP, System.out);
        System.out.println("(DEBUG) InfiniteListener: the automaton is :");
        w.write(translate);
      }
    } catch (ParseErrorException ex) {
      Logger.getLogger(InfiniteListener.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    if (search != null) {
      search.setSpec(translate, ltl, ci.getName(), DEBUG);
    } else {
      saveSpec = translate;
      specText = ltl;
      specSource = ci;
    }
    
    //Just extracting the list of atoms
    for (Iterator<Node<String>> it = saveSpec.getNodes().iterator(); it.hasNext();) {
      Node n = it.next();
      for (Iterator iter = n.getOutgoingEdges().iterator(); iter.hasNext();) {
        Edge e = (Edge) iter.next();
        if (!e.getGuard().isEmpty()) {
              Iterator<Literal<String>> itr = e.getGuard().iterator();
              while (itr.hasNext()) {
                Literal<String> a = itr.next();
                
                /*
                String str = a.getAtom().toString();
                System.out.println(str);

                if ((str.contains("<"))) {
                  String nameVar = str.substring(0, str.indexOf('<'));
                  int valueVar = Integer.parseInt(str.substring(str.indexOf('<')+1, str.length()));
                  System.out.println(nameVar + " @@@@@@@ " + valueVar);
                }else{*/

                String bytecodeMethodName = translateToByteCode(a.getAtom().toString());
                LTLatoms.add(bytecodeMethodName);
                //}
              }
        }
      }
    }

  }

  @Override
  public void searchStarted(Search search) {
    assert search instanceof DDFSearch : "this listener only works with DDFSearch";
    this.search = (DDFSearch) search;
    if (saveSpec != null) {
      this.search.setSpec(saveSpec, specText, specSource.getName(),DEBUG);
    }
    if(LTLatoms.isEmpty()){
     System.out.println("The formula does not contain atoms. Please check it.");
     System.exit(0);
    }
  }

  @Override
  public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {

    
     
    Instruction insn = instructionToExecute;
    
    /*Tramite questo if annidato possiamo recuperare il valore delle variabili
     * 
    if (insn.getMethodInfo().getLocalVars() != null) {

      LocalVarInfo[] vars = insn.getMethodInfo().getLocalVars();
      //System.out.println("____" + vars.length);
      for (int i = 0; i < vars.length; i++) {
        LocalVarInfo lv = vars[i];
        //System.out.println("name of the variable = "+ lv.getName() + " = " + lv.getSignature());
      }
    }


    */
    if (DEBUG) {
      System.out.println("#########################################################" + insn.toString());
      //System.out.println("____"+instruction.getMethodInfo().getLocalVars());
      /*if (insn instanceof ILOAD){
       System.out.println("@@@@@@@@@@@@@@@@@@@@@"+insn.getSourceLine());
       }*/
      if (insn instanceof GETSTATIC) {//static variables returned
        System.out.println("getSourceLineGETSTATIC = " + insn.getSourceLine());
      }
      if (insn instanceof ISTORE) {//Store int (and boolean) "value"
        System.out.println("getSourceLineISTORE = " + insn.getSourceLine());
      }
      if (insn instanceof ASTORE) {//String variables 
        System.out.println("getSourceLineASTORE = " + insn.getSourceLine());
      }
      if (insn instanceof PUTSTATIC) {//Set static field to "value" in a class
        System.out.println("getSourceLinePUTSTATIC = " + insn.getSourceLine());
      }
    }
    
    
    
    
    if (insn instanceof InvokeInstruction) {

      
      InvokeInstruction inst = (InvokeInstruction) insn;
   
      String methodName = inst.getInvokedMethodName(); //getKeyValuePairs()[Ljava/lang/String;
      String returnedParam = methodName.substring(methodName.indexOf(")") + 1, methodName.length()); //[Ljava/lang/String;
      try {
        methodName = methodName.replace(returnedParam, "");
      } catch (PatternSyntaxException e) {
        System.out.println(e.toString());
      }
      if (LTLatoms.contains(methodName)){
        List<Edge<String>> outgoingEdges = search.getCurrentNode().getOutgoingEdges();
        List<Edge> enabledEdges = new LinkedList<Edge>();
        if (!outgoingEdges.isEmpty()) {
          for (int k = 0; k < outgoingEdges.size(); k++) {
            if (evaluateGuard(outgoingEdges.get(k).getGuard(),methodName)){
              enabledEdges.add(outgoingEdges.get(k));
            }
          }
        }
        if (!enabledEdges.isEmpty()){
          ThreadInfo ti = currentThread;
          if (!ti.isFirstStepInsn()){
            EdgeCG cg = new EdgeCG("EdgeCG", enabledEdges);
            if (vm.setNextChoiceGenerator(cg)) {
              return;
            }
          } else {
            EdgeCG cg = vm.getCurrentChoiceGenerator("EdgeCG", EdgeCG.class);
            Edge g = cg.getNextChoice();
            search.setCurrentNode(g.getNext(), true);
          }
        }else{
          //to check
          vm.backtrack();
        }
      }          
    }
  }
  
  
  
  @Override
  public void choiceGeneratorSet(VM vm, ChoiceGenerator<?> newCG) {
    System.out.println("FRANCO: in choiceGeneratorSet"); 
    if (vm.getChoiceGenerator() == null ) return;
    if (vm.getChoiceGenerator().getId().equals("EdgeCG")) {
            System.out.println("FRANCO: in choiceGeneratorSet the id of cg is EdgeCG");
      // If the choice generator is of class EdgeCG it means we have created
      // it. In this case we ignore it.
      return;
    } else {
      System.out.println("FRANCO: in choiceGeneratorSet the id of the cg is: "+vm.getChoiceGenerator().getId());

      // Otherwise, it was created by JPF and not by our listener. We compute
      // all the enabled edges assuming that all the atoms are false
      List<Edge<String>> outgoingEdges = search.getCurrentNode().getOutgoingEdges();
      List<Edge> enabledEdges = new LinkedList<Edge>();
      if (!outgoingEdges.isEmpty()) {
        for (int k = 0; k < outgoingEdges.size(); k++) {
          if (evaluateGuard(outgoingEdges.get(k).getGuard(), "")) {
            enabledEdges.add(outgoingEdges.get(k));
          }
        }
      } //else???
      System.out.println("FRANCO: in choiceGeneratorSet N. of enabled edges = " + enabledEdges.size());
      if (!enabledEdges.isEmpty()) {
        EdgeCG cg = new EdgeCG("EdgeCG", enabledEdges);
        cg.setCascaded();
        vm.getChoiceGenerator().setPreviousChoiceGenerator(cg);
      }
    }
  }

  // Franco: this is not needed now.
  //  @Override
  //  public void instructionExecuted(JVM vm) {
  //    assert search != null;
  //    ThreadInfo ti = vm.getCurrentThread();
  //    if (methodMatchNoNegate) {
  //      ti.breakTransition();
  //      methodMatchNoNegate = false;
  //    }
  //  }

  
  /**
   * This recursive method return true if the examined transition is allowed.
   * 
   * Questo metodo ricorsivo ritorna true se la transizione in esame è permessa.
   * 
   * @param g
   * @param m
   * @return 
   */
  private boolean evaluateGuard (TreeSet g, String m){
    if (g.isEmpty()) {
      return true;
    }
    Literal lit = (Literal) g.first();
    if (translateToByteCode(lit.getAtom().toString()).equals(m)){
      if (lit.isNegated()) {
        return false;
      }else{
        /*
         * usiamo una variabile d'appoggio poichè al ritorno dalla ricorsione
         * la label della guardia tolta (g.first) sarebbe persa del tutto.
         */
        Object labelcopy = g.first();
        g.remove(g.first());
        boolean returnBool = evaluateGuard (g,m);
        g.add(labelcopy);
        return returnBool;
      }
    }else {
      if (!lit.isNegated()) {
        return false;
      }else{
        Object labelcopy = g.first();
        g.remove(g.first());
        boolean returnBool = evaluateGuard (g,m);
        g.add(labelcopy);
        return returnBool;
      }
    }
  }

  
  private String translateToByteCode(String atom) {
    ANTLRStringStream stream =
            new ANTLRStringStream(atom);
    DetectParamParser parser = new DetectParamParser(new CommonTokenStream(new DetectParamLexer(stream)));
    try {
      parser.detectParameters();
    } catch (RecognitionException ex) {
      Logger.getLogger(InfiniteListener.class.getName()).log(Level.SEVERE, null, ex);
    }
    //System.out.println("atoms"+p.getAtoms().toString());
    return translateInBytecodeMethod(atom, parser.getAtoms());
  }
  
  /**
   * This method tranlates the atoms written in the @LTLSpec annotation into 
   * bytecode instructions to make a comparison
   * 
   * Questo metodo traduce gli atomi scritti nell'annotazione @LTLSpec in 
   * istruzioni bytecode per poter fare un confronto.
   * 
   * @param atom
   * @param atomsVector
   * @return 
   */
  private String translateInBytecodeMethod(String atom, Vector<String> atomsVector) {
    boolean bool1 = true, bool2 = true, bool3 = true, bool4 = true,
            bool5 = true, bool6 = true, bool7 = true, bool8 = true;
    for (int i = 0; i < atomsVector.size(); i++) {
      char c = atomsVector.get(i).charAt(0);
      switch (c) {
        case 'i':
          if (atomsVector.get(i).equals("int")) {
            if (bool1) {
              atom = atom.replace("int", "I");
              bool1 = false;
            }
            break;
          }
        case 'l':
          if (atomsVector.get(i).equals("long")) {
            if (bool2) {
              atom = atom.replace("long", "J");
              bool2 = false;
            }
            break;
          }
        case 's':
          if (atomsVector.get(i).equals("ahort")) {
            if (bool3) {
              atom = atom.replace("short", "S");
              bool3 = false;
            }
            break;
          }
        case 'b':
          if (atomsVector.get(i).equals("byte")) {
            if (bool4) {
              atom = atom.replace("byte", "B");
              bool4 = false;
            }
            break;
          }
        case 'c':
          if (atomsVector.get(i).equals("char")) {
            if (bool5) {
              atom = atom.replace("char", "C");
              bool5 = false;
            }
            break;
          }
        case 'f':
          if (atomsVector.get(i).equals("float")) {
            if (bool6) {
              atom = atom.replace("float", "F");
              bool6 = false;
            }
            break;
          }
        case 'd':
          if (atomsVector.get(i).equals("double")) {
            if (bool7) {
              atom = atom.replace("double", "D");
              bool7 = false;
            }
            break;
          }
        case 'S':
          if (atomsVector.get(i).equals("String")) {
            if (bool8) {
              atom = atom.replace("String", "Ljava/lang/String;");
              bool8 = false;
            }
            break;
          }
        default:
          atom = atom.replaceFirst(atomsVector.get(i), "L" + specSource.getPackageName() + "/" + atomsVector.get(i) + ";");
          break;
      }
    }
    atom = atom.replace(" ", "");
    atom = atom.replace(",", "");
    return atom;
  }
}
