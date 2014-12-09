/**
 * 
 */
package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.ltl.finite.Atom;
import gov.nasa.jpf.ltl.finite.Operand;

/**
 * This represents a relation atom. 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public class RelationAtom extends Atom {
  public static class EQ extends RelationAtom {
    public EQ(Operand left, Operand right) {
      super(left, Comparator.EQ, right);
    }
  }

  public static class GE extends RelationAtom {
    public GE(Operand left, Operand right) {
      super(left, Comparator.GE, right);
    }
  }

  public static class GT extends RelationAtom {
    public GT(Operand left, Operand right) {
      super(left, Comparator.GT, right);
    }
  }

  public static class LE extends RelationAtom {
    public LE(Operand left, Operand right) {
      super(left, Comparator.LE, right);
    }
  }

  public static class LT extends RelationAtom {
    public LT(Operand left, Operand right) {
      super(left, Comparator.LT, right);
    }
  }

  public static class NE extends RelationAtom {
    public NE(Operand left, Operand right) {
      super(left, Comparator.NE, right);
    }
  }

  private Constraint constraint;

  private Operand o1, o2;

  private Comparator cmp;

  public RelationAtom(Operand left, Comparator cmp, Operand right) {
    o1 = left;
    o2 = right;
    isSatisfiable = true;
    this.cmp = cmp;
    if (o1.isVariableNotExist() || o2.isVariableNotExist())
      isSatisfiable = false;
    else if (!isSymbolic())
      isSatisfiable = isSatisfyConcreteConstraint();
    text = o1.getText() + cmp.toString() + o2.getText();
  }

  @Override
  public Constraint getConstraint() {
    if (!isSatisfiable || !isSymbolic())
      return null;

    Expression left = o1.getSymbolicValue();
    Expression right = o2.getSymbolicValue();
    assert left != null && right != null;

    if (left instanceof RealExpression && right instanceof RealExpression)
      constraint = new RealConstraint((RealExpression) left, cmp,
          (RealExpression) right);
    else if (left instanceof IntegerExpression
        && right instanceof IntegerExpression) {
      if (left instanceof LinearIntegerExpression
          && right instanceof LinearIntegerExpression)
        constraint = new LinearIntegerConstraint((IntegerExpression) left, cmp,
            (IntegerExpression) right);
      else
        constraint = new NonLinearIntegerConstraint((IntegerExpression) left,
            cmp, (IntegerExpression) right);
    } else if (left instanceof RealExpression)
      constraint = new MixedConstraint((RealExpression) left, cmp,
          (IntegerExpression) right);
    else
      constraint = new MixedConstraint((RealExpression) right, cmp,
          (IntegerExpression) left);
    return constraint;
  }

  private boolean isSatisfyConcreteConstraint() {
    Object left = o1.getConcreteValue();
    Object right = o2.getConcreteValue();

    if (left instanceof String && right instanceof String) {
      String a = (String) left;
      String b = (String) right;

      switch (cmp) {
      case EQ:
        return a.equals(b);
      case NE:
        return !a.equals(b);
      default:
        throw new RuntimeException(
            "## Error: The operator "
                + cmp
                + " is undefined for the argument type(s) java.lang.String, java.lang.String");
      }
    } else if (left instanceof Number && right instanceof Number) {
      double a = ((Number) left).doubleValue();
      double b = ((Number) right).doubleValue();
      switch (cmp) {
      case EQ:
        return a == b;
      case NE:
        return a != b;
      case LT:
        return a < b;
      case LE:
        return a <= b;
      case GT:
        return a > b;
      case GE:
        return a >= b;
      }
    }
    throw new RuntimeException(
        "## Error: The reference type has not supported or incompatible");
  }

  public boolean isSymbolic() {
    return o1.isSymbolic() || o2.isSymbolic();
  }
}
