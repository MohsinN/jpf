package finite;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ltl.LTLSpec;
import gov.nasa.jpf.util.test.TestJPF;
import org.junit.Test;
import gov.nasa.jpf.ltl.finite.LTLListener;

/**
 * @author Phuc Nguyen Dinh
 * 
 */
@LTLSpec("<>k==10")
class Eventually {
  public int k = 3;

  public Eventually() {
    k = 100;
  }

  public Eventually(String abc) {
    k = 200;
  }

  public void foo(int i, int j) {
    if (i < 2 && j > 5)
      k = 10;
    else if (j < 5)
      k = 10;
    else
      k = i + j;
    // assert k==10;
  }
}

public class EventuallyTest extends TestJPF {
  static String[] JPF_ARGS = { "+finite=true" };
  Eventually e = new Eventually();

  public static void main(String[] args) {
    runTestsOfThisClass(args);
  }

  @Test
  public void test_1() {
    System.out.println("** this is test_1() - it should succeed");

    if (verifyNoPropertyViolation(JPF_ARGS)) {
      e.foo(1, 2);
    }

  }

  @Test
  public void test_2() {
    System.out.println("** this is test_2() - Jpf-ltl should detect errors");
    if (verifyPropertyViolation(LTLListener.class, JPF_ARGS)) {
      e.foo(3, 6);
    }

  }

  @Test
  public void test_3() {
    System.out.println("** this is test_3() - it should succeed");
    if (verifyNoPropertyViolation()) {
      e.foo(5, 5);
    }
  }

  @Test
  public void test_4() {
    System.out.println("** this is test_4() - Jpf-ltl should detect errors");
    if (verifyPropertyViolation(LTLListener.class, JPF_ARGS)) {
      e.foo(3, 5);
    }
  }

}
