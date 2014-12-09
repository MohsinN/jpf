/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import gov.nasa.jpf.symbc.numeric.Constraint;

/**
 * This abstract base class represents an atomic proposition. An atom can be one
 * of the following form
 *  
 * - Relation
 * <p>
 * - Method call
 * <p>
 * - Boolean variable
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public abstract class Atom {
  protected boolean isSatisfiable;
  protected String text;

  /**
   * gets the symbolic constraint of this atom
   * 
   * @return
   */
  public abstract Constraint getConstraint();

  /**
   * @return the string represents this atom
   */
  public String getText() {
    return text;
  }

  /**
   * check for the satisfiability of this atom
   * 
   * @return
   */
  public boolean isSatisfiable() {
    return isSatisfiable;
  }

  public void setText(String text) {
    this.text = text;
  }
}
