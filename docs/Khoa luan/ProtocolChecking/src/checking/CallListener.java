package checking;

import java.io.PrintWriter;
import java.util.Stack;
import java.util.Vector;

import sequence_diagram.SequenceDiagram;
import fsm.*;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.jvm.AnnotationInfo;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.bytecode.BytecodeUtils;
import gov.nasa.jpf.symbc.bytecode.INVOKESTATIC;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;

/**
 * A jpf VM - Search listener and publisher that get all the events from the
 * main program then verifying according to a specified protocol represented in
 * a UML 2.0 sequence diagram that built-in in a finite state machine.
 * 
 * @author Phuc Nguyen Dinh 
 * @author Chung Tuyen Luu
 */
public class CallListener extends PropertyListenerAdapter {
	String className;
	String methodName;
	CheckingPath cp; // The current test method
	Vector<CheckingPath> allPaths = new Vector<CheckingPath>(); // All test
																// methods
	SequenceDiagram diagram;// The UML 2.0 sequence diagram
	FSMControl fsmControl; // The finite state machine
	Stack<CheckingPath> unFinishedPaths;
	String pc = null;
	private boolean retainVal = false;
	private boolean forcedVal = false;
	private boolean verboseMode;

	public CallListener(JPF jpf, SequenceDiagram seq, String methodFullName, boolean verboseMode) {
		jpf.addPublisherExtension(ConsolePublisher.class, this);
		diagram = seq;
		fsmControl = diagram.createFSMControl();
		this.verboseMode = verboseMode;
		if (verboseMode )fsmControl.displayDFA();
		int index = methodFullName.lastIndexOf('.');
		className = methodFullName.substring(0, index);
		methodName = methodFullName.substring(index + 1);
		unFinishedPaths = new Stack<CheckingPath>();
	}

