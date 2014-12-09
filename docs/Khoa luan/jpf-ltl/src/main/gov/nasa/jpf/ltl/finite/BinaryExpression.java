/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.Operator;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.string.StringExpression;

/**
 * A expression in the relation atom.
 * 
 * @author Phuc Nguyen Dinh - luckymaphuc@gmail.com
 * 
 */
public class BinaryExpression extends Operand {
  public static class Div extends BinaryExpression {
    public Div(Operand left, Operand right) {
      super(left, right, Operator.DIV);
    }
  }

  public static class Minus extends BinaryExpression {
    public Minus(Operand left, Operand right) {
      super(left, right, Operator.MINUS);
    }
  }

  public static class Mult extends BinaryExpression {
    public Mult(Operand left, Operand right) {
      super(left, right, Operator.MUL);
    }
  }

  public static class Plus extends BinaryExpression {
    public Plus(Operand left, Operand right) {
      super(left, right, Operator.PLUS);
    }
  }

  private Operand o1, o2;

  private Operator operator;

  public BinaryExpression(Operand left, Operand right, Operator op) {
    o1 = left;
    o2 = right;
    operator = op;
    text = left.getText() + operator.toString() + right.getText();
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.ltl.Operand#getConcreteValue()
   */
  @Override
  public Object getConcreteValue() {
    assert !isVariableNotExist() : "Some field doesn't exist while evaluating the concrete value";
    Object left = o1.getConcreteValue();
    Object right = o2.getConcreteValue();
    if (left instanceof String && right instanceof String) {
      if (operator.equals(Operator.PLUS))
        return (String) left + (String) right;
      throw new RuntimeException(
          "## Error: The operator"
              + operator
              + " is undefined for the argument type(s) java.lang.String, java.lang.String ");
    } else if (left instanceof Number && right instanceof Number) {
      Number a = (Number) right;
      Number b = (Number) left;
      if (a instanceof Double || b instanceof Double)
        switch (operator) {
        case MUL:
          return new Double(a.doubleValue() * b.doubleValue());
        case DIV:
          return new Double(a.doubleValue() / b.doubleValue());
        case PLUS:
          return new Double(a.doubleValue() + b.doubleValue());
        case MINUS:
          return new Double(a.doubleValue() - b.doubleValue());
        default:
          throw new AssertionError("Invalid operator: " + operator);
        }
      else
        switch (operator) {
        case MUL:
          return new Long(a.longValue() * b.longValue());
        case DIV:
          return new Long(a.longValue() / b.longValue());
        case PLUS:
          return new Long(a.longValue() + b.longValue());
        case MINUS:
          return new Long(a.longValue() - b.longValue());
        default:
          throw new AssertionError("Invalid operator: " + operator);
        }
    }
    throw new RuntimeException("## Error: Type is incompatible");
  }

  /**
   * Retrieves the symbolic representations of this expression.
   * 
   * @return the symbolic expression
   * @see gov.nasa.jpf.symbc.numeric.Expression
   */
  @Override
  public Expression getSymbolicValue() {
    assert !isVariableNotExist() : "Some field doesn't exist while evaluating the symbolic value";
    Object left = o1.getSymbolicValue();
    Object right = o2.getSymbolicValue();
    if (left instanceof StringExpression && right instanceof StringExpression)
      throw new RuntimeException(
          "## Error: String symbolic is incompatible with the operator "
              + operator);
    else if (left instanceof RealExpression && right instanceof RealExpression)
      switch (operator) {
      case MUL:
        return ((RealExpression) left)._mul((RealExpression) right);
      case DIV:
        return ((RealExpression) left)._div((RealExpression) right);
      case PLUS:
        return ((RealExpression) left)._plus((RealExpression) right);
      case MINUS:
        return ((RealExpression) left)._minus((RealExpression) right);
      default:
        throw new AssertionError(
            "RealExpression is incompatible with the operator: " + operator);
      }
    else if (left instanceof IntegerExpression
        && right instanceof IntegerExpression)
      switch (operator) {
      case MUL:
        return ((IntegerExpression) left)._mul((IntegerExpression) right);
      case DIV:
        throw new RuntimeException(
            "## Error: Operation on DIV operator has not supported with IntegerExpression");
      case PLUS:
        return ((IntegerExpression) left)._plus((IntegerExpression) right);
      case MINUS:
        return ((IntegerExpression) left)._minus((IntegerExpression) right);
      default:
        throw new AssertionError(
            "IntegerExpression is incompatible with the operator: " + operator);
      }
    throw new RuntimeException(
        "## Error: type incompatibility real/integer expression");
  }

  /**
   * indicates whether this expression is symbolic. An expression is symbolic if
   * and only if one of its two operand is symbolic.
   * 
   * @return
   */
  @Override
  public boolean isSymbolic() {
    return o1.isSymbolic() || o2.isSymbolic();
  }

  /**
   * checks if this expression contains any variable which doesn't exist in the
   * current SUT runtime
   * 
   * @return
   */
  @Override
  public boolean isVariableNotExist() {
    return o1.isVariableNotExist() || o2.isVariableNotExist();
  }
}
