package sequence_diagram;

import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fsm.FSMControl;

class Alt extends CombinedFragment {
	Vector<Message> ifMessage;
	Vector<Message> elseMessage;
	int separator;
	boolean isEndOfDiagram;

	Alt(Element alt, NodeList guiLinkList, NodeList guiNodeLinkList,
			Vector<Message> messageList) {
		super(alt, guiLinkList, guiNodeLinkList, messageList);
	}

	void setMessage(NodeList guiLinkList, NodeList guiNodeLinkList,
			Vector<Message> messageList) {
		ifMessage = new Vector<Message>();
		elseMessage = new Vector<Message>();

		Element alt = null;
		for (int i = 0; i < guiLinkList.getLength(); i++) {
			Element element = (Element) guiLinkList.item(i);
			if (element.hasAttribute("guiLink_Element"))
				if (element.getAttribute("guiLink_Element").equals(id))
					alt = element;
		}

		if (alt != null) {
			NodeList list = alt.getElementsByTagName("nodeRect");
			Element nodeRect = (Element) list.item(0);
			left = Integer.parseInt(nodeRect.getAttribute("Left"));
			top = Integer.parseInt(nodeRect.getAttribute("Top"));
			right = Integer.parseInt(nodeRect.getAttribute("Right"));
			bottom = Integer.parseInt(nodeRect.getAttribute("Bottom"));

			NodeList sepList = alt.getElementsByTagName("Separators");
			NodeList separators = ((Element) sepList.item(0))
					.getElementsByTagName("separator");
			Element sep = (Element) separators.item(0);
			separator = Integer.parseInt(sep.getAttribute("position")) + top;

			for (Message message : messageList) {
				if (isInRect(message)) {
					if (isInIf(message))
						ifMessage.add(message);
					else
						elseMessage.add(message);
				}
			}

			Message last = elseMessage.lastElement();
			for (int i = messageList.indexOf(last) + 1; i < messageList.size(); i++)
				if (!messageList.get(i).isReply()) {
					firstMessageAfterFragment = messageList.get(i);
					break;
				}

			if (messageList.lastElement().equals(elseMessage.lastElement()))
				isEndOfDiagram = true;
		}
	}

	boolean isInIf(Message message) {
		int y = message.begin.y;
		if (y <= separator)
			return true;
		return false;
	}

	Message getFirstIfMessage() {
		for (Message message : ifMessage)
			if (!message.isReply())
				return message;
		return null;
	}

	Message getFirstElseMessage() {
		for (Message message : elseMessage)
			if (!message.isReply())
				return message;
		return null;
	}

	Message getLastIfMessage() {
		for (int i = ifMessage.size() - 1; i >= 0; i--)
			if (!ifMessage.get(i).isReply())
				return ifMessage.get(i);
		return null;
	}

	Message getLastElseMessage() {
		for (int i = elseMessage.size() - 1; i >= 0; i--)
			if (!elseMessage.get(i).isReply())
				return elseMessage.get(i);
		return null;
	}

	Message getFirstMessage() {
		return getFirstIfMessage();
	}

	Message getLastMessage() {
		return getLastElseMessage();
	}

	boolean isEndOfDiagram() {
		return isEndOfDiagram;
	}

	void FSMProcess(FSMControl fsmControl) {
		fsmControl.removeTransition(getFirstElseMessage().getSourceState(),
				getFirstElseMessage().getEventName(), getFirstElseMessage()
						.getTargetState());
		for (String firstState : firstStates)
			fsmControl.addTransition(firstState, getFirstElseMessage()
					.getEventName(), getFirstElseMessage().getTargetState());
		if (getFirstMessageAfterFragment() != null)
			fsmControl.addTransition(getLastIfMessage().getTargetState(),
					getFirstMessageAfterFragment().getEventName(),
					getFirstMessageAfterFragment().getTargetState());
		if (isEndOfDiagram()) {
			fsmControl.setLastState(getLastIfMessage().getTargetState());
			fsmControl.setLastState(getLastElseMessage().getTargetState());
		}
	}
}