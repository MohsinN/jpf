package fsm;

import java.util.ArrayList;
/**
 * Class that represent an event
 * 
 * @author Phuc Nguyen Dinh
 * @author Chung Tuyen Luu
 * 
 */
public class Transition {
	private State sourceState;
	private ArrayList<State> targetStates;
	private String transitionName;
	private boolean isVisited;

	public Transition(State sourceState, State targetState, String name) {
		this.sourceState = sourceState;
		targetStates = new ArrayList<State>();
		targetStates.add(targetState);
		transitionName = name;
	}

	public ArrayList<State> getTargetState() {
		return targetStates;
	}

	public void addTargetState(State state) {
		targetStates.add(state);
	}

	public State getSourceState() {
		return sourceState;
	}

	public void execute(FSMContext fsmc) {
		if (targetStates.size() > 1) {
			System.out.println("Not a deterministic state machine!");
		} else {
			fsmc.setCurrentState(targetStates.get(0));
			isVisited = true;
		}
	}

	public void remove(String targetState) {
		for (int i = 0; i < targetStates.size(); i++)
			if (targetStates.get(i).getName().equals(targetState)) {
				targetStates.remove(i);
				break;
			}

	}
	public String toString(){
		return "[" + sourceState.getName() + "] --> " + transitionName + " --> [" + targetStates.get(0).getName() +"]";
	}
	
	public String getTransitionName(){
		return transitionName;
	}
	public void display(){
		System.out.println(this);
	}
	public boolean isVisited() {
		return isVisited;
	}
}
