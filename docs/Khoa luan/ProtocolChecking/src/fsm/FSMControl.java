package fsm;

import java.util.*;

/*
 * FSMControl class
 *  Each state machine consists of states, events, transitions
 *  Control add, edit, remove states,transitions and events of state machine
 *  Convert from nondeterministic finite automaton to derterministic finite automaton
 *	@author : Luu Chung Tuyen  
 */

public class FSMControl {
	private Hashtable<String, State> states; // states of machine
	private ArrayList<String> events; // events of machine
	private State firstState; // first State of machine
	private ArrayList<State> lastStates; // set of last (accepting) State of
	// Machine
	private State lastAddedState; // last added State into Machine
	private int numberOfTransitions; // number of Transitions
	private String xmiPath;
	public static boolean isDisplayed;

	/*
	 * Constructor
	 */
	public FSMControl(String xmiPath) {
		this.xmiPath = xmiPath;
		states = new Hashtable<String, State>();
		events = new ArrayList<String>();
		firstState = null;
		lastStates = new ArrayList<State>();
	}

	/*
	 * Construct machine from a first State
	 */
	public FSMControl(String xmiPath, State firstState) {
		this(xmiPath);
		states.put(firstState.getName(), firstState);
		this.firstState = firstState;
		lastAddedState = firstState;

	}

	/*
	 * add a new State into machine if this state existed in machine, ignore it
	 */
	public void addState(String stateName) {
		if (!states.containsKey(stateName)) {
			State newState = new State(stateName);
			states.put(stateName, newState);
			lastAddedState = newState;
		}
	}

	public void addState(State state) {
		states.put(state.getName(), state);
		lastAddedState = state;
	}

	/*
	 * Check if machine contains any state
	 */
	public boolean contains(State state) {
		if (states.containsKey(state.getName()))
			return true;
		return false;
	}

	/*
	 * return set of last state
	 */
	public ArrayList<State> getLastState() {
		return lastStates;
	}

	/*
	 * return first State
	 */
	public State getFirstState() {
		return firstState;
	}

	/*
	 * return number of Transitions
	 */
	public int getNumberOfTransitions() {
		return this.numberOfTransitions;
	}

	/*
	 * return set of States
	 */
	public Hashtable<String, State> getStates() {
		return states;
	}

	public State getStateByName(String name) {
		return states.get(name);

	}

	/*
	 * set the first state of machine
	 */
	public void setFirstState(String first) {
		firstState = states.get(first);
	}

	/*
	 * set a state as last (accepting) state
	 */
	public void setLastState(State state) {
		if (!lastStates.contains(state))
			lastStates.add(state);
	}

	public void setLastState(String lastStateName) {
		for (int i = 0; i < lastStates.size(); i++)
			if (lastStates.get(i).getName().equals(lastStateName))
				return;
		lastStates.add(states.get(lastStateName));
	}

	/*
	 * set the last added state as last state
	 */
	public void setLastState() {
		setLastState(lastAddedState);
	}

	/*
	 * add a new event, if this event existed, ignore it
	 */
	public void addEvent(String eventName) {
		if (!events.contains(eventName)) {
			events.add(eventName);
		}
	}

	/*
	 * add a new transition
	 */
	public void addTransition(String sourceState, String event,
			String targetState) {
		if (states.containsKey(sourceState)) {
			State source = states.get(sourceState);
			if (states.containsKey(targetState)) {
				State target = states.get(targetState);
				if (events.contains(event)) {
					source.addTransition(event, target);
					numberOfTransitions++;
				}
			}
		}
	}

	public void addTransition(State sourceState, String event, State targetState) {
		sourceState.addTransition(event, targetState);
		numberOfTransitions++;
	}

	/*
	 * remove a transition
	 */
	public void removeTransition(String sourceState, String event,
			String targetState) {
		if (states.containsKey(sourceState)) {
			State source = states.get(sourceState);
			source.removeTransition(event, targetState);
		}
	}

	/*
	 * display machine after convert NFA-->DFA
	 */
	public void displayDFA() {
		if (!isDisplayed) {
			System.out.println("---------------------DFA of sequence diagram "
					+ xmiPath + " -------------------------------\n");

			Enumeration<State> stateList = states.elements();
			while (stateList.hasMoreElements())
				stateList.nextElement().display();
			System.out.println("	Fist state: [" + firstState.getName() + "]");
			for (int i = 0; i < lastStates.size(); i++) {
				System.out.println("	Final state : ["
						+ lastStates.get(i).getName() + "]");
			}
			System.out.println(" 	Number of transition " + numberOfTransitions);
			System.out.println(" 	Number of states " + states.size());
			System.out
					.println("---------------------***-------------------------------\n");
			isDisplayed = true;
		}
	}

