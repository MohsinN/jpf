package gov.nasa.jpf.symbc.numeric;
import java.util.ArrayList;

import cvc3.*;

public class CVC3Constraint extends  ProblemGeneral implements NumericConstraint{
  protected Expr pb;
  ArrayList<Expr> listConstraint;
  ArrayList<Expr> listExistentialIntegerVar;
  protected final int base = 10; //used in creating real variables
  static ValidityChecker vc = null;
  static FlagsMut flags = null;
  
  public static void initCVC3Constraint(){
    flags = ValidityChecker.createFlags(null);
    flags.setFlag("dagify-exprs",false);
    vc = ValidityChecker.create(flags);
  }
  public CVC3Constraint(){
    listConstraint = new ArrayList<Expr>();
    listExistentialIntegerVar = new ArrayList<Expr>();

  }
  public Object getExpr(){
//if (pb == null) return new
    if (listConstraint.size()!=0){
	  pb = vc.andExpr(listConstraint);
	  if (listExistentialIntegerVar!=null);
	  pb = vc.existsExpr(listExistentialIntegerVar, pb);
	}
	return pb;
  }

  @Override
  public boolean isSubsumedBy(NumericConstraint constraint) {
    vc.push();
    Expr cc = vc.impliesExpr(pb, (Expr)(constraint.getExpr()));
    System.out.println("Implication: " +cc);
    QueryResult sr = vc.query(cc);
		   
    System.out.println(sr.toString());
	if (sr == QueryResult.VALID){
	  vc.pop();
	  return true;
    }
	return false;
  }
  public String toString(){
    if (pb == null) return "NULL";
    return pb.toString();
  }
  public void addExistentialVar(Object dpvar){
    listExistentialIntegerVar.add((Expr)dpvar);
  }
  public void post(Object constraint) {
    try { 
	  listConstraint.add((Expr)constraint);
	} catch (Exception e){
	  e.printStackTrace();
      throw new RuntimeException("## Error CVC3: Exception caught making Int Var in CVC3 ???: \n" + e);
    }
  }
 
  Object makeIntVar(String name, int min, int max) {
    try{
	  /*Type sType = vc.subrangeType(vc.ratExpr(min),
        vc.ratExpr(max));
     	return vc.varExpr(name, sType);*/
	  Expr expr = vc.varExpr(name, vc.intType());
	  return expr;
	} catch (Exception e) {
	  e.printStackTrace();
	  throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
    }
  }
	
  Object makeRealVar(String name, double min, double max) {
		//WARNING: need to downcast double to int - I don't see
		// a way in CVC3 to create a sub-range for real types
		//other choice is not to bound and use vc.realType() to
		//create the expression
    int minInt = (int)min;
	int maxInt = (int)max;
	try{
	  //Expr x = vc.varExpr(name, vc.realType());
	  /*Type sType = vc.subrangeType(vc.ratExpr(minInt),
       vc.ratExpr(maxInt));
	   return vc.varExpr(name, sType);*/
	   Expr expr = vc.varExpr(name, vc.intType());
	   return expr;
	}catch (Exception e) {
	  e.printStackTrace();
	  throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

    }
  }

  Object eq(int value, Object exp){
	try{
	  return  vc.eqExpr(vc.ratExpr(value), (Expr)exp);
	}catch (Exception e) {
	  e.printStackTrace();
	  throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

   }
}

