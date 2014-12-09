package sequence_diagram;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class Message implements Comparable<Message>{
	private String id, name;
	private String sendEventId, receiveEventId;
	private String messageSort;
	String className;
	Lifeline from, to;
	String sourceState, targetState;
	Point begin, end;	
	private boolean isMultiObject, isStatic;
	String getSourceState() {
		return sourceState;
	}

	void setSourceState(String sourceState) {
		this.sourceState = sourceState;
	}

	String getTargetState() {
		return targetState;
	}

	void setTargetState(String targetState) {
		this.targetState = targetState;
	}

	Message(Element message) {
		id = message.getAttribute("xmi:id");
		name = message.getAttribute("name");
		if(name.indexOf('(') > 0)
			name = name.substring(0, name.indexOf('('));
		sendEventId = message.getAttribute("sendEvent");
		receiveEventId = message.getAttribute("receiveEvent");
		
		NodeList list = message.getElementsByTagName("attributeExtension");
		for(int i = 0; i < list.getLength(); i++) {
			if(((Element)list.item(i)).hasAttribute("messageSort"))
				messageSort = ((Element)list.item(i)).getAttribute("messageSort");
		}
	}
	
	void setClassName(Vector<Lifeline> lifelineList, Vector<MessageOccurSpec> occurList) {
		String toId = "", fromId = "";
		boolean isToDone = false, isFromDone = false;
		for(MessageOccurSpec mos: occurList) {
			if(isToDone && isFromDone)
				break;
			if(mos.getId().equals(receiveEventId)) {
				toId = mos.getLifelineCoveredId();
				isToDone = true;
			}
			else if(mos.getId().equals(sendEventId)) {
				fromId = mos.getLifelineCoveredId();
				isFromDone = true;
			}
		}
		isToDone = isFromDone = false;
		for(Lifeline lifeline: lifelineList) {
			if(isToDone && isFromDone)
				break;
			if(lifeline.getId().equals(toId)) {
				to = lifeline;
				className = to.getClassName();
				isMultiObject = to.isMultiObject();
				isToDone = true;
			}
			else if(lifeline.getId().equals(fromId)) {
				from = lifeline;
				isFromDone = true;
			}
		}
	}
	
	void setPosition(Element element) {
		NodeList pointList = element.getElementsByTagName("guiLineLinkWaypoint");
		Element endPoint = (Element)pointList.item(1);
		NodeList list = endPoint.getElementsByTagName("pos");
		Element pos = (Element)list.item(0);
		int x = Integer.parseInt(pos.getAttribute("X"));
		int y = Integer.parseInt(pos.getAttribute("Y"));
		end = new Point(x,y);
		Element beginPoint = (Element)pointList.item(0);
		list = beginPoint.getElementsByTagName("pos");
		pos = (Element) list.item(0);
		x = Integer.parseInt(pos.getAttribute("X"));
		y = Integer.parseInt(pos.getAttribute("Y"));
		begin = new Point(x, y);
	}
	
	boolean isReply() {
		if(messageSort != null)
			return messageSort.equals("reply");
		return false;
	}
	
	boolean isCreation() {
		if(messageSort != null)
			return messageSort.equals("createMessage");
		return false;
	}
	boolean isStatic() {
		return isStatic;
	}
	String getId() {
		return id;
	}
	
	String getName() {
		if(isCreation())
			return "<init>";
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
		
	String getMessageSort() {
		return messageSort;
	}
	
	String getClassName() {
		return className;
	}
	
	boolean isMultiObject() {
		return isMultiObject;
	}
	
	String getEventName() {
		if(isCreation())
			return to.objectName + "." + className + ".<init>";
		return to.objectName + "." + className + "." + name;
	}

	@Override
	public int compareTo(Message message) {
		if(begin.y > message.begin.y)
			return 1;
		else if(begin.y < message.begin.y)
			return -1;
		return 0;
	}
}