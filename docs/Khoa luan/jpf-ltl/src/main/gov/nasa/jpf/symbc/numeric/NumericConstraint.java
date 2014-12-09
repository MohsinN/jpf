package gov.nasa.jpf.symbc.numeric;

public interface NumericConstraint{
  public boolean isSubsumedBy(NumericConstraint constraint);
  public Object getExpr();
  public String toString();
  public void addExistentialVar(Object dpvar);
  public boolean isSatisfiable();
}
