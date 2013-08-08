package finite;

import org.junit.Test;

import gov.nasa.jpf.ltl.LTLSpec;
import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.ltl.finite.LTLListener;
/**
 * 
 * @author Tuyen Luu
 *
 */
@LTLSpec("i == 0 U test(int,int).a > 8")
class Until {
  static int i = 0;

  public static void test(int a, int b) {
    while (b < 10) {
      b++;
      a++;
      System.out.println(a);
    }
    i = 10;
  }
}

public class UntilTest extends TestJPF {
  static String[] JPF_ARGS = { "+finite=true" };

  public static void main(String args[]) {
    runTestsOfThisClass(args);
  }

  @Test
  public void test_1() {
    System.out.println("this is test_1() - Jpf-ltl should detects violation");
    if (verifyPropertyViolation(LTLListener.class, JPF_ARGS)) {
      Until.test(0, 7);
    }
  }

  @Test
  public void test_2() {
    System.out.println("this is test_2() - it should succeed");
    if (verifyNoPropertyViolation(JPF_ARGS)) {
      Until.test(0, -5);
    }
  }
}
