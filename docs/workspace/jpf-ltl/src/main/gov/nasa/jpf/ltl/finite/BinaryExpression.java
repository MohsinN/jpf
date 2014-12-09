/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.Operator;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.string.StringExpression;

import java.util.Set;
import java.util.TreeSet;

/**
 * This represents a binary expression in the relational atomic proposition.
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
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

  private BinaryExpression(Operand left, Operand right, Operator op) {
    o1 = left;
    o2 = right;
    operator = op;
    text = left.getText() + operator.toString() + right.getText();
  }

  private Object evalConcreteResult(Object left, Object right) {
    if (left instanceof String && right instanceof String) {
      if (operator.equals(Operator.PLUS))
        return (String) left + (String) right;
      else
        throw new RuntimeException(
            "## Error: The operator"
                + operator
                + " is undefined for the argument type(s) java.lang.String, java.lang.String ");
    } else if (left instanceof Number && right instanceof Number) {
      Number a = (Number) right;
      Number b = (Number) left;
      Object value = null;
      if (a instanceof Double || b instanceof Double) {
        switch (operator) {
        case MUL:
          value = new Double(a.doubleValue() * b.doubleValue());
          break;
        case DIV:
          value = new Double(a.doubleValue() / b.doubleValue());
          break;
        case PLUS:
          value = new Double(a.doubleValue() + b.doubleValue());
          break;
        case MINUS:
          value = new Double(a.doubleValue() - b.doubleValue());
          break;
        default:
          throw new AssertionError("Invalid operator: " + operator);
        }
      } else {
        switch (operator) {
        case MUL:
          value = new Long(a.longValue() * b.longValue());
          break;
        case DIV:
          value = new Long(a.longValue() / b.longValue());
          break;
        case PLUS:
          value = new Long(a.longValue() + b.longValue());
          break;
        case MINUS:
          value = new Long(a.longValue() - b.longValue());
          break;
        default:
          throw new AssertionError("Invalid operator: " + operator);
        }
      }
      return value;
    }
    throw new RuntimeException("## Error: Type is incompatible");
  }
  
  private Expression evalSymbolicConstraint(Expression left,  Expression right) {
    Expression value = null;
    if (left instanceof StringExpression
        || right instanceof StringExpression)
      throw new RuntimeException(
          "## Error: String symbolic is incompatible with the operator "
              + operator);
    else if (left instanceof RealExpression
        && right instanceof RealExpression)
      switch (operator) {
      case MUL:
        value = ((RealExpression) left)._mul((RealExpression) right);
        break;
      case DIV:
        value = ((RealExpression) left)._div((RealExpression) right);
        break;
      case PLUS:
        value = ((RealExpression) left)._plus((RealExpression) right);
        break;
      case MINUS:
        value = ((RealExpression) left)._minus((RealExpression) right);
        break;
      default:
        throw new AssertionError(
            "RealExpression is incompatible with the operator: " + operator);
      }
    else if (left instanceof IntegerExpression
        && right instanceof IntegerExpression)
      switch (operator) {
      case MUL:
        value = ((IntegerExpression) left)._mul((IntegerExpression) right);
        break;
      case DIV:
        throw new RuntimeException(
            "## Error: Operation on DIV operator has not supported with IntegerExpression");
      case PLUS:
        value = ((IntegerExpression) left)._plus((IntegerExpression) right);
        break;
      case MINUS:
        value = ((IntegerExpression) left)
            ._minus((IntegerExpression) right);
        break;
      default:
        throw new AssertionError(
            "IntegerExpression is incompatible with the operator: "
                + operator);
      }
    if(value != null)
      return value;
    throw new RuntimeException("## Error: type incompatibility real/integer expression");
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.ltl.finite.Operand#isVariableNotExist()
   */
  @Override
  public boolean isVariableNotExist() {
    return o1.isVariableNotExist() || o2.isVariableNotExist();
  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.ltl.finite.Operand#getValues()
   */
  @Override
  public Set<Object> getValues() {
    assert !isVariableNotExist() : "Some field doesn't exist while evaluating the concrete value";
    Set<Object> leftValues = o1.getValues();
    Set<Object> rightValues = o2.getValues();
    TreeSet<Object> result = new TreeSet<Object>();
    
    for(Object left: leftValues) 
      for(Object right: rightValues) {
        Object value = null;
        if(left instanceof Expression || right instanceof Expression) {
          if(!(left instanceof Expression))
            left = toSymbolic(left);
          if(!(right instanceof Expression))
            right = toSymbolic(right);
          value = evalSymbolicConstraint((Expression) left, (Expression) right);
        }
        else 
          value = evalConcreteResult(left, right);
        result.add(isNegative ? convertToNeg(value) : value);
      }
    return result;
  }
}
