package checking;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import fsm.*;
import gov.nasa.jpf.jvm.Path;
import gov.nasa.jpf.util.Pair;

/**
 * Method class Represents a test method
 * 
 * @author Phuc Nguyen Dinh
 * @author Chung Tuyen Luu
 * 
 */
public class CheckingPath implements Cloneable {
	String name; // method name
	Event currentEvent;
	FSMContext fsmContext; // map a thread id with
	Hashtable<State, Boolean> exploredStates;
	FSMControl fsmControl; // finite state machine
	boolean isError;
	String errorString;
	String testCase;
	boolean isDone;

	String[] argTypes; // type of arguments
	/*
	 * Actual value of arguments even this method execute symbolically or
	 * concrete
	 */
	Object[] argValues;

	String symValues; // symbolic value which is not a real value
	String pathCondition;

	public CheckingPath(String name, Object[] args, String[] argTypes,
			FSMControl fsmControl) {
		this.name = name;
		this.fsmControl = fsmControl;
		this.argValues = args;
		this.argTypes = argTypes;
		fsmContext = new FSMContext(fsmControl);
		initExploredStates();
	}

	public String[] getArgTypes() {
		return argTypes;
	}

	public void setArgTypes(String[] argTypes) {
		this.argTypes = argTypes;
	}

	public Object[] getArgValues() {
		return argValues;
	}

	public void setArgValues(Object[] argValues) {
		this.argValues = argValues;
	}

	public String getSymValues() {
		return symValues;
	}

	public void setSymValues(String symValues) {
		this.symValues = symValues;
	}

	void addPathCondition(String pc) {
		if (pathCondition == null && pc.contains("SYM")) {
			pathCondition = pc;
		}
	}

	/**
	 * Add event to event list and on-verifying check the protocol
	 */
	public void addEvent(Event event) {
		if (isDone)
			return;
		if (!fsmContext.isError()) { // if this fsm context is error former
			// then we stop verify
			exploredStates.put(fsmContext.getCurrentState(), true);
			fsmContext.dispatch(event);
		}
		currentEvent = event;
	}

	/**
	 * Check for not finished state in all thread then publish the verify result
	 * of this method
	 * 
	 * @param pw
	 */
	public void publish(PrintWriter pw) {
		pw.println(toString());
		if (isError)
			pw.println(errorString);
	}

	public CheckingPath clone() {
		CheckingPath cloned = null;
		try {
			cloned = (CheckingPath) super.clone();
			cloned.currentEvent = currentEvent;
			cloned.fsmContext = fsmContext.clone();
			cloned.exploredStates = exploredStates;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return cloned;
	}

	public String getTestCase() {
		String result = null;
		if (pathCondition != null) {
			String pc = pathCondition;
			result = name + "(";
			StringTokenizer st = new StringTokenizer(symValues, ",");
			for (int i = 0; i < argValues.length; i++) {
				String token = "";
				String actualValue = argValues[i].toString();
				String actualType = argTypes[i];
				if (st.hasMoreTokens())
					token = st.nextToken();
				if (pc.contains(token)) {
					String temp = pc.substring(pc.indexOf(token));
					String val = temp.substring(temp.indexOf("[") + 1, temp
							.indexOf("]"));
					if (actualType.equalsIgnoreCase("int")
							|| actualType.equalsIgnoreCase("float")
							|| actualType.equalsIgnoreCase("long")
							|| actualType.equalsIgnoreCase("double")
							|| actualType.equalsIgnoreCase("short")
							|| actualType.equalsIgnoreCase("byte")
							|| actualType.equalsIgnoreCase("string"))
						result = result + val + ",";
					else if (actualType.equalsIgnoreCase("char")) {
						result = result
								+ "'"
								+ String.valueOf(Character
										.valueOf((char) Integer.valueOf(val)
												.intValue())) + "'";
					} else { // translate boolean values represented as ints
						// to "true" or "false"
						if (val.equalsIgnoreCase("0"))
							result = result + "false" + ",";
						else if (val.equalsIgnoreCase("1")) {
							result = result + "true" + ",";
						} else
							result = result + actualValue + ",";
					}
				} else
					result = result + actualValue + ",";

			}
			if (result.endsWith(","))
				result = result.substring(0, result.length() - 1);
			result = result + ")";

		}
		return result;
	}

	private void initExploredStates() {
		exploredStates = new Hashtable<State, Boolean>();
		Hashtable<String, State> states = fsmControl.getStates();
		Collection<State> stateRefs = states.values();
		for (State state : stateRefs)
			exploredStates.put(state, false);
	}

	boolean isError() {
		if (fsmContext.isError()) { // check for protocol error
			errorString = fsmContext.getErrorString() + "\n";
			isError = true;
		} else { // check for not yet finished state machine
			if (!fsmContext.isFinished()) {
				errorString = fsmContext.getErrorString()
						+ "\n\tThe last method detected in the program is: \""
						+ currentEvent.getClassName() + "."
						+ currentEvent.getName() + "\" at ("
						+ currentEvent.getLocation() + ")\n";
				isError = true;
			}
		}
		return isError;
	}

	public String toString() {
		if (testCase == null)
			testCase = getTestCase();
		return testCase;
	}
}