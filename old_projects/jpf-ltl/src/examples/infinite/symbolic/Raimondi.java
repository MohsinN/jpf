package infinite.symbolic;

/**
 * A simple program with infinite loops for testing LTL verification
 *
 * @author Franco Raimondi
 */

import gov.nasa.jpf.ltl.LTLSpec;

@LTLSpec("[](<> done() && <> foo())")
//@LTLSpec("[](<> foo())")
public class Raimondi {
  public static void main(String[] args) {
    test(2);
  }

  public static void test(int y) {
    int x = 0;
    while (x != y) {
      System.out.println("x=" + x);
      x = x + 1;
      if (x > 1) {
        x = 0;
      }
      done();
    }
    foo();
  }

  public static void done() {
  }

  public static void foo() {
  }

}
