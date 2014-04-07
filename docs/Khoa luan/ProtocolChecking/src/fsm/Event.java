package fsm;

import gov.nasa.jpf.jvm.Path;

/**
 * Class that represent an event
 * 
 * @author Phuc Nguyen Dinh
 * @author Chung Tuyen Luu
 * 
 */
public class Event{
	int threadId, objectId, lineNumber;
	String name, className, objectName, location;
	boolean isStatic;

	public Event(int threadId, int objectId, boolean isStatic, String name,
			String className, int lineNumber, String location) {
		this.threadId = threadId;
		this.name = name;
		this.className = className;
		this.objectId = objectId;
		this.lineNumber = lineNumber;
		this.location = location;
	}

	public Event(String name) {
		this.name = name;
		int i = name.indexOf('.');
		objectName = name.substring(0, i);
		className = name.substring(i + 1, name.lastIndexOf('.'));
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public void setIsStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public String getEventName() {
		return objectName + "." + name;
	}

	public int getThreadId() {
		return threadId;
	}

	public String getName() {
		return name;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getClassName() {
		return  className;
	}
}