	/*
	 * display the non-deterministic finite automata
	 */
	public void displayNFA() {
		System.out.println("---------------------NFA of sequence diagram "
				+ xmiPath + " -------------------------------\n");
		int i = 0;
		while (states.containsKey(String.valueOf(i))) {
			(states.get(String.valueOf(i))).display();
			i++;
		}
		System.out.println("	Fist state: [" + firstState.getName() + "]");
		for (i = 0; i < lastStates.size(); i++) {
			System.out.println("	Final state : [" + lastStates.get(i).getName()
					+ "]");
		}
		System.out.println(" 	Number of transition " + numberOfTransitions);
		System.out.println(" 	Number of states " + states.size());
		System.out
				.println("---------------***-------------------------------\n");
	}

	/**
	 * check if this FSM is non-derteministic
	 */
	public boolean isNonderteministic() {
		Enumeration<State> stateList = states.elements();
		while (stateList.hasMoreElements()) {
			State state = stateList.nextElement();
			if (state.isNondeterministic()) {
				return true;
			}
		}
		return false;
	}

	public String getXmiPath() {
		return xmiPath;
	}

	/*
	 * Convert this machine from Non-derteministic finite automata to
	 * Deterministic finite automata if needed
	 */
	public FSMControl NFAtoDFA() {

		ArrayList<State> lastStates = this.getLastState();
		FiniteState firstFSMState = new FiniteState(this.getFirstState());

		FSMControl fsmControl = new FSMControl(xmiPath, firstFSMState);
		if (lastStates.contains(this.getFirstState()))
			fsmControl.setLastState(firstFSMState);
		Stack<FiniteState> fsmStates = new Stack<FiniteState>();
		fsmStates.add(firstFSMState);
		int count = 0;
		// Scanner s = new Scanner(System.in);
		while (!fsmStates.isEmpty()) {
			FiniteState tempState = fsmStates.pop();
			// System.out.println(" TEmp State = " + tempState.getName());
			// s.next();
			// System.out.println(tempState.getName());
			ArrayList<State> NFSMStates = tempState.getStates();
			ArrayList<String> FSMEvents = new ArrayList<String>();
			for (int i = 0; i < NFSMStates.size(); i++) {
				ArrayList<Event> events = NFSMStates.get(i).getEvents();
				for (int j = 0; j < events.size(); j++)
					if (!FSMEvents.contains(events.get(j).getName()))
						FSMEvents.add(events.get(j).getName());
			}
			for (int i = 0; i < FSMEvents.size(); i++) {
				String event = FSMEvents.get(i);
				FiniteState fsmState = new FiniteState();
				for (int j = 0; j < NFSMStates.size(); j++)
					if (NFSMStates.get(j).hasEvent(event)) {
						ArrayList<State> targetStates = NFSMStates.get(j)
								.getTargetState(event);
						for (int k = 0; k < targetStates.size(); k++) {
							count++;
							fsmState.addState(targetStates.get(k));
							if (lastStates.contains(targetStates.get(k))) {
								fsmState.setIsLastState();
								// System.out.println(" Last = " +
								// targetStates.get(k).getName());
							}
						}

					}
				// System.out.println("Add trans " + tempState.getName() +
				// " --> " + event + "-->" + fsmState.getName());
				fsmControl.addEvent(event);
				if (!fsmControl.contains(fsmState)) { // if this fsmState is not
					// in FSM --> push to
					// FSM
					fsmStates.push(fsmState);
					fsmControl.addState(fsmState);
					if (fsmState.isLastState())
						fsmControl.setLastState(fsmState);
					fsmControl.addTransition(tempState, event, fsmState);
				} else {

					fsmControl.addTransition(tempState, event, fsmControl
							.getStateByName(fsmState.getName()));
				}
			}
		}
		return fsmControl;
	}

	/*
	 * coverage published
	 */
	public void coveragePublish() {
		System.out.println("\n----------Coverage Published--------\n");
		Enumeration<State> stateList = states.elements();
		int count = 0;
		String allMissingTransitions = "";
		while (stateList.hasMoreElements()) {
			
			// System.out.println("More");
			State state = stateList.nextElement();
			ArrayList<Transition> transitions = state.getTransitions();
			
			for (Transition transition : transitions)
				if (!transition.isVisited()){
					allMissingTransitions += "\n\t" +transition.getTransitionName();
				count++;	
				}
		}
		if (count == 0)
			System.out.println("All message in the sequence diagram have been implemented!");
		else{
			System.out.println("The messages below have never been implemented or are not reachable");
			System.out.println(allMissingTransitions);
		}
		System.out.println("\n------------------------------------");
	}
}
