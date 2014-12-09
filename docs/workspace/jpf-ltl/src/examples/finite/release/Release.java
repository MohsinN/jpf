package finite.release;

import gov.nasa.jpf.ltl.LTLSpec;
/**
 * 
 * @author Tuyen Luu
 *
 */
@LTLSpec("b >= 10 V a <= 10")
public class Release {
  static int a;
  static int b;

  public static void test(int x, int y) {
    while (x < 10) {
      a++;
      x++;
      System.out.println(a);
    }
    if (b <= 20)
      b = 10;
    while (b <= 20) {

      b++;
      System.out.println(b);
    }
  }

  public static void main(String[] args) {
    int a = Integer.parseInt(args[0]);
    int b = Integer.parseInt(args[1]);
    test(a, b);

  }
}
