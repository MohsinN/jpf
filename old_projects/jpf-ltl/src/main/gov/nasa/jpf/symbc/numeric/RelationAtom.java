/**
 * 
 */
package gov.nasa.jpf.symbc.numeric;

import java.util.Set;
import java.util.TreeSet;

import gov.nasa.jpf.ltl.atom.Atom;
import gov.nasa.jpf.ltl.atom.Operand;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.LinearIntegerConstraint;
import gov.nasa.jpf.symbc.numeric.LinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.MixedConstraint;
import gov.nasa.jpf.symbc.numeric.NonLinearIntegerConstraint;
import gov.nasa.jpf.symbc.numeric.RealConstraint;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.string.StringExpression;

/**
 * This represents a relation atom.
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public class RelationAtom extends Atom {
  public static class EQ extends RelationAtom {
    public EQ(Operand left, Operand right, boolean isChecking) {
      super(left, Comparator.EQ, right, isChecking);
    }
  }

  public static class GE extends RelationAtom {
    public GE(Operand left, Operand right, boolean isChecking) {
      super(left, Comparator.GE, right, isChecking);
    }
  }

  public static class GT extends RelationAtom {
    public GT(Operand left, Operand right, boolean isChecking) {
      super(left, Comparator.GT, right, isChecking);
    }
  }

  public static class LE extends RelationAtom {
    public LE(Operand left, Operand right, boolean isChecking) {
      super(left, Comparator.LE, right, isChecking);
    }
  }

  public static class LT extends RelationAtom {
    public LT(Operand left, Operand right, boolean isChecking) {
      super(left, Comparator.LT, right, isChecking);
    }
  }

  public static class NE extends RelationAtom {
    public NE(Operand left, Operand right, boolean isChecking) {
      super(left, Comparator.NE, right, isChecking);
    }
  }

  private Set<Constraint> constraints;

  private Operand o1, o2;

  private Comparator cmp;

  /**
   * Creates a new instance of <code>RelationAtom</code>.
   * 
   * @param left
   *          The left hand side expression.
   * @param cmp
   *          The relative operator.
   * @param right
   *          The right hand side expression.
   */
  private RelationAtom(Operand left, Comparator cmp, Operand right, boolean isChecking) {
    text = left.getText() + cmp.toString() + right.getText();
    if(!isChecking)
      return;
    o1 = left;
    o2 = right;
    constraints = new TreeSet<Constraint>();
    isSatisfiable = true;
    this.cmp = cmp;
    if (o1.isVariableNotExist() || o2.isVariableNotExist())
      isSatisfiable = false;
    else
      isSatisfiable = isSatisfyConcreteConstraint();
    System.err.println("RelationAtom " +  text + ", o1=" + o1.isVariableNotExist() + ", o2=" + o2.isVariableNotExist() + ", isSatisfiable=" + isSatisfiable);
  }
  
  
  
  private Constraint evalConstraint(Expression left, Expression right) {
    Constraint constraint;
    if (left instanceof StringExpression || right instanceof StringExpression)
      throw new RuntimeException(
          "## Error: String symbolic is incompatible with the operator " + cmp);

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

  /*
   * Checks if this atom is satisfied in concrete mode.
   */
  private boolean isSatisfyConcreteConstraint() {
    Set<Object> leftValues = o1.getValues();
    Set<Object> rightValues = o2.getValues();

    if(leftValues.isEmpty() || rightValues.isEmpty())
      return false;
    for (Object left : leftValues)
      for (Object right : rightValues) {
        if(left instanceof Expression || right instanceof Expression) {
          if(!(left instanceof Expression))
            left = Operand.toSymbolic(left);
          if(!(right instanceof Expression))
            right = Operand.toSymbolic(right);
          constraints.add(evalConstraint((Expression) left, (Expression) right));
          continue;
        }
        int diff = 0;

        // Boolean is not a subtype of Number, so convert if needed.
        if (left instanceof Boolean)
          left = (Integer) (((Boolean) left) ? 1 : 0);
        if (right instanceof Boolean)
          right = (Integer) (((Boolean) right) ? 1 : 0);

        if (left instanceof String && right instanceof String) {
          String a = (String) left;
          String b = (String) right;
          diff = a.compareTo(b);
        } else if (left instanceof Number && right instanceof Number) {
          /*
           * If both arguments are integral, compare them as long values, else
           * as double values.
           */
          if (((left instanceof Short) || (left instanceof Integer) || (left instanceof Long))
              && ((right instanceof Short) || (right instanceof Integer) || (right instanceof Long)))
            diff = new Long(((Number) left).longValue())
                .compareTo(((Number) right).longValue());
          else
            diff = new Double(((Number) left).doubleValue())
                .compareTo(((Number) right).doubleValue());
        } else {
          throw new RuntimeException(
              "## Error: A reference type is not supported or incompatible: "
                  + left.getClass().getName() + cmp
                  + right.getClass().getName());
        }
        boolean result;
        switch (cmp) {
        case EQ:
          result = diff == 0;
          break;
        case NE:
          result = diff != 0;
          break;
        case LT:
          result = diff < 0;
          break;
        case LE:
          result = diff <= 0;
          break;
        case GT:
          result = diff > 0;
          break;
        case GE:
          result = diff >= 0;
          break;
        default: // Cannot happen.
          return false;
        }
        if(!result)
          return false;
      }
    return true;
  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.ltl.finite.Atom#getConstraints()
   */
  @Override
  public Set<Constraint> getConstraints() {
    return constraints;
  }
}
