/**
 * 
 */
package gov.nasa.jpf.ltl.ddfs;

import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.search.Search;

/**
 * @author Michele Lombardi
 * 
 */
class LTLProperty extends GenericProperty {
  protected boolean violated = false;
  protected String ltl, location;

  public LTLProperty(String ltl, String location) {
    this.ltl = ltl;
    this.location = location;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.GenericProperty#check(gov.nasa.jpf.search.Search,
   * gov.nasa.jpf.jvm.JVM)
   */
  @Override
  public boolean check(Search search, VM vm) {
    return !violated;
  }

  void setViolated() {
    this.violated = true;
  }

  @Override
  public String getErrorMessage() {
    return "Violated LTL property for " + location + ":\n " + ltl;
  }

}
