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
@LTLSpec("b >= 10 V a <= 10")
class Release {
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

}

public class ReleaseTest extends TestJPF {
  static String[] JPF_ARGS = { "+finite=true" };

  public static void main(String args[]) {
    runTestsOfThisClass(args);
  }

  @Test
  public void test_1() {
    System.out.println("this is test_1() - it should succeed");
    if (verifyNoPropertyViolation(JPF_ARGS)) {
      Release.test(0, 0);
    }
  }

  @Test
  public void test_2() {
    System.out.println("this is test_2() - Jpf-ltl should detects violation");
    if (verifyPropertyViolation(LTLListener.class, JPF_ARGS)) {
      Release.test(-3, 0);
    }
  }

  @Test
  public void test_3() {
    System.out.println("this is test_3() - it should succeed");
    if (verifyNoPropertyViolation(JPF_ARGS)) {
      Release.test(0, 21);
    }
  }

  @Test
  public void test_4() {
    System.out.println("this is test_4() - Jpf-ltl should detects violation");
    if (verifyPropertyViolation(LTLListener.class, JPF_ARGS)) {
      Release.test(-5, 21);
    }
  }
}
