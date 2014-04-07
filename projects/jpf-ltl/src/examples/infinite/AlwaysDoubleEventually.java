package infinite;

/**
 * A simple program with infinite loops for testing LTL verification
 *
 * @author Franco AlwaysDoubleEventually
 */

import gov.nasa.jpf.ltl.LTLSpec;

// If you remove the breakTransition it is false.
//import gov.nasa.jpf.jvm.Verify;
import java.util.Random;

@LTLSpec("([](<>\"done()\" && <>\"foo()\"))")
public class AlwaysDoubleEventually {

  public static void main(String[] args) {
    
    Random random = new Random();
    while (true) {
      int a = random.nextInt(2);
      System.out.println(a);
      if (a == 0)
        done();
    }
  }

  public static void done() {
  }
}
