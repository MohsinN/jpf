package finite;

/**
 * A simple program with infinite loops for testing LTL verification
 *
 * @author Franco Raimondi
 */
import gov.nasa.jpf.ltl.LTLSpec;

// If you remove the breakTransition it is false.
// import gov.nasa.jpf.jvm.Verify;

@LTLSpec("[](<> \"done()\" && <> \"foo()\")")
public class AlwaysDoubleEventuallyFinite2 {

  public static void main(String[] args) {
    int y = 11;
    // if (args.length > 0)
    // y = Integer.parseInt(args[0]);
    // loops x from 0 to 9 until x==y
    int x = 0;
    while (x != y) {
      System.out.println(x);
      x = x + 1;
      done();
      // Verify.breakTransition();
    }
    foo();
    // Verify.breakTransition();
  }

  public static void done() {
  }

  public static void foo() {
  }

}
