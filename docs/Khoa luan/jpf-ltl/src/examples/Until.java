import gov.nasa.jpf.ltl.LTLSpec;

@LTLSpec("Until.i == 0 U Until.test(int).a > 8")
public class Until {
  static int i = 0;
  static int [] [] a;
  static float [] b = new float [4];
  GCD gcd;
  static int k = 0;
  public static void test(int a){
	while (a < 10){
      a++;
      System.out.println(a);
	}
	i = 10;
  }
  public static void main(String args[]){
    test(0);
  }
}
