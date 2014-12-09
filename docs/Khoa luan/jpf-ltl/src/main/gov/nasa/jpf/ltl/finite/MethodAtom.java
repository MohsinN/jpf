/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import gov.nasa.jpf.symbc.numeric.Constraint;

/**
 * This atom requires a given method must be called.
 * 
 * @author Phuc Nguyen Dinh
 * 
 */
public class MethodAtom extends Atom {

  /**
   * creates new method atom and check for the satisfiability. All the
   * parameters must be the form of method long name.
   * 
   * @param atomName
   *          name of the method given in the LTL formulae
   * @param methodName
   *          the current method name of the instruction
   *@see gov.nasa.jpf.jvm.MethodInfo#getLongName()
   */
  public MethodAtom(String atomName, String methodName) {
    isSatisfiable = atomName.equals(methodName);
    text = atomName;
  }

  /**
   * @return null, there is no constraint for a method invocation.
   * 
   */
  public Constraint getConstraint() {
    return null;
  }

}
