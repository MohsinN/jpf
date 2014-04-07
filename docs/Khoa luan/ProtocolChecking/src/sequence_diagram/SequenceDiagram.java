package sequence_diagram;

import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fsm.FSMControl;

/**
 * SequenceDiagram class Load an xmi file with a specified path name then build
 * a finite state machine.
 * 
 * @author Phuc Nguyen Dinh
 * 
 */
public class SequenceDiagram {

	/*
	 * Elements in the UML 2.0 sequence diagram
	 */
	private Vector<Lifeline> lifelines;
	private Vector<Message> messages;
	private Vector<MessageOccurSpec> MOSs;
	private CombinedFragmentTree combinedFragments;
	private String xmiPath;
	public SequenceDiagram(String xmiPath) {
		this.xmiPath = xmiPath;
		Document doc = load(xmiPath);
		lifelines = new Vector<Lifeline>();
		messages = new Vector<Message>();
		MOSs = new Vector<MessageOccurSpec>();
		combinedFragments = new CombinedFragmentTree();
		setLifelines(doc.getElementsByTagName("lifeline"));
		setMessages(doc.getElementsByTagName("message"), doc
				.getElementsByTagName("guiDiagramGuiLink"));
		setMOSs(doc.getElementsByTagName("fragment"));
		setClassNameForMessage();
		setCombinedFragment(doc.getElementsByTagName("fragment"), doc
				.getElementsByTagName("guiDiagramGuiLink"), doc
				.getElementsByTagName("guiNodeLinkGuiNodeLink"));
	}

	/**
	 * Parse an existing xmi file with the specified path file name
	 * 
	 * @param pathFileName
	 * @return org.w3c.dom.Document object received by parse the xmi file
	 */
	private Document load(String pathFileName) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = dbf.newDocumentBuilder();
			File xmiFile = new File(pathFileName);
			Document document = parser.parse(xmiFile);
			return document;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void setLifelines(NodeList list) {
		for (int i = 0; i < list.getLength(); i++) {
			Lifeline newLifeline = new Lifeline((Element) list.item(i));
			String className = newLifeline.getClassName();
			boolean isMultiObject = false;
			int count = 1;
			for (Lifeline lifeline : lifelines) {
				if (lifeline.getClassName().equals(className)) {
					lifeline.setMultiObject(true);
					lifeline.setObjectName("object" + String.valueOf(count));
					count++;
					isMultiObject = true;
				}
			}
			if (isMultiObject) {
				newLifeline.setMultiObject(true);
				newLifeline.setObjectName("object" + String.valueOf(count));
			}
			lifelines.add(newLifeline);
		}
	}

	private void setMessages(NodeList list, NodeList guiLinkList) {
		Hashtable<String, Message> messageList = new Hashtable<String, Message>();
		for (int i = 0; i < list.getLength(); i++) {
			Element element = (Element) list.item(i);
			if (element.hasAttribute("xmi:type")
					&& element.getAttribute("xmi:type").equals("uml:Message")) {
				Message message = new Message(element);
				messageList.put(message.getId(), message);
			}
		}
		for (int i = 0; i < guiLinkList.getLength(); i++) {
			Element element = (Element) guiLinkList.item(i);
			if (element.hasAttribute("guiLink_Element")) {
				String id = element.getAttribute("guiLink_Element");
				Message message = (Message) messageList.get(id);
				if (message != null) {
					message.setPosition(element);
					messages.add(message);
				}
			}
		}
		Message[] messageArray = new Message[messages.size()];
		messageArray = messages.toArray(messageArray);
		Arrays.sort(messageArray);
		messages.removeAllElements();
		for (Message message : messageArray) {
			messages.add(message);
		}
	}

	private void setMOSs(NodeList list) {
		for (int i = 0; i < list.getLength(); i++) {
			Element mos = (Element) list.item(i);
			if (mos.hasAttribute("xmi:type")
					&& mos.getAttribute("xmi:type").equals(
							"uml:MessageOccurrenceSpecification"))
				MOSs.add(new MessageOccurSpec(mos));
		}
	}

