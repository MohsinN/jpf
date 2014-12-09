package sequence_diagram;

import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fsm.FSMControl;
import fsm.State;

class CombinedFragment {
	String id;
	Vector<Message> messages;
	Message firstMessageAfterFragment;
	int top, right, left, bottom;
	
	Vector<String> firstStates;

	CombinedFragment(Element fragment, NodeList guiLinkList,
			NodeList guiNodeLinkList, Vector<Message> messageList) {
		id = fragment.getAttribute("xmi:id");
		firstStates = new Vector<String>();
		setMessage(guiLinkList, guiNodeLinkList, messageList);
	}

	void setMessage(NodeList guiLinkList, NodeList guiNodeLinkList,
			Vector<Message> messageList) {
		messages = new Vector<Message>();
		Element combinedFragment = getElementFromDiagramList(guiLinkList);
		if (combinedFragment == null)
			combinedFragment = getElementFromDiagramList(guiNodeLinkList);

		if (combinedFragment != null) {
			NodeList list = combinedFragment.getElementsByTagName("nodeRect");
			Element nodeRect = (Element) list.item(0);
			left = Integer.parseInt(nodeRect.getAttribute("Left"));
			top = Integer.parseInt(nodeRect.getAttribute("Top"));
			right = Integer.parseInt(nodeRect.getAttribute("Right"));
			bottom = Integer.parseInt(nodeRect.getAttribute("Bottom"));

			for (Message message : messageList) {
				if (isInRect(message))
					messages.add(message);
			}
			Message last = messages.lastElement();
			for (int i = messageList.indexOf(last) + 1; i < messageList.size(); i++)
				if (!messageList.get(i).isReply()) {
					firstMessageAfterFragment = messageList.get(i);
					break;
				}
		}
	}

	boolean isInRect(Message message) {
		int x = message.begin.x;
		int y = message.begin.y;
		if (x >= left && x <= right && y >= top && y <= bottom)
			return true;
		return false;
	}

	Message getFirstMessage() {
		for (Message message : messages)
			if (!message.isReply())
				return message;
		return null;
	}

	Message getLastMessage() {
		for (int i = messages.size() - 1; i >= 0; i--)
			if (!messages.get(i).isReply())
				return messages.get(i);
		return null;
	}

	Message getFirstMessageAfterFragment() {
		return firstMessageAfterFragment;
	}
	
	void setFirstStates() {
		addFirstState(getFirstMessage().getSourceState());
	}
	
	void addFirstState(String state) {
		if(state != null && !firstStates.contains(state))
			firstStates.add(state);
	}
	
	/*
	 * return true if this combined fragment is above cf 
	 */
	boolean isHigher(CombinedFragment cf) {
		return bottom <= cf.top;
	}

	private Element getElementFromDiagramList(NodeList guiLinkList) {
		Element combinedFragment = null;
		for (int i = 0; i < guiLinkList.getLength(); i++) {
			Element element = (Element) guiLinkList.item(i);
			if (element.hasAttribute("guiLink_Element"))
				if (element.getAttribute("guiLink_Element").equals(id)) {
					combinedFragment = element;
					break;
				}
		}
		return combinedFragment;
	}

	void FSMProcess(FSMControl fsmControl) {

	}
}