	public void instructionExecuted(JVM vm) {
		Instruction insn = vm.getLastInstruction();
		ThreadInfo ti = vm.getLastThreadInfo();
		SystemState ss = vm.getSystemState();
		Config conf = vm.getConfig();

		/*
		 * Specify which is the test method and set it to current method if test
		 * methods nested in others, base on the stack depth we only consider
		 * the method with a stack depth
		 */
		if (insn instanceof InvokeInstruction) {
			if (insn.isCompleted(ti) && !ti.isInstructionSkipped()) {
				InvokeInstruction call = (InvokeInstruction) insn;
				String methodName = call.getInvokedMethodName();
				MethodInfo mi = call.getInvokedMethod();
				Object[] args = call.getArgumentValues(ti);
				String[] argTypeNames = call.getInvokedMethod(ti)
						.getArgumentTypeNames();
				ClassInfo ci = mi.getClassInfo();

				if (ci.getName().equals(className)
						&& mi.getName().equals(this.methodName)) {
					if ((BytecodeUtils.isClassSymbolic(conf, className, mi,
							methodName))
							|| BytecodeUtils.isMethodSymbolic(conf, mi
									.getFullName(), args.length, null)) {
						retainVal = ss.getRetainAttributes();
						forcedVal = ss.isForced();
						// turn off state matching
						ss.setForced(true);
						// make sure it stays turned off when a new state is
						// created
						ss.retainAttributes(true);

						cp = new CheckingPath(ci.getName() + "." + mi.getName(),
								args, argTypeNames, fsmControl);
						String symValues = getSymvalues(call, ti);
						cp.setSymValues(symValues);
						allPaths.add(cp);
					}					
				}

				/*
				 * Check an event then add them to current method and
				 * on-verifying the protocol
				 */
				String classNameWithoutPkg = ci.getName();
				if (classNameWithoutPkg.contains("."))
					classNameWithoutPkg = classNameWithoutPkg
							.substring(classNameWithoutPkg.lastIndexOf('.') + 1);

				if (diagram.isMessage(classNameWithoutPkg, mi.getName())
						&& cp != null) {
					StackFrame stack = ti.getCallerStackFrame();
					int caller = stack.getCalleeThis(mi); // Get the objectId of
					// this event
					cp.addEvent(new Event(ti.getIndex(), caller, mi.isStatic(),
							mi.getName(), classNameWithoutPkg, insn
									.getLineNumber(), insn.getFileLocation()));
				}
			}
		} else if (insn instanceof ReturnInstruction) {
			MethodInfo mi = insn.getMethodInfo();
			ClassInfo ci = mi.getClassInfo();
			if (ci != null) {
				String className = ci.getName();
				String methodName = mi.getName();
				int numberOfArgs = mi.getNumberOfArguments();
				if (((BytecodeUtils.isClassSymbolic(conf, className, mi,
						methodName)) || BytecodeUtils.isMethodSymbolic(conf, mi
						.getFullName(), numberOfArgs, null))) {
					ss.retainAttributes(retainVal);
					ss.setForced(forcedVal);
					ChoiceGenerator<?> cg = vm.getChoiceGenerator();
					if (!(cg instanceof PCChoiceGenerator)) {
						ChoiceGenerator<?> prev_cg = cg
								.getPreviousChoiceGenerator();
						while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
							prev_cg = prev_cg.getPreviousChoiceGenerator();
						}
						cg = prev_cg;
					}
					if ((cg instanceof PCChoiceGenerator && cp != null)
							&& ((PCChoiceGenerator) cg).getCurrentPC() != null) {
						PathCondition pathCondition = ((PCChoiceGenerator) cg)
								.getCurrentPC();
						pathCondition.solve();
						pc = pathCondition.toString();
						cp.addPathCondition(pc);
						cp.isDone = true;
					}
				}
			}
		}
	}

	public void stateBacktracked(Search search) {
		JVM vm = search.getVM();
		ChoiceGenerator<?> cg = vm.getChoiceGenerator();
		if (cg instanceof PCChoiceGenerator && cg.hasMoreChoices()
				&& !unFinishedPaths.isEmpty()) {
			cp = unFinishedPaths.pop();
		}
	}

	public void choiceGeneratorAdvanced(JVM jvm) {
		ChoiceGenerator<?> cg = jvm.getChoiceGenerator();
		if (cg instanceof PCChoiceGenerator && cg.hasMoreChoices()
				&& cp != null) {
			CheckingPath current = cp.clone();
			unFinishedPaths.push(current);
			allPaths.add(current);
		}
	}

	/**
	 * Check for not yet finished state then publish results
	 */
	public void publishFinished(Publisher publisher) {
		PrintWriter pw = publisher.getOut();
		if (allPaths.isEmpty())
			return;
		publisher.publishTopicStart("method under test: " + className + "."
				+ methodName);
		Vector<CheckingPath> validPath = new Vector<CheckingPath>();
		Vector<CheckingPath> invalidPath = new Vector<CheckingPath>();
		for (int i = 0, j; i < allPaths.size(); i++) {
			CheckingPath cp = allPaths.get(i);
			if (cp.pathCondition == null)
				continue;
			for (j = i - 1; j >= 0; j--)
				if (allPaths.get(j).pathCondition != null
						&& cp.toString().equals(allPaths.get(j).toString()))
					break;
			if (j == -1 && cp.isError()) {
				invalidPath.add(cp);
			} else if (j == -1)
				validPath.add(cp);
		}
		if (!invalidPath.isEmpty()) {
			pw.println("\n----- Error protocol test cases -----\n");
			for (CheckingPath failedTestCase : invalidPath)
				failedTestCase.publish(pw);
		}
		if (!validPath.isEmpty()) {
			pw.println("\n----- Valid protocol test cases -----\n");
			for (CheckingPath passedTestCase : validPath)
				passedTestCase.publish(pw);
		}
		fsmControl.coveragePublish();
	}
	
	private String getSymvalues(InvokeInstruction call, ThreadInfo ti) {
		Object[] args = call.getArgumentValues(ti);
		String [] argTypeNames = call.getInvokedMethod(ti).getArgumentTypeNames();
		StackFrame stack = ti.getTopFrame();
		String symVarName = "", symValues = "";

		String[] names = call.getInvokedMethod().getLocalVariableNames();

		int sfIndex;

		// TODO:
		// if debug option was not used when compiling the
		// class,
		// then we do not have names of the locals and need
		// to
		// use a different naming scheme
		// previous code was broken
		if (names == null)
			throw new RuntimeException(
					"ERROR: you need to turn debug option on");

		if (call instanceof INVOKESTATIC)
			sfIndex = 0;
		else
			sfIndex = 1; // do not consider implicit
		// parameter "this"

		for (int i = 0; i < args.length; i++) {
			Expression expLocal = (Expression) stack
					.getLocalAttr(sfIndex);
			if (expLocal != null) { // symbolic
				symVarName = expLocal.toString();
				symValues = symValues + symVarName + ",";
			} else
				symVarName = names[sfIndex] + "_CONCRETE" + ",";

			if (argTypeNames[i].equals("double")
					|| argTypeNames[i].equals("long"))
				sfIndex = sfIndex + 2;

			else
				sfIndex++;
		}

		// get rid of last ","
		if (symValues.endsWith(",")) {
			symValues = symValues.substring(0, symValues
					.length() - 1);
		}
		return symValues;
	}
}