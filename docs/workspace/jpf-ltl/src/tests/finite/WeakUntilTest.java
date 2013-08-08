package finite;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ltl.LTLSpec;
import gov.nasa.jpf.ltl.finite.LTLListener;

import org.junit.Test;
import gov.nasa.jpf.util.test.TestJPF;

/**
 * 
 * @author Tuyen Luu 
 * test weak until formula
 */
//correct spec
@LTLSpec("k == 0 U (k < 0 W k == 15)")

//incorrect spec
//@LTLSpec("((finite.WeakUntilTest.k < 0) W (finite.WeakUntilTest.k  == 15))")
public class WeakUntilTest extends TestJPF {
  static String[] JPF_ARGS = { "+printTrace=true" };
  static int k;
  static int TIMES = 0;
  public static void foo(int a, int b){
  k = a;
  if (b <= a){
      while ( TIMES < 10){
        TIMES++;
        k++;
        System.out.println("k = " + k);
      }
      k = 15; 
    }
  }

  @Test
  public void test_1() {
    System.out.println("** this is test_1() - it should succeed");

    if (verifyNoPropertyViolation(JPF_ARGS)) {
      foo(-15, -16);
    }
  }

  @Test
  public void test_2() {
    System.out.println("** this is test_2() - Jpf-ltl should detect violation");

    // Currently: the value of k is initiated to 0, so it conforms with the
    // formula
    // ** Bug here **
    // TODO: fix this bug

    if (verifyPropertyViolation(LTLListener.class, JPF_ARGS)) {
      foo(-9, -16);
    }
  }

  @Test
  public void test_3() {
    System.out.println("** this is test_3() - it should succeed");

    if (verifyNoPropertyViolation(JPF_ARGS)) {
      foo(-1, 0);
    }
  }

  @Test
  public void test_4() {
    System.out.println("** this is test_4() - it should succeed");

    if (verifyNoPropertyViolation(JPF_ARGS)) {
      foo(15, 0);
    }
  }

  public static void main(String args[]) {
    runTestsOfThisClass(args);
  }
}
