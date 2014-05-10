package infinite.symbolic;

/**
 * A simple program with infinite loops for testing LTL verification
 *
 * @author Franco AlwaysDoubleEventually
 */

import gov.nasa.jpf.ltl.LTLSpec;

//@LTLSpec("([](<>\"done()\" && <>\"foo()\"))")
@LTLSpec("([](<>\"done()\"))")
public class AlwaysDoubleEventually {

  public static void main(String[] args) {
    test(1);
  }
  
  public static void test(int x) {
    while (true) {
      if (x != 1)
        done();
    }
  }

  public static void done() {
  }
}
