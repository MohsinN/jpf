package gov.nasa.jpf.ltl.ddfs;

import gov.nasa.ltl.graph.Edge;
import gov.nasa.ltl.trans.ParseErrorException;

import java.util.LinkedList;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.AnnotationInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.ltl.Antlr.DetectParamLexer;
import gov.nasa.jpf.ltl.Antlr.DetectParamParser;
import gov.nasa.jpf.ltl.finite.trans.Translator;
import gov.nasa.jpf.search.DFSearch;
import gov.nasa.jpf.search.Search;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graph.Literal;
import gov.nasa.ltl.trans.Formula;
import gov.nasa.ltl.trans.LTL2Buchi;
import gov.nasa.ltl.trans.Parser;

import java.util.Iterator;
import java.util.List;
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
public class FiniteListener extends ListenerAdapter {

  protected int insnCount = 0;
  protected boolean atDirectCallStackFrame = false, methodMatchNoNegate = false;
  protected LinkedList<Boolean> nonProgressBoundaryStack = new LinkedList<Boolean>();
  protected DFSearch search = null;
  protected Graph<String> saveSpec = null;
  protected String specText = null, edge;
  protected ClassInfo specSource = null;
  protected boolean passedPropToLTLListener = false, methodMatchNegate = false;
  protected gov.nasa.jpf.ltl.finite.trans.Graph translateFinite = null;
  VM vm;
  String ltl = null;

  @Override
  public void classLoaded(VM vm, ClassInfo loadedClass) {
    this.vm = vm;
    String isReverse = vm.getConfig().getProperty("finite");
    if (isReverse != null) {
      if (isReverse.equals("false")) {
        try {
          throw new EndException("Set finite=true for this Listener.");
        } catch (EndException ex) {
          Logger.getLogger(InfiniteListener.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    ClassInfo ci = loadedClass;
    AnnotationInfo ai;
    ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpec");
    if (ai == null) {
      ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpecFile");
    }
    // System.err.println ("cL: ci = " + ci + ", ai = " + ai);
    if (ai == null) {
      return;
    }
    if (ltl != null) {
      return;
    }
    ltl = ai.valueAsString();
    Formula<String> f = null;
    try {
      f = Parser.parse(ltl);
    } catch (ParseErrorException ex) {
      Logger.getLogger(FiniteListener.class.getName()).log(Level.SEVERE, null, ex);
    }
    translateFinite = Translator.translate(f);
    System.out.println("translateFinite.toString()"+translateFinite.toString());
  }

  @Override
  public void searchStarted(Search search) {
    System.out.println("Start FiniteListener");
    this.search = (DFSearch) search;
  }

  @Override
  public void executeInstruction(VM vm, ThreadInfo currentThread, Instruction instructionToExecute) {
  }

  @Override
  public void instructionExecuted(VM vm, ThreadInfo currentThread, Instruction nextInstruction, Instruction executedInstruction) {
  }
  
  @Override
  public void stateAdvanced(Search search){
    if (vm.isEndState()){
      //TODO: Paramtro dovrà essere il nodo corrente
      //per controllare se è accettante.
      translateFinite.getNode(0); 
    }
  }
  
  @Override
  public void stateBacktracked(Search search){
    
  }
}
