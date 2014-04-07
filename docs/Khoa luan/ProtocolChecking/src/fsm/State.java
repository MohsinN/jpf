package fsm;

import java.util.*;

/*
 * State 
 * 	 Each state consists of transtions and events
 *	 @author: Luu Chung Tuyen
 */
public class State{
	protected String stateName; // name of this state
	protected ArrayList<Transition> transitions; // set of transitions
	protected ArrayList<Event> events; // set of events

	/*
	 * Constructor
	 */
	public State(String name) {
		stateName = name;
		transitions = new ArrayList<Transition>();
		events = new ArrayList<Event>();
	}

	/*
	 * return name of this state
	 */
	public String getName() {
		return stateName;
	}

	/*
	 * check if this state contains event.
	 */
	public boolean hasEvent(String event) {
		for (Event e:events)
			if (e.getName().equals(event))
				return true;
		return false;
	}
	public boolean hasEvent(Event event){
		if (events.contains(event))
			return true;
		return false;
	}
	/*
	 * return set of states which this state has transitions to.
	 */
	public ArrayList<State> getTargetState(String event) {
		ArrayList<State> targets = new ArrayList<State>();
		for (int i = 0; i < events.size(); i++)
			if (events.get(i).getName().equals(event)) {
				for (int j = 0; j < ((transitions.get(i)).getTargetState())
						.size(); j++)
					targets.add(((transitions.get(i)).getTargetState()).get(j));
			}
		return targets;
	}
	public ArrayList<State> getTargetState(Event event) {
		ArrayList<State> targets = new ArrayList<State>();
		for (int i = 0; i < events.size(); i++)
			if (events.get(i).equals(event)) {
				for (int j = 0; j < ((transitions.get(i)).getTargetState())
						.size(); j++)
					targets.add(((transitions.get(i)).getTargetState()).get(j));
			}
		return targets;
	}
	/*
	 * return set of events
	 */
	public ArrayList<Event> getEvents() {
		return events;
	}

	/*
	 * Check if this state is nondeterministic
	 */
	public boolean isNondeterministic() {
		for (int i = 0; i < transitions.size(); i++)
			if ((transitions.get(i)).getTargetState().size() > 1)
				return true;
		return false;
	}

	/*
	 * return set of expected state can be achieved by event
	 */
	public ArrayList<String> getExpectObjectName(Event event) {
		ArrayList<String> result = new ArrayList<String>();
		String eventName = event.className + "." + event.name;
		for (int i = 0; i < events.size(); i++) {
			String transition = events.get(i).getName();
			int index = transition.indexOf('.');
			if (transition.substring(index + 1).equals(eventName))
				result.add(transition.substring(0, index) + "." + event.className);
		}
		return result;
	}

	/*
	 * add a new transition to this state
	 */
	public void addTransition(String event, State target) {
		int pos = -1;
		for (Event e:events)
			if (e.getName().equals(event))
				pos = events.indexOf(e);
		if (pos == -1) {
			Transition tran = new Transition(this, target,event);
			events.add(new Event(event));
			transitions.add(tran);
		} else {
			(transitions.get(pos)).addTargetState(target);
		}
	}
	public void addTransition(Event event, State target) {
		int pos = events.indexOf(event);
		if (pos == -1) {
			Transition tran = new Transition(this, target,event.getName());
			events.add(event);
			transitions.add(tran);
		} else {
			(transitions.get(pos)).addTargetState(target);
		}
	}
	/*
	 * remove a transition from this state
	 */
	public void removeTransition(String event, String targetState) {
		for (int i = 0; i < events.size(); i++)
			if (events.get(i).getName().equals(event)) {
				if ((transitions.get(i)).getTargetState().size() == 1) {
					transitions.remove(i);
					events.remove(i);
				} else
					(transitions.get(i)).remove(targetState);
				break;
			}
	}
	public void removeTransition(Event event, String targetState) {
		for (int i = 0; i < events.size(); i++)
			if (events.get(i).equals(event)) {
				if ((transitions.get(i)).getTargetState().size() == 1) {
					transitions.remove(i);
					events.remove(i);
				} else
					(transitions.get(i)).remove(targetState);
				break;
			}
	}
	/*
	 * dispatch an event
	 */
	public void dispatch(String event, FSMContext fsmc) {
		int count = 0;
		int position = 0;
		for (int i = 0; i < events.size(); i++)
			if (events.get(i).getName().equals(event)) {
				count++;
				position = i;
			}
		if (count > 1)
			System.out.println("Not a deterministic finite automaton!");

		else
			(transitions.get(position)).execute(fsmc);
	}
	public void dispatch(Event event, FSMContext fsmc) {
		int count = 0;
		int position = 0;
		for (int i = 0; i < events.size(); i++)
			if (events.get(i).equals(event)) {
				count++;
				position = i;
			}
		if (count > 1)
			System.out.println("Not a deterministic finite automaton!");

		else
			(transitions.get(position)).execute(fsmc);
	}

	/*
	 * to String
	 */
	public String toString() {
		return stateName;
	}
	
	public ArrayList<Transition> getTransitions() {
		return transitions;
	}

	/*
	 * display all transition of this state
	 */
	public void display() {
		/*for (int i = 0; i < transitions.size(); i++) {
			Transition tran = transitions.get(i);
			for (int j = 0; j < tran.getTargetState().size(); j++)
				System.out.println("[" + tran.getSourceState().getName()
						+ "] --> " + events.get(i).getName() + " --> ["
						+ ((State) tran.getTargetState().get(j)).getName()
						+ "]");
		}*/
		for (Transition tran:transitions){
			tran.display();
		}
	}
}
