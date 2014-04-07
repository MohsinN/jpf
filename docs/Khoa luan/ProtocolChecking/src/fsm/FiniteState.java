package fsm;

import java.util.ArrayList;
/**
 * Class that represent an event
 * 
 * @author Phuc Nguyen Dinh
 * @author Chung Tuyen Luu
 * 
 */
public class FiniteState extends State {
	ArrayList<State> states;
	boolean isLastState;

	public FiniteState() {
		super("");
		states = new ArrayList<State>();
	}

	public FiniteState(String name) {
		super(name);
		states = new ArrayList<State>();
	}

	public FiniteState(State state) {
		this(state.getName());
		states.add(state);
	}

	public void addState(State state) {
		if (states.size() == 0)
			states.add(state);
		if (!states.contains(state)) {

			int i = 0;
			while (i < states.size()) {
				if (Integer.valueOf(state.getName()) > Integer.valueOf(states
						.get(i).getName()))
					i++;
				else
					break;
			}
			states.add(i, state);
		}
		this.stateName = states.get(0).getName();
		for (int k = 1; k < states.size(); k++)
			this.stateName += "," + states.get(k).getName();

	}

	public ArrayList<State> getStates() {
		return states;
	}

	public void setIsLastState() {
		isLastState = true;
	}

	public boolean isLastState() {
		return isLastState;
	}
	// public void addTransition(String event,State targetState){

	// }

}
