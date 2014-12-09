package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.symbc.numeric.solvers.ProblemGeneral;

import java.util.ArrayList;

import cvc3.*;

public class CVC3Constraint extends ProblemGeneral implements NumericConstraint {
  protected Expr pb;
  ArrayList<Expr> listConstraint;
  ArrayList<Expr> listExistentialIntegerVar;
  protected final int base = 10; // used in creating real variables
  static ValidityChecker vc = null;
  static FlagsMut flags = null;

  public static void initCVC3Constraint() {
    flags = ValidityChecker.createFlags(null);
    flags.setFlag("dagify-exprs", false);
    vc = ValidityChecker.create(flags);
  }

  public CVC3Constraint() {
    listConstraint = new ArrayList<Expr>();
    listExistentialIntegerVar = new ArrayList<Expr>();
  }

  public Object getExpr() {
    if (pb == null) {
      pb = vc.trueExpr();
      return pb;
    }
    if (listConstraint.size() != 0) {
      pb = vc.andExpr(listConstraint);
      if (listExistentialIntegerVar != null)
        ;
      pb = vc.existsExpr(listExistentialIntegerVar, pb);
    }
    return pb;
  }

  @Override
  public boolean isSubsumedBy(NumericConstraint constraint) {
    vc.push();

    System.out.println("isSubsumedBy: " + constraint.getExpr());
    assert constraint.getExpr() != null : "Can not check subsume with null expr" + constraint;

    Expr cc = vc.impliesExpr(pb, (Expr) (constraint.getExpr()));
    System.out.println("Implication: " + cc);
    QueryResult sr = vc.query(cc);

    System.out.println(sr.toString());
    vc.pop();
    return sr == QueryResult.VALID;
  }

  public String toString() {
    if (pb == null)
      return "NULL";
    return pb.toString();
  }

  public void addExistentialVar(Object dpvar) {
    listExistentialIntegerVar.add((Expr) dpvar);
  }

  public void post(Object constraint) {
    try {
      listConstraint.add((Expr) constraint);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught making Int Var in CVC3 ???: \n" + e);
    }
  }

  public Object makeIntVar(String name, int min, int max) {
    try {
      /*
       * Type sType = vc.subrangeType(vc.ratExpr(min), vc.ratExpr(max)); return
       * vc.varExpr(name, sType);
       */
      Expr expr = vc.varExpr(name, vc.intType());
      return expr;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
    }
  }

  public Object makeRealVar(String name, double min, double max) {
    // WARNING: need to downcast double to int - I don't see
    // a way in CVC3 to create a sub-range for real types
    // other choice is not to bound and use vc.realType() to
    // create the expression
    int minInt = (int) min;
    int maxInt = (int) max;
    try {
      // Expr x = vc.varExpr(name, vc.realType());
      /*
       * Type sType = vc.subrangeType(vc.ratExpr(minInt), vc.ratExpr(maxInt));
       * return vc.varExpr(name, sType);
       */
      Expr expr = vc.varExpr(name, vc.intType());
      return expr;
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object eq(int value, Object exp) {
    try {
      return vc.eqExpr(vc.ratExpr(value), (Expr) exp);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object eq(Object exp, int value) {
    try {
      return vc.eqExpr((Expr) exp, vc.ratExpr(value));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object eq(Object exp1, Object exp2) {
    try {
      return vc.eqExpr((Expr) exp1, (Expr) exp2);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object eq(double value, Object exp) {
    try {
      return vc.eqExpr(vc.ratExpr(Double.toString(value), base), (Expr) exp);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object eq(Object exp, double value) {
    try {
      return vc.eqExpr((Expr) exp, vc.ratExpr(Double.toString(value), base));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object neq(int value, Object exp) {
    try {
      return vc.notExpr(vc.eqExpr(vc.ratExpr(value), (Expr) exp));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object neq(Object exp, int value) {
    try {
      return vc.notExpr(vc.eqExpr((Expr) exp, vc.ratExpr(value)));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object neq(Object exp1, Object exp2) {
    try {
      return vc.notExpr(vc.eqExpr((Expr) exp1, (Expr) exp2));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object not(Object exp1) {
    try {
      return vc.notExpr((Expr) exp1);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object neq(double value, Object exp) {
    try {
      return vc.notExpr(vc.eqExpr(vc.ratExpr(Double.toString(value), base),
          (Expr) exp));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object neq(Object exp, double value) {
    try {
      return vc.notExpr(vc.eqExpr((Expr) exp, vc.ratExpr(
          Double.toString(value), base)));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object leq(int value, Object exp) {
    try {
      return vc.leExpr(vc.ratExpr(value), (Expr) exp);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object leq(Object exp, int value) {
    try {
      return vc.leExpr((Expr) exp, vc.ratExpr(value));
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object leq(Object exp1, Object exp2) {
    try {
      return vc.leExpr((Expr) exp1, (Expr) exp2);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  public Object leq(double value, Object exp) {
    try {
      return vc.leExpr(vc.ratExpr(Double.toString(value), base), (Expr) exp);

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(
          "## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  @Override
  public Object and(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object and(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object and(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object div(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object div(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object div(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object div(double value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object div(Object exp, double value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object geq(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object geq(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object geq(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object geq(double value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object geq(Object exp, double value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getIntValue(Object dpVar) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getRealValue(Object dpVar) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getRealValueInf(Object dpvar) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double getRealValueSup(Object dpVar) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Object gt(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object gt(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object gt(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object gt(double value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object gt(Object exp, double value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object leq(Object exp, double value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object lt(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object lt(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object lt(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object lt(double value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object lt(Object exp, double value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object minus(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object minus(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object minus(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object minus(double value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object minus(Object exp, double value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object mixed(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object mult(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object mult(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object mult(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object mult(double value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object mult(Object exp, double value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object or(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object or(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object or(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object plus(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object plus(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object plus(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object plus(double value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object plus(Object exp, double value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object shiftL(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object shiftL(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object shiftL(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object shiftR(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object shiftR(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object shiftR(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object shiftUR(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object shiftUR(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object shiftUR(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Boolean solve() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object xor(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object xor(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object xor(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isSatisfiable() {
    // TODO Auto-generated method stub
    return false;
  }
  
}
