package finite.weakuntil;
import gov.nasa.jpf.ltl.LTLSpec;
/**
 * 
 * @author Tuyen Luu
 * test weak until formula
 */
//correct spec
@LTLSpec("k == 0 U (k < 0 W k == 15)")

//incorrect spec
//@LTLSpec("((finite.weakuntil.WeakUntil.k < 0) W (finite.weakuntil.WeakUntil.k  == 15))")
public class WeakUntil {
  static int k;
  static int TIMES = 0;
  public static void test(int a, int b){
	k = a;
	if (b <= a){
      while ( TIMES < 10){
        TIMES++;
        k++;
        System.out.println("k = " + k);
      }
      k = 15; 
	  }
  }
  public static void main(String args[]){
    int a = Integer.parseInt(args[0]);
    int b = Integer.parseInt(args[1]);
    test(a,b);
  }
}
