/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.symbc.string.StringConstant;

/**
 * This is the abstract base class for all the operand of the expression in the
 * relation atom.
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public abstract class Operand {
  /**
   * represents a number operand
   * 
   * @author Phuc Nguyen Dinh
   * 
   */
  public static class Const extends Operand {
    private Object value;

    /**
     * creates a real operand
     * 
     * @param real
     *          the real number. That appears in a double format in the atom.
     */
    public Const(Double real) {
      value = real;
      text = real.toString();
    }

    /**
     * creates an integer operand
     * 
     * @param integer
     *          the Long number. Which appears in an int format in the atom.
     */
    public Const(Long integer) {
      value = integer;
      text = integer.toString();
    }

    @Override
    public Object getConcreteValue() {
      return value;
    }

    @Override
    public Expression getSymbolicValue() {
      if (value instanceof Double)
        return new RealConstant(((Double) value).doubleValue());
      else
        return new IntegerConstant(((Long) value).intValue());
    }

    @Override
    public boolean isSymbolic() {
      return false;
    }

    @Override
    public boolean isVariableNotExist() {
      return false;
    }
  }

  /**
   * This class represents an operand which has only a variable.
   * 
   * @author Phuc Nguyen Dinh
   * 
   */
  public static class Var extends Operand {
    private boolean exist;
    private Field field;
    private String type;
    private Object value;

    /**
     * creates the variable operand
     * 
     * @param varName
     */
    public Var(String varName) {
      field = LTLListener.getField(varName);
      if (field != null) {
        exist = true;
        type = field.getType();
      }
      text = varName;
    }

    @Override
    public Object getConcreteValue() {
      assert exist : "Cannot find any variable which matches with the specified name";
      return field.getConcreteValue();
    }

    @Override
    public Expression getSymbolicValue() {
      assert exist : "Cannot find any variable which matches with the specified name";
      value = field.getSymbolicValue();
      if (value != null)
        return (Expression) value;

      value = field.getConcreteValue();
      if (type.equals("double") || type.equals("float"))
        return new RealConstant(((Double) value).doubleValue());
      else if (type.equals("String"))
        return new StringConstant((String) value);
      else {
        assert !type.equals("Object") && !field.isRef() : "The symbolic type is not supported: "
            + type.toString();
        return new IntegerConstant(((Long) value).intValue());
      }
    }

    /**
     * indicates whether this variable is symbolic or not. If it is symbolic,
     * the symbolic value must be non-null
     */
    @Override
    public boolean isSymbolic() {
      return field.isSymbolic();
    }

    /**
     * indicates whether this variable exists in the system under test or not
     * 
     * @return
     */
    @Override
    public boolean isVariableNotExist() {
      return !exist;
    }
  }

  /**
   * The String represents the operand.
   */
  protected String text;

  /**
   * Retrieves the value of this operand
   * 
   * @return the concrete value. Caller must cast to the appropriate type.
   */
  public abstract Object getConcreteValue();

  /**
   * Returns the symbolic representations of this operand. If this is concrete,
   * we must convert it to symbolic.
   * 
   * @return the symbolic expression
   * @see gov.nasa.jpf.symbc.numeric.Expression
   */
  public abstract Expression getSymbolicValue();

  public String getText() {
    return text;
  }

  public abstract boolean isSymbolic();

  public abstract boolean isVariableNotExist();

  public void setText(String text) {
    this.text = text;
  }

}
