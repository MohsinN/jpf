/**
 * 
 */
package gov.nasa.jpf.ltl.infinite;

import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;

/**
 * @author Ewgenij Starostin
 * 
 */
class Property extends GenericProperty {
	protected boolean violated = false;
	protected String ltl;
	protected String location;

	public Property(String ltl, String location) {
		this.ltl = ltl;
		this.location = location;
	}

	@Override
	public boolean check(Search search, JVM vm) {
		return !violated;
	}

	void setViolated() {
		this.violated = true;
	}

	@Override
	public String getErrorMessage() {
		return "Violated LTL property for " + location + ":\n\t" + ltl;
	}
}
