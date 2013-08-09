package gov.nasa.jpf.symbc.numeric;

public interface NumericConstraint {
  /**
   * 
   * @param constraint
   * @return {@code true} if this constraint is subsumed by constraint param
   */
	public boolean isSubsumedBy(NumericConstraint constraint);
  
	/**
	 * 
	 * @return the expression of this constraint
	 */
	public Object getExpr();

	/**
	 * 
	 * @return 
	 */
	public String toString();

	public void addExistentialVar(Object dpvar);

	public boolean isSatisfiable();
}
