/**
 * 
 */
package gov.nasa.jpf.ltl.atom;

import gov.nasa.jpf.ltl.property.Field;
import gov.nasa.jpf.ltl.property.FieldTracker;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.string.StringConstant;

import java.util.Set;
import java.util.TreeSet;

/**
 * This is the abstract base class for all the operand of the expression in a
 * relation atomic proposition.
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public abstract class Operand {
  /**
   * represents a number operand
   * 
   * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
   * 
   */
  public static class Const extends Operand {
    private Object value;

    /**
     * creates a real constant operand
     * 
     * @param real
     *          the real number. That appears in a double format in the atom.
     */
    public Const(Double real) {
      value = real;
      text = real.toString();
    }

    /**
     * creates an integer constant operand
     * 
     * @param integer
     *          the Long number. Which appears in an integer format in the atom.
     */
    public Const(Long integer) {
      value = integer;
      text = integer.toString();
    }

    /**
     * @return {@code false}, this is a constant, not a variable or field.
     * @see gov.nasa.jpf.ltl.atom.Operand#isVariableNotExist()
     */
    @Override
    public boolean isVariableNotExist() {
      return false;
    }

    /* (non-Javadoc)
     * @see gov.nasa.jpf.ltl.finite.Operand#getValues()
     */
    @Override
    public Set<Object> getValues() {
      TreeSet<Object> result = new TreeSet<Object>();
      result.add(isNegative? convertToNeg(value) : value);
      return result;
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
    private int instanceOrder = -1;

    /**
     * Creates a new operand that represents a single field or local variable
     * and try to extract its value at the JPF runtime.
     * 
     * @param varName
     *          Full name of the variable.
     * @see gov.nasa.jpf.ltl.finite.LTLListener#getField(String)
     */
    public Var(String varName, boolean isChecking) {
      text = varName;
      if(!isChecking) 
        return;
        
      int i = varName.indexOf('#');
      if(i != -1) {
        String order = varName.substring(i);
        varName = varName.replace(order, "");
        instanceOrder = Integer.valueOf(order.substring(1));
      }
      FieldTracker.printFields();
      field = FieldTracker.getField(varName);
      if (field != null) {
        exist = true;
      }
      
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

    /* (non-Javadoc)
     * @see gov.nasa.jpf.ltl.finite.Operand#getValues()
     */
    @Override
    public Set<Object> getValues() {
      Set<Object> result = null;
      if(instanceOrder == -1) 
        result = field.getValues();
      else {
        Object value = field.getValue(instanceOrder);
        result = new TreeSet<Object>();
        if(value != null)
          result.add(value);
      }
      
      if(isNegative) {
        TreeSet<Object> negativeResult = new TreeSet<Object>();
        for(Object value: result)
          negativeResult.add(convertToNeg(value));
        return negativeResult;
      }
      
      return result;
    }
  }
  
  protected boolean isNegative;
  
  public void changeNegativeness() {
    isNegative = !isNegative;
  }

  /**
   * The String represents the operand.
   */
  protected String text;
  
  public abstract Set<Object> getValues();


  public String getText() {
    return text;
  }
  
  protected Object convertToNeg(Object value) {
    if(value instanceof Double) 
      return new Double(-((Double) value).doubleValue());
    else if(value instanceof Long)
      return new Long(-((Long) value).longValue());
    else if(value instanceof RealExpression) 
      return ((RealExpression) value)._neg();
    else if(value instanceof IntegerExpression)
      return ((IntegerExpression) value)._neg();
    else
      throw new RuntimeException("Type " + value.getClass() + " not supported for '-' operator");
  }
  
  public static Expression toSymbolic(Object concreteConst) {
    if(concreteConst instanceof Double)
      return new RealConstant(((Double) concreteConst).doubleValue());
    else if(concreteConst instanceof Long)
      return new IntegerConstant(((Long) concreteConst).intValue());
    else if(concreteConst instanceof String)
      return new StringConstant((String) concreteConst);
    else
      throw new RuntimeException("Cannot convert a concrete constant from " + concreteConst.getClass() + " to symbolic");
  }
  

  /**
   * Checks if this contains at least one variable or class field that doesn't
   * exist in SUT at the current runtime.
   * 
   * @return {@code true} if there exist such a variable or field, {@code false}
   *         other wise
   */
  public abstract boolean isVariableNotExist();

  public void setText(String text) {
    this.text = text;
  }

}
