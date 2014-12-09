package sequence_diagram;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class MessageOccurSpec {
	private String id;
	private String lifelineCoveredId;

	MessageOccurSpec(Element element) {
		id = element.getAttribute("xmi:id");

		NodeList lifelineList = element.getElementsByTagName("covered");
		lifelineCoveredId = ((Element) lifelineList.item(0))
				.getAttribute("xmi:idref");
	}

	String getId() {
		return id;
	}

	String getLifelineCoveredId() {
		return lifelineCoveredId;
	}
}