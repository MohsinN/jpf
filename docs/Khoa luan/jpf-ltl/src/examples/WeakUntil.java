import gov.nasa.jpf.ltl.LTLSpec;
/**
 * 
 * @author Tuyen Luu
 * test weak until formula
 */
@LTLSpec("(WeakUntil.k < 10) W (WeakUntil.k  == 15)")
public class WeakUntil {
  static int k = 0;
  static int TIMES = 0;
  public static void test(int a, int b){
	k = a;
	if (b <= a){
    while ( TIMES < 10){
      TIMES++;
      k++;
      System.out.println("k = " + k);
    }
	}
    if (k < 10) k = 15; 
  }
  public static void main(String args[]){
    int a = Integer.parseInt(args[0]);
    int b = Integer.parseInt(args[1]);
    test(a,b);
  }
}
