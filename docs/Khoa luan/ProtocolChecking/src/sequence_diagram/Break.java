package sequence_diagram;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fsm.FSMControl;

class Break extends CombinedFragment {
	Break(Element fragment, NodeList guiLinkList, NodeList guiNodeLinkList,
			Vector<Message> messageList) {
		super(fragment, guiLinkList, guiNodeLinkList, messageList);
	}

	void FSMProcess(FSMControl fsmControl) {
		if (getFirstMessageAfterFragment() != null) {
			fsmControl.removeTransition(getFirstMessageAfterFragment()
					.getSourceState(), getFirstMessageAfterFragment()
					.getEventName(), getFirstMessageAfterFragment()
					.getTargetState());
			for (String firstState : firstStates)
				fsmControl.addTransition(firstState,
						getFirstMessageAfterFragment().getEventName(),
						getFirstMessageAfterFragment().getTargetState());
		} else
			for (String firstState : firstStates)
				fsmControl.setLastState(firstState);
		fsmControl.setLastState(getLastMessage().getTargetState());
	}
}