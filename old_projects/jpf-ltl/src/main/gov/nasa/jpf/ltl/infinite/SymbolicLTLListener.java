/**
 * 
 */
package gov.nasa.jpf.ltl.infinite;

import java.util.LinkedList;

import gov.nasa.jpf.Config;
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
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.ltl.finite.LTLProperty;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graphio.Writer;

public class SymbolicLTLListener extends ListenerAdapter {
	protected int insnCount = 0;
	protected boolean atDirectCallStackFrame = false;
	protected LinkedList<Boolean> nonProgressBoundaryStack = new LinkedList<Boolean>();

	protected DDFSearch search = null;

	protected Graph<String> saveSpec = null;
	protected String specText = null;
	protected ClassInfo specSource = null;
	protected LTLProperty prop = null;

	private boolean forcedVal = false;
	private boolean retainVal = false;

	void setFirstStep() {
		insnCount = 0;
	}

	@Override
	public void executeInstruction(JVM vm) {
		ThreadInfo ti = vm.getCurrentThread();

		System.err.println("-- executeInstruction: count=" + insnCount + ", stateId=" + vm.getStateId() + ", insn=" + ti.getPC() + ", source=" + ti.getPC().getSourceLocation() + ", pc=" + PathCondition.getPC(JVM.getVM()));

		if (ti.getStackFrame(ti.getStackDepth() - 1) instanceof DirectCallStackFrame) {
			System.err.println("-- executeInstruction: returned because of skipping DCSF");
			atDirectCallStackFrame = true;
			return;
		}

		switch (insnCount) {
			case 0:
				assert (vm.getStateId() == -1 && nonProgressBoundaryStack.isEmpty()) || !nonProgressBoundaryStack.isEmpty();
				System.err.println("-- executeInstruction: boundaryStack " + getFewNonProgressBoundaryStack());

				if (!nonProgressBoundaryStack.isEmpty() && nonProgressBoundaryStack.peek()) {
					System.err.println("-- executeInstruction: nonProgressBoundaryStack " + ", pc=" + PathCondition.getPC(vm));
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
	}

	@Override
	public void instructionExecuted(JVM vm) {
		ThreadInfo ti = vm.getCurrentThread();
		SystemState ss = vm.getSystemState();

		System.err.println("-- instructionExecuted: count=" + insnCount + ", stateId=" + vm.getStateId() + ", insn=" + ti.getPC() + ", source=" + ti.getPC().getSourceLocation() + ", pc=" + PathCondition.getPC(vm));

		if (atDirectCallStackFrame) {
			atDirectCallStackFrame = false;
			System.err.println("-- instructionExecuted: returned because of atDirectCallStackFrame=" + atDirectCallStackFrame);
			return;
		}

		if (insnCount == 1) {
			if (ss.getNextChoiceGenerator() != null) {
				nonProgressBoundaryStack.push(true);
				search.noProgress();

				System.err.println("-- instructionExecuted: No progress" + ", pc=" + PathCondition.getPC(vm) + ", cg=" + ss.getCurrentChoiceGenerator(ss.getNextChoiceGenerator()));
				// TODO: create dummy insn and set CG here
			}
			else {
				nonProgressBoundaryStack.push(false);
				ti.breakTransition();

				System.err.println("-- instructionExecuted: breaking transition" + ", pc=" + PathCondition.getPC(vm));
			}
		}
		insnCount++;
	}

	@Override
	public void stateBacktracked(Search search) {
		System.err.println("stateBacktracked: stateId=" + JVM.getVM().getStateId() + ", insn=" + JVM.getVM().getCurrentThread().getPC() + ", pc=" + PathCondition.getPC(JVM.getVM()));

		System.err.println("-- stateBacktracked: before boundaryStack " + getFewNonProgressBoundaryStack());
		nonProgressBoundaryStack.pop();
		System.err.println("-- stateBacktracked: after boundaryStack " + getFewNonProgressBoundaryStack());
	}

	@Override
	public void searchStarted(Search search) {
		System.err.println("searchStarted " + saveSpec + ", " + specText + ", " + specSource);

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
		System.out.println("classLoaded: " + ci + ", " + ai);
		if (ai == null) {
			return;
		}

		assert prop == null : "prop has been set already.";

		String ltl = ai.valueAsString();
		prop = new LTLProperty(ltl, true, ai.getName().endsWith("File"), ci.getName());
		System.out.println("ltl2buchi");
		prop.getActualAutomata().print();

		if (prop.getNegatedAutomata() != null) {
			System.out.println("Buchi translated by ltl2automata for infinite trace");
			prop.getNegatedAutomata().print();
		}
		Writer<String> v = Writer.getWriter(Writer.Format.SPIN, System.out);
		if (prop.getNegatedBuchi() != null) {
			System.out.println("Buchi translated by ltl2buchi");
			v.write(prop.getNegatedBuchi());

			String isShowBuchi = vm.getConfig().getProperty("show_buchi");
			if (isShowBuchi != null && isShowBuchi.equals("true")) {
				prop.showGraph(prop.getNegatedBuchi());
			}
		}

		if (search != null) {
			search.setSpec(prop.getNegatedBuchi(), ltl, ci.getName());
		}
		else {
			saveSpec = prop.getNegatedBuchi();
			specText = ltl;
			specSource = ci;
		}
	}

	private String getFewNonProgressBoundaryStack() {
		String st = "[";
		for (int i = 0; !nonProgressBoundaryStack.isEmpty() && i < 15 && i < nonProgressBoundaryStack.size(); ++i) {
			st += nonProgressBoundaryStack.get(i) + ", ";
		}
		st += "]";
		return st;
	}

	public static String invokedMethodName(JVM vm) {
		Instruction insn = vm.getLastInstruction();
		if (!(insn instanceof InvokeInstruction)) {
			return null;
		}
	    MethodInfo mi = ((InvokeInstruction) insn).getInvokedMethod();
	    return mi.getClassName() + "." + mi.getLongName();
	}

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
					|| BytecodeUtils.isMethodSymbolic(conf, mi.getFullName(), numberOfArgs, null)) {
				// get the original attribute of the system state to restore later
				retainVal = ss.getRetainAttributes();
				forcedVal = ss.isForced();
				// turn off concrete state matching
				ss.setForced(true);
				// make sure it stays turned off when a new state is created
				ss.retainAttributes(true);
			}
		}
		else if (insn instanceof ReturnInstruction) {
			MethodInfo mi = insn.getMethodInfo();
			ClassInfo ci = mi.getClassInfo();
			if (null != ci) {
				String className = ci.getName();
				String methodName = mi.getName();
				int numberOfArgs = mi.getNumberOfArguments();
				if (BytecodeUtils.isClassSymbolic(conf, className, mi, methodName)
						|| BytecodeUtils.isMethodSymbolic(conf, mi.getFullName(), numberOfArgs, null)) {
					// restore the prior attribute of system state
					ss.retainAttributes(retainVal);
					ss.setForced(forcedVal);
				}
			}
		}
	}
}
