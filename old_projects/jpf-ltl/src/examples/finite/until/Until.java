package finite.until;

import gov.nasa.jpf.ltl.LTLSpec;

//before a>8, i has to equal 0
@LTLSpec("i == 0 U test(int,int).a > 8")
public class Until {
  static int i = 0;

  public static void test(int a, int b) {
    while (b < 10) {
      b++;
      a++;
      System.out.println(a);
    }
    i = 10;
  }

  public static void main(String args[]) {
    Integer a = Integer.parseInt(args[0]);
    Integer b = Integer.parseInt(args[1]);
    test(a, b);
  }
}