	private void setCombinedFragment(NodeList fragmentList,
			NodeList guiLinkList, NodeList guiNodeLinkList) {
		for (int i = 0; i < fragmentList.getLength(); i++) {
			Element fragment = (Element) fragmentList.item(i);
			if (fragment.hasAttribute("interactionOperator")) {
				String operator = fragment.getAttribute("interactionOperator");
				if (operator.equals("alt"))
					combinedFragments.add(new Alt(fragment, guiLinkList,
							guiNodeLinkList, messages));
				else if (operator.equals("opt"))
					combinedFragments.add(new Opt(fragment, guiLinkList,
							guiNodeLinkList, messages));
				else if (operator.equals("loop"))
					combinedFragments.add(new Loop(fragment, guiLinkList,
							guiNodeLinkList, messages));
				else
					combinedFragments.add(new Break(fragment, guiLinkList,
							guiNodeLinkList, messages));
			}

		}
	}

	/**
	 * Check whether an event with a specified class name and method name is
	 * match a message in the diagram
	 * 
	 * @param lifelineName
	 *            class name of event must be match a lifeline name
	 * @param messageName
	 *            method name of event must be match a message name
	 * @return true if the event matches a message
	 */
	public boolean isMessage(String lifelineName, String messageName) {
		for (Message message : messages) {
			if (message.getClassName().equals(lifelineName)
					&& message.getName().equals(messageName))
				return true;
		}
		return false;
	}

	private void setClassNameForMessage() {
		for (Message message : messages) {
			message.setClassName(lifelines, MOSs);
		}
	}

	/**
	 * Build a deterministic finite state machine from the loaded elements
	 * 
	 * @return FSMControl object that represent the finite state machine
	 */
	public FSMControl createFSMControl() {
		FSMControl fsmControl = new FSMControl(xmiPath);
		fsmControl.addState("0"); // add the initial state
		fsmControl.setFirstState("0");

		/*
		 * Add elements of state and temporary ignore the combined fragments
		 */
		for (int i = 0, count = 0; i < messages.size(); i++) {
			Message message = messages.get(i);
			if (!message.isReply()) {
				fsmControl.addState(String.valueOf(count + 1));
				fsmControl.addEvent(message.getEventName());
				fsmControl.addTransition(String.valueOf(count), message
						.getEventName(), String.valueOf(count + 1));
				message.setSourceState(String.valueOf(count));
				message.setTargetState(String.valueOf(count + 1));
				count++;
			}
		}
		fsmControl.setLastState();

		/*
		 * Process all combined fragment included in the diagram
		 */
		combinedFragments.postOrderTraverse(fsmControl);
		
		return fsmControl.NFAtoDFA();
	}
	
	public String getXmiPath(){
		return xmiPath;
	}
	/**
	 * an example of fsmControl for testing
	 */
	private FSMControl TestConvertNFAtoDFA2(){
		FSMControl fsmControl = new FSMControl("testConvert");
		fsmControl.addState("0");
		//fsmControl.setLastState();
		fsmControl.addState("1");
		fsmControl.setLastState();
		fsmControl.addState("2");
		fsmControl.addState("3");
		fsmControl.addState("4");
		fsmControl.setFirstState("0");
		fsmControl.addEvent("a");
		fsmControl.addEvent("b");
		fsmControl.addTransition("0", "a", "1");
		fsmControl.addTransition("0", "a", "2");
		fsmControl.addTransition("0", "b", "2");
		fsmControl.addTransition("0", "a", "3");
		fsmControl.addTransition("0", "b", "3");
		
		fsmControl.addTransition("1", "a", "1");
		fsmControl.addTransition("1", "a", "2");
		fsmControl.addTransition("1", "b", "2");
		fsmControl.addTransition("1", "b", "3");
		
		fsmControl.addTransition("2", "b", "2");
		fsmControl.addTransition("2", "b", "3");
		fsmControl.addTransition("2", "b", "4");
		
		fsmControl.addTransition("3", "b", "3");
		fsmControl.addTransition("3", "b", "4");
		fsmControl.addTransition("3", "a", "4");
		fsmControl.addTransition("3", "b", "2");
		return fsmControl;
		
	}
}