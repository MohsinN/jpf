package sequence_diagram;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fsm.FSMControl;

class Opt extends CombinedFragment {
	Opt(Element opt, NodeList guiLinkList, NodeList guiNodeLinkList,
			Vector<Message> messageList) {
		super(opt, guiLinkList, guiNodeLinkList, messageList);
	}

	void FSMProcess(FSMControl fsmControl) {
		if (getFirstMessageAfterFragment() != null) {
			for (String firstState : firstStates)
				fsmControl.addTransition(firstState,
						getFirstMessageAfterFragment().getEventName(),
						getFirstMessageAfterFragment().getTargetState());
		} else
			for (String firstState : firstStates)
				fsmControl.setLastState(firstState);
	}
}