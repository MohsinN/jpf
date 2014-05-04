/**
 * 
 */
package gov.nasa.jpf.ltl.ddfs;

import java.util.LinkedList;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.AnnotationInfo;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DirectCallStackFrame;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.ltl.finite.LTLListener;
import gov.nasa.jpf.ltl.property.LTLProperty;
import gov.nasa.jpf.search.Search;
import gov.nasa.ltl.graph.Graph;

/**
 * @author Ewgenij Starostin
 * 
 */
public class Listener extends ListenerAdapter {
  protected int insnCount = 0;
  protected boolean atDirectCallStackFrame = false;
  protected LinkedList<Boolean> nonProgressBoundaryStack = new LinkedList<Boolean>();
  protected DDFSearch search = null;

  void setFirstStep() {
    insnCount = 0;
  }

  @Override
  public void executeInstruction(JVM vm) {
    // TODO? Kludge.
    if (!passedPropToLTLListener && prop != null
        && LTLListener.instance != null) {
      LTLListener.instance.setFields(prop.getFields());
      LTLListener.instance.updateFieldList(specSource);
      passedPropToLTLListener = true;
    }
    ThreadInfo ti = vm.getCurrentThread();
    if (ti.getStackFrame(ti.getStackDepth() - 1) instanceof DirectCallStackFrame) {
      // System.err.println ("eI: skipping DCSF");
      atDirectCallStackFrame = true;
      return;
    }
    switch (insnCount) {
    case 0:
      assert (vm.getStateId() == -1 && nonProgressBoundaryStack.isEmpty())
          || !nonProgressBoundaryStack.isEmpty();
      if (!nonProgressBoundaryStack.isEmpty()
          && nonProgressBoundaryStack.peek()) {
        insnCount = 1;
        break;
      }
      ti.setNextPC(ti.getPC());
      ti.skipInstruction();
      break;
    case 1:
      break;
    default:
      assert false;
    }
    // System.err.println ("eI[" + insnCount + "," + ti.getPC () + "]: "
    // + ti.getPC () + " @ " + ti.getPC ().getSourceLocation ());
  }

  @Override
  public void instructionExecuted(JVM vm) {
    assert search != null;
    ThreadInfo ti = vm.getCurrentThread();
    SystemState ss = vm.getSystemState();
    if (atDirectCallStackFrame) {
      atDirectCallStackFrame = false;
      return;
    }
    if (insnCount == 1) {
      if (ss.getNextChoiceGenerator() != null) {
        nonProgressBoundaryStack.push(true);
        search.noProgress();
        // System.err.println ("iE: No progress");
      } else {
        nonProgressBoundaryStack.push(false);
        ti.breakTransition();
        // System.err.println ("iE: breaking transition");
      }
    }
    insnCount++;
  }

  @Override
  public void stateBacktracked(Search search) {
    nonProgressBoundaryStack.pop();
  }

  @Override
  public void searchStarted(Search search) {
    assert search instanceof DDFSearch : "this listener only works with DDFSearch";
    this.search = (DDFSearch) search;
    if (saveSpec != null)
      this.search.setSpec(saveSpec, specText, specSource.getName());
    saveSpec = null;
  }

  protected Graph<String> saveSpec = null;
  protected String specText = null;
  protected ClassInfo specSource = null;
  protected LTLProperty prop = null;
  protected boolean passedPropToLTLListener = false;

  @Override
  public void classLoaded(JVM vm) {
    ClassInfo ci = vm.getClassInfo();
    AnnotationInfo ai;
    ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpec");
    if (ai == null)
      ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpecFile");
    // System.err.println ("cL: ci = " + ci + ", ai = " + ai);
    if (ai == null)
      return;
    if (prop != null) {
      // TODO: Warn?
      return;
    }
    String ltl = ai.valueAsString();
    prop = new LTLProperty(ltl, true, ai.getName().endsWith("File"), ci.getName());
    if (search != null) {
      search.setSpec(prop.getNegatedBuchi(), ltl, ci.getName());
    } else {
      saveSpec = prop.getNegatedBuchi();
      specText = ltl;
      specSource = ci;
    }
  }
}
