/**
 * 
 */
package gov.nasa.jpf.ltl.atom;

import java.util.Set;

import gov.nasa.jpf.symbc.numeric.Constraint;

/**
 * This represents an atomic proposition in the LTL specification for methods.
 * In the LTL formula, an atom can be a method signature with full package name
 * and their parameters (if any). For instance:
 * <p>
 * <code>packageName.ClassName.methodName(int, float [] [], String, T)</code>
 * </p>
 * <p>
 * <code>ClassName.methodName(T)</code>
 * </p>
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public class MethodAtom extends Atom {

  /**
   * Constructor. All the parameters must be in the form of method long name.
   * 
   * @param atomName
   *          name of the method given in the LTL formula
   * @param methodName
   *          the current method name of the instruction
   *@see gov.nasa.jpf.jvm.MethodInfo#getLongName()
   */
  public MethodAtom(String atomName, String methodName) {
    isSatisfiable = atomName.equals(methodName);
    text = atomName;
  }

  /**
   * @return {@code null}, there is no constraint needed for a method
   *         invocation.
   * 
   */
  @Override
  public Set<Constraint> getConstraints() {
    return null;
  }

}
