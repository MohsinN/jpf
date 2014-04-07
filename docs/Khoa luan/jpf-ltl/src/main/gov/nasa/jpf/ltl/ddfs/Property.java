/**
 * 
 */
package gov.nasa.jpf.ltl.ddfs;

import gov.nasa.jpf.GenericProperty;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;

/**
 * @author estar
 *
 */
class Property extends GenericProperty {
  protected boolean violated = false;
  protected String ltl, location;
  
  public Property (String ltl, String location) {
    this.ltl = ltl;
    this.location = location;
  }
  
  /* (non-Javadoc)
   * @see gov.nasa.jpf.GenericProperty#check(gov.nasa.jpf.search.Search, gov.nasa.jpf.jvm.JVM)
   */
  @Override
  public boolean check (Search search, JVM vm) {
    return violated;
  }

  void setViolated () {
    this.violated = true;
  }
  
  @Override
  public String getErrorMessage () {
    return "Violated LTL property from " + location + ":\n\t" + ltl;
  }
}