	Object eq(Object exp, int value){
		try{
			return  vc.eqExpr((Expr)exp, vc.ratExpr(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object eq(Object exp1, Object exp2){
		try{
			return  vc.eqExpr((Expr)exp1, (Expr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object eq(double value, Object exp){
		try{
			return  vc.eqExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object eq(Object exp, double value){
		try{
			return  vc.eqExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object neq(int value, Object exp){
		try{
			return  vc.notExpr(vc. eqExpr(vc.ratExpr(value), (Expr)exp));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object neq(Object exp, int value){
		try{
			return  vc.notExpr(vc.eqExpr((Expr)exp, vc.ratExpr(value)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object neq(Object exp1, Object exp2){
		try{
			return  vc.notExpr(vc.eqExpr((Expr)exp1, (Expr)exp2));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	public Object not(Object exp1){
		try{
			return  vc.notExpr((Expr)exp1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object neq(double value, Object exp){
		try{
			return  vc.notExpr(vc.eqExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object neq(Object exp, double value){
		try{
			return  vc.notExpr(vc.eqExpr((Expr)exp, vc.ratExpr(Double.toString(value), base)));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object leq(int value, Object exp){
		try{
			return  vc.leExpr(vc.ratExpr(value), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object leq(Object exp, int value){
		try{
			return  vc.leExpr((Expr)exp, vc.ratExpr(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object leq(Object exp1, Object exp2){
		try{
			return  vc.leExpr((Expr)exp1, (Expr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object leq(double value, Object exp){
		try{
			return  vc.leExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object leq(Object exp, double value){
		try{
			return  vc.leExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object geq(int value, Object exp){
		try{
			return  vc.geExpr(vc.ratExpr(value), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object geq(Object exp, int value){
		try{
			return  vc.geExpr((Expr)exp, vc.ratExpr(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object geq(Object exp1, Object exp2){
		try{
			return  vc.geExpr((Expr)exp1, (Expr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object geq(double value, Object exp){
		try{
			return  vc.geExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object geq(Object exp, double value){
		try{
			return  vc.geExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object lt(int value, Object exp){
		try{
			return  vc.ltExpr(vc.ratExpr(value), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object lt(Object exp, int value){
		try{
			return  vc.ltExpr((Expr)exp, vc.ratExpr(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object lt(Object exp1, Object exp2){
		try{
			return  vc.ltExpr((Expr)exp1, (Expr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object lt(double value, Object exp){
		try{
			return  vc.ltExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object lt(Object exp, double value){
		try{
			return  vc.ltExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}


	Object gt(int value, Object exp){
		try{
			return  vc.gtExpr(vc.ratExpr(value), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object gt(Object exp, int value){
		try{
			return  vc.gtExpr((Expr)exp, vc.ratExpr(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object gt(Object exp1, Object exp2){
		try{
			return  vc.gtExpr((Expr)exp1, (Expr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object implies(Object exp1, Object exp2){
		try{
			return  vc.impliesExpr((Expr)exp1, (Expr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object gt(double value, Object exp){
		try{
			return  vc.gtExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}

	Object gt(Object exp, double value){
		try{
			return  vc.gtExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}




	Object plus(int value, Object exp) {
		try{
			return  vc.plusExpr(vc.ratExpr(value), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object plus(Object exp, int value) {
		try{
			return  vc.plusExpr((Expr)exp, vc.ratExpr(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object plus(Object exp1, Object exp2) {
		try{
			return  vc.plusExpr((Expr)exp1, (Expr)exp1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object plus(double value, Object exp) {
		try{
			return  vc.plusExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object plus(Object exp, double value) {
		try{
			return  vc.plusExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object minus(int value, Object exp) {
		try{
			return  vc.minusExpr(vc.ratExpr(value), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object minus(Object exp, int value) {
		try{
			return  vc.minusExpr((Expr)exp, vc.ratExpr(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object minus(Object exp1, Object exp2) {
		try{
			return  vc.minusExpr((Expr)exp1, (Expr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object minus(double value, Object exp) {
		try{
			return  vc.minusExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object minus(Object exp, double value) {
		try{
			return  vc.minusExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object mult(int value, Object exp) {
		try{
			return  vc.multExpr(vc.ratExpr(value), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object mult(Object exp, int value) {
		try{
			return  vc.multExpr((Expr)exp, vc.ratExpr(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object mult(Object exp1, Object exp2) {
		try{
			return  vc.multExpr((Expr)exp1, (Expr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}
	Object mult(double value, Object exp) {
		try{
			return  vc.multExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}
	Object mult(Object exp, double value) {
		try{
			return  vc.multExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	//TODO

	Object div(int value, Object exp) {
		try{
			return  vc.divideExpr(vc.ratExpr(value), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object div(Object exp, int value) {
		try{
			return  vc.divideExpr((Expr)exp, vc.ratExpr(value));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}

	Object div(Object exp1, Object exp2) {
		try{
			return  vc.divideExpr((Expr)exp1, (Expr)exp2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}
	Object div(double value, Object exp) {
		try{
			return  vc.divideExpr(vc.ratExpr(Double.toString(value), base), (Expr)exp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}
	Object div(Object exp, double value) {
		try{
			return  vc.divideExpr((Expr)exp, vc.ratExpr(Double.toString(value), base));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);
		}
	}
	/*Object makeIntVar(String name, int min, int max) {
		try{
			Expr var = vc.varExpr(name, vc.intType());
            return var;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("## Error CVC3: Exception caught in CVC3 JNI: \n" + e);

	    }
	}*/
	public boolean isSatisfiable(){
		vc.push();
		System.out.println(vc.checkUnsat(pb));
		vc.pop();
		return true;
	}
	@Override
	Object and(int value, Object exp) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object and(Object exp, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object and(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	int getIntValue(Object dpVar) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	double getRealValue(Object dpVar) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	double getRealValueInf(Object dpvar) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	double getRealValueSup(Object dpVar) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	Object mixed(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object or(int value, Object exp) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object or(Object exp, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object or(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object shiftL(int value, Object exp) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object shiftL(Object exp, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object shiftR(int value, Object exp) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object shiftR(Object exp, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Boolean solve() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object xor(int value, Object exp) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object xor(Object exp, int value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	Object xor(Object exp1, Object exp2) {
		// TODO Auto-generated method stub
		return null;
	}
  /* (non-Javadoc)
   * @see gov.nasa.jpf.symbc.numeric.ProblemGeneral#shiftL(java.lang.Object, java.lang.Object)
   */
  @Override
  Object shiftL(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }
  /* (non-Javadoc)
   * @see gov.nasa.jpf.symbc.numeric.ProblemGeneral#shiftR(java.lang.Object, java.lang.Object)
   */
  @Override
  Object shiftR(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }
  /* (non-Javadoc)
   * @see gov.nasa.jpf.symbc.numeric.ProblemGeneral#shiftUR(int, java.lang.Object)
   */
  @Override
  Object shiftUR(int value, Object exp) {
    // TODO Auto-generated method stub
    return null;
  }
  /* (non-Javadoc)
   * @see gov.nasa.jpf.symbc.numeric.ProblemGeneral#shiftUR(java.lang.Object, int)
   */
  @Override
  Object shiftUR(Object exp, int value) {
    // TODO Auto-generated method stub
    return null;
  }
  /* (non-Javadoc)
   * @see gov.nasa.jpf.symbc.numeric.ProblemGeneral#shiftUR(java.lang.Object, java.lang.Object)
   */
  @Override
  Object shiftUR(Object exp1, Object exp2) {
    // TODO Auto-generated method stub
    return null;
  }
}
