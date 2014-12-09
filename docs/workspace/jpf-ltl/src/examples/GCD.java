import gov.nasa.jpf.ltl.LTLSpec;
import gov.nasa.jpf.ltl.finite.*;

/**
 * 
 * @author Tuyen Luu
 * 
 *         greatest common divisor algorithm
 * 
 */
// @LTLSpec("<>(GCD.x%GCD.z == 0 &&GCD.y%GCD.z == 0")
@LTLSpec("<>GCD.isGCD")
public class GCD {
  static boolean isGCD;

  static int gcd(int a, int b) {
    if (a == 0)
      return b;
    while (b != 0) {
      if (a > b)
        a = a - b;
      else
        b = b - a;
    }
    return a;
  }

  static void testGCD(int a, int b) {
    int x = a;
    int y = b;
    int z = gcd(a, b);
    isGCD = ((x % z == 0) && (y % z == 0));
    System.out.println(x + " " + y + " " + z);
  }

  public static void main(String args[]) {
    int a = Integer.parseInt(args[0]);
    int b = Integer.parseInt(args[1]);
    testGCD(a, b);
  }
}
