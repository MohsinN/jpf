package sequence_diagram;

import org.w3c.dom.Element;

class Lifeline {
	private String id, className, name;
	private boolean isMultiObject;
	String objectName;
	Lifeline(Element lifeline) {
		id = lifeline.getAttribute("xmi:id");
		objectName = "object1";
		String fullName = lifeline.getAttribute("name");
		int index = fullName.indexOf(':');
		if(index != -1) {
			className = fullName.substring(index + 1);
			if(index != 0)
				name = fullName.substring(0, index);
			else
				name = "anonymous";
		}
		else {
			className = fullName;
			name = "anonymous";
		}
	}
	boolean isObject() {
		return name != null;
	}
	boolean isAnonymous() {
		return name.equals("anonymous");
	}
	boolean isMultiObject() {
		return isMultiObject;
	}
	void setMultiObject(boolean multiObject) {
		isMultiObject = multiObject;
	}
	String getId() {
		return id;
	}
	
	String getClassName() {
		return className;
	}
	
	void setName(String name) {
		this.name = name;
	}

	String getName() {
		return name;
	}
	
	void setObjectName(String name) {
		objectName = name;
	}
}