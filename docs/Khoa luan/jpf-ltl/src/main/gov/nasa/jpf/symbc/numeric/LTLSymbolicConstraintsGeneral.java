package gov.nasa.jpf.symbc.numeric;

import java.util.HashMap;
import java.util.*;

/**
 * @author Tuyen Luu
 */
public class LTLSymbolicConstraintsGeneral extends SymbolicConstraintsGeneral{
  Map<SymbolicInteger,Object> fieldIntegerVar;

  public LTLSymbolicConstraintsGeneral(){
    CVC3Constraint.initCVC3Constraint();
	pb = new CVC3Constraint();
	
  }
	
  Object getExpression(IntegerExpression eRef) {
    assert eRef != null;
	assert !(eRef instanceof IntegerConstant);
	
	if (eRef instanceof SymbolicInteger) {
	  String eRefName = ((SymbolicInteger)eRef).getName();
	  //System.out.println(" ERef Name = " + eRefName);
	  if (!eRefName.contains("e")){
	    Object dpvar = symIntegerVar.get(eRef);
		if (dpvar == null) {
		//System.out.println("Make BoundIntVar " + ((SymbolicInteger)eRef).getName());
		  dpvar = pb.makeIntVar(((SymbolicInteger)eRef).getName(), 
			   ((SymbolicInteger)eRef)._min, ((SymbolicInteger)eRef)._max);
		  symIntegerVar.put((SymbolicInteger)eRef, dpvar);
		  ((NumericConstraint)pb).addExistentialVar(dpvar);
		}
		return dpvar;
	  }
	  else{
	    Object dpvar = fieldIntegerVar.get(eRef);
		if (dpvar == null) {
		  //System.out.println("Make BoundIntVar " + ((SymbolicInteger)eRef).getName());
		  dpvar = pb.makeIntVar(((SymbolicInteger)eRef).getName(), 
			   ((SymbolicInteger)eRef)._min, ((SymbolicInteger)eRef)._max);
		  fieldIntegerVar.put((SymbolicInteger)eRef, dpvar);
	  }
	  return dpvar;
	}
  }

  Operator    opRef;
  IntegerExpression	e_leftRef;
  IntegerExpression	e_rightRef;

  if(eRef instanceof BinaryLinearIntegerExpression) {
	opRef = ((BinaryLinearIntegerExpression)eRef).op;
    e_leftRef = ((BinaryLinearIntegerExpression)eRef).left;
    e_rightRef = ((BinaryLinearIntegerExpression)eRef).right;

    switch(opRef){
      case PLUS:
	    if (e_leftRef instanceof IntegerConstant && e_rightRef instanceof IntegerConstant)
		  throw new RuntimeException("## Error: this is not a symbolic expression"); // TODO: fix
        else if (e_leftRef instanceof IntegerConstant)
          return pb.plus(((IntegerConstant)e_leftRef).value,getExpression(e_rightRef));
        else if (e_rightRef instanceof IntegerConstant)
          return pb.plus(getExpression(e_leftRef),((IntegerConstant)e_rightRef).value);
		else
		  return pb.plus(getExpression(e_leftRef),getExpression(e_rightRef));
      case MINUS:
        if (e_leftRef instanceof IntegerConstant && e_rightRef instanceof IntegerConstant)
          throw new RuntimeException("## Error: this is not a symbolic expression"); // TODO: fix
        else if (e_leftRef instanceof IntegerConstant)
          return pb.minus(((IntegerConstant)e_leftRef).value,getExpression(e_rightRef));
        else if (e_rightRef instanceof IntegerConstant)
          return pb.minus(getExpression(e_leftRef),((IntegerConstant)e_rightRef).value);
        else
          return pb.minus(getExpression(e_leftRef),getExpression(e_rightRef));
      case MUL:
        if (e_leftRef instanceof IntegerConstant && e_rightRef instanceof IntegerConstant)
          throw new RuntimeException("## Error: this is not a symbolic expression"); // TODO: fix
        else if (e_leftRef instanceof IntegerConstant)
          return pb.mult(((IntegerConstant)e_leftRef).value,getExpression(e_rightRef));
        else if (e_rightRef instanceof IntegerConstant)
          return pb.mult(((IntegerConstant)e_rightRef).value,getExpression(e_leftRef));
		else
		  throw new RuntimeException("## Error: Binary Non Linear Operation");   
	  case DIV:
	    throw new RuntimeException("## Error: Binary Non Linear Operation"); 
	  default:
	    throw new RuntimeException("## Error: Binary Non Linear Operation"); 
      }
    }
    else {
      throw new RuntimeException("## Error: Binary Non Linear Expression " + eRef);
    }
  }
	/**
	 * checking if nc1 is subsumed under nc2
	 * @param nc1 numeric constraint of the first state
	 * @param nc2 numeric constraint of the second state
	 * 
	 */
  public static boolean isConstraintSubsumed(NumericConstraint nc1, NumericConstraint nc2){
    if (nc1 == null || nc2 == null)
      return false;
    if (nc1.isSubsumedBy(nc2))
      return true;
	return false;
  }
  
  public Object getConstraint(PathCondition constraint, int varcount){
    pb = new CVC3Constraint();
    if (varcount == 0) return pb;
    if (fieldIntegerVar == null)
      fieldIntegerVar = new HashMap<SymbolicInteger,Object>();
		
      symRealVar = new HashMap<SymbolicReal,Object>();
      symIntegerVar = new HashMap<SymbolicInteger,Object>();

      if (constraint == null) {
        System.out.println("## Warning: empty path condition");
        return null;
	  }

      Constraint cRef = constraint.header;

      while (cRef != null) {
        boolean constraintResult = true;

        if (cRef instanceof RealConstraint)
          constraintResult= createDPRealConstraint((RealConstraint)cRef);// create choco real constraint
        else if (cRef instanceof LinearIntegerConstraint)
          constraintResult= createDPLinearIntegerConstraint((LinearIntegerConstraint)cRef);// create choco linear integer constraint
        else if (cRef instanceof MixedConstraint)
				// System.out.println("Mixed Constraint");
          constraintResult= createDPMixedConstraint((MixedConstraint)cRef);
        else 
          throw new RuntimeException("## Error: Non Linear Integer Constraint not handled " + cRef);

        if(constraintResult == false) return null;
          cRef = cRef.and;
      }
      ((NumericConstraint)pb).getExpr();
      //System.out.println(((NumericConstraint)pb).isSatisfiable());
      return pb;
  }
}
