/*
 * A Simple test to with [](<>a /| <>b), a standard test to
 * verify that Buchi automaton are explored appropriately.
 */


package gov.nasa.jpf.test.jpfltl;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.vm.Verify;
import gov.nasa.jpf.ltl.LTLSpec;
import gov.nasa.jpf.util.TypeRef;
import gov.nasa.jpf.util.test.TestJPF;
import java.util.Random;

import org.junit.Test;

@LTLSpec("([](<>\"done()\" && <>\"foo()\"))")
public class AlwaysDoubleEventuallyTest extends TestJPF {

  public static void main(String[] testMethods) {
    runTestsOfThisClass(testMethods);
  }
  private static final String[] JPF_LTL = {"+listener=gov.nasa.jpf.ltl.ddfs.InfiniteListener",
    "+search.class=gov.nasa.jpf.ltl.ddfs.DDFSearch",
    "+finite=false","+vm.storage.class=gov.nasa.jpf.jvm.FullStateSet",
    "+cg.enumerate_random=true",
    "+debug=true"};

  
  @Test
  public void test1() {

    System.out.println("** this is test_1()");

    //if (verifyNoPropertyViolation(JPF_LTL))
    if (verifyPropertyViolation(new TypeRef("gov.nasa.jpf.ltl.ddfs.LTLProperty"),JPF_LTL)) {

      Random random = new Random();
      int a = 42;
      while (a!=0) {
        a = random.nextInt(2);
        if (a == 0) {
          done();
        }
      }
    }
    if (Verify.isRunningInJPF()) {
      return;
    }
  }

  public static void done() {
  }

  public static void foo() {
  }
}
