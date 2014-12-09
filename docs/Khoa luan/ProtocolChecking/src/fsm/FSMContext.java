package fsm;

import java.util.ArrayList;
import java.util.Hashtable;
/**
 * Class that represent an event
 * 
 * @author Phuc Nguyen Dinh
 * @author Chung Tuyen Luu
 * 
 */
public class FSMContext implements Cloneable{
	private State currentState;
	private FSMControl fsmControl;
	private boolean isError;
	String errorString;
	Hashtable<Integer, String> lifelineName;

	public FSMContext(FSMControl fsmControl) {
		this.fsmControl = fsmControl;
		currentState = fsmControl.getFirstState();
		lifelineName = new Hashtable<Integer, String>();
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public State getCurrentState() {
		return currentState;
	}

	public boolean isError() {
		return isError;
	}
	

	public boolean dispatch(Event event) {
		/**
		 * Dispatch the current state and consider for objects with coincide
		 * class name
		 */
		isError = true;
		if (lifelineName.containsKey(event.objectId)) {
			event.objectName = (String) lifelineName.get(event.objectId);
			if (currentState.hasEvent(event.getEventName())) {
				currentState.dispatch(event.getEventName(), this);
				isError = false;
			}
		} else {
			ArrayList<String> objectNames = currentState
					.getExpectObjectName(event);
			for (int i = 0; i < objectNames.size(); i++) {
				String objectName = objectNames.get(i);
				if (!lifelineName.containsValue(objectName)) {
					event.objectName = objectName;
					currentState.dispatch(event.getEventName(), this);
					isError = false;
					lifelineName.put(event.objectId, objectName);
				}
			}
		}
		ArrayList<Event> expectEvents = currentState.getEvents();
		if (isError){ 
			errorString = "\tError method: \"" + event.className
					+ "." + event.name  + "\" at (" + event.location + ")"
					+ "\n\tExpected method: ";
			errorString += "\"" +expectEvents.get(0).getName() +"\"";
			for (int i = 1; i < expectEvents.size(); i++) errorString += " or \"" + expectEvents.get(i).getName() +"\"";
		}
		/*else if(isError){
			errorString = "\tInvalid begin state"  + " at (" + event.location + ")"
			+ "\n\tExpected method: ";
			errorString +=expectEvents.get(0).getName();
			
		}*/
		return isError;
	}
	public boolean isFinished() {
		if(!fsmControl.getLastState().contains(currentState)) {
			isError = true;
			errorString = "\tNot yet finished the sequence diagram";
			return false;
		}
		return true;
	}
	public String getErrorString() {
		return errorString;
	}
	
	public FSMContext clone() {
		FSMContext cloned = null;
		try {
			cloned = (FSMContext) super.clone();
		} catch (CloneNotSupportedException e) { 
			e.printStackTrace();
		}
		return cloned;
	}
}