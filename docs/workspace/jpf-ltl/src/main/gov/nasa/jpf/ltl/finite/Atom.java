/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import java.util.Set;

import gov.nasa.jpf.symbc.numeric.Constraint;

/**
 * This abstract base class represents an atomic proposition. An atom can be one
 * of the following forms:
 * <p>
 * - a relation such as 'C.x + D.y > E.z' (C,D,E are full class names including
 * their packages).
 * </p>
 * <p>
 * - a signature such as 'C.m(T)' which is true when the method is called and
 * false otherwise.
 * </p>
 * <p>
 * - simply a boolean variable.
 * </p>
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 */
public abstract class Atom {
  protected boolean isSatisfiable;
  protected String text; // The string represents this atom

  /**
   * @return symbolic constraints of this atom
   */
  public abstract Set<Constraint> getConstraints();

  /**
   * @return the string represents this atom
   */
  public String getText() {
    return text;
  }

  /**
   * checks for the satisfiability of this atom
   * 
   * @return {@code true} if this atom is satisfiable, {@code false} other wise
   */
  public boolean isSatisfiable() {
    return isSatisfiable;
  }
}
