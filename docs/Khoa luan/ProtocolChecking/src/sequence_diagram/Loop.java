package sequence_diagram;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fsm.FSMControl;

class Loop extends CombinedFragment {
	String minGuard;
	Vector<String> endStates;

	Loop(Element loop, NodeList guiLinkList, NodeList guiNodeLinkList,
			Vector<Message> messageList) {
		super(loop, guiLinkList, guiNodeLinkList, messageList);
		NodeList guard = loop.getElementsByTagName("minint");
		Element minint = (Element) guard.item(0);
		minGuard = minint.getAttribute("value");
		endStates = new Vector<String>();
	}
	
	void addEndState(String state) {
		if(!endStates.contains(state))
			endStates.add(state);
	}

	void FSMProcess(FSMControl fsmControl) {
		endStates.add(getLastMessage().getTargetState());
		for (String endState : endStates)
			fsmControl.addTransition(endState,
					getFirstMessage().getEventName(), getFirstMessage()
							.getTargetState());
		
		if (getFirstMessageAfterFragment() != null && minGuard.equals("0"))
			for (String firstState : firstStates)
				fsmControl.addTransition(firstState,
						getFirstMessageAfterFragment().getEventName(),
						getFirstMessageAfterFragment().getTargetState());
		else
			for (String firstState : firstStates)
				fsmControl.setLastState(firstState);
	}
}