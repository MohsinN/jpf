/**
 * 
 */
package gov.nasa.jpf.ltl.infinite;

import java.util.LinkedList;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.AnnotationInfo;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.DirectCallStackFrame;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.ltl.finite.LTLListener;
import gov.nasa.jpf.ltl.finite.LTLProperty;
import gov.nasa.jpf.search.Search;
import gov.nasa.ltl.graph.Graph;

public class SymbolicLTLListener extends ListenerAdapter {
	protected int insnCount = 0;
	protected boolean atDirectCallStackFrame = false;
	protected LinkedList<Boolean> nonProgressBoundaryStack = new LinkedList<Boolean>();

	protected DDFSearch search = null;

	protected Graph<String> saveSpec = null;
	protected String specText = null;
	protected ClassInfo specSource = null;
	protected LTLProperty prop = null;
	protected boolean passedPropToLTLListener = false;

	void setFirstStep() {
		insnCount = 0;
	}

	@Override
	public void executeInstruction(JVM vm) {
		ThreadInfo ti = vm.getCurrentThread();
		if (ti.getStackFrame(ti.getStackDepth() - 1) instanceof DirectCallStackFrame) {
			// System.err.println ("eI: skipping DCSF");
			atDirectCallStackFrame = true;
			return;
		}

		switch (insnCount) {
			case 0:
				assert (vm.getStateId() == -1 && nonProgressBoundaryStack.isEmpty()) || !nonProgressBoundaryStack.isEmpty();

				if (!nonProgressBoundaryStack.isEmpty() && nonProgressBoundaryStack.peek()) {
					insnCount = 1;
					break;
				}
				ti.setNextPC(ti.getPC());
				ti.skipInstruction();
				break;

			case 1:
				break;

			default:
				assert false : "Instruction count is more than 1 insnCount=" + insnCount;
				break;
		}

		System.err.println ("eI[" + insnCount + ", " + ti.getPC () + "]: " + ti.getPC ().getSourceLocation());
	}

	@Override
	public void instructionExecuted(JVM vm) {
		assert search != null : "Search is not definded yet.";

		if (atDirectCallStackFrame) {
			atDirectCallStackFrame = false;
			return;
		}

		if (insnCount == 1) {
			SystemState ss = vm.getSystemState();
			if (ss.getNextChoiceGenerator() != null) {
				nonProgressBoundaryStack.push(true);
				search.noProgress();

				System.err.println("iE: No progress");
			}
			else {
				nonProgressBoundaryStack.push(false);

				ThreadInfo ti = vm.getCurrentThread();
				ti.breakTransition();

				System.err.println("iE: breaking transition");
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
		assert search instanceof DDFSearch : "This listener only works with DDFSearch search=" + search;

		this.search = (DDFSearch) search;
		if (saveSpec != null) {
			this.search.setSpec(saveSpec, specText, specSource.getName());
		}
		saveSpec = null;
	}

	@Override
	public void classLoaded(JVM vm) {
		ClassInfo ci = vm.getClassInfo();
		AnnotationInfo ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpec");
		if (ai == null) {
			ai = ci.getAnnotation("gov.nasa.jpf.ltl.LTLSpecFile");
		}
		if (ai == null) {
			return;
		}

		assert prop == null : "prop has been set already.";

		String ltl = ai.valueAsString();
		prop = new LTLProperty(ltl, true, ai.getName().endsWith("File"), ci.getName());
		if (search != null) {
			search.setSpec(prop.getNegatedBuchi(), ltl, ci.getName());
		}
		else {
			saveSpec = prop.getNegatedBuchi();
			specText = ltl;
			specSource = ci;
		}
	}

	public static String invokedMethodName(JVM vm) {
		Instruction insn = vm.getLastInstruction();
		if (!(insn instanceof InvokeInstruction)) {
			return null;
		}
	    MethodInfo mi = ((InvokeInstruction) insn).getInvokedMethod();
	    return mi.getClassName() + "." + mi.getLongName();
	}
}
