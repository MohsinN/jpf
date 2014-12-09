import gov.nasa.jpf.ltl.LTLSpec;

/**
 * This simple program contains method invocation, assignment statements and
 * control statements
 * 
 * @author Anh Cuong
 */
@LTLSpec("[](f2(int,int) -> X(<> f1(String)))")
// @LTLSpec("[]!Simple.f2(int,int)")//
public class Simple {

  public static void main(String[] args) {
    assert args.length == 2;
    f2(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
  }

  public static void f1(String x) {
  }

  public static void f3() {
  }

  public static int f2(int x, int y) {
    if (x > 3) {
      if (y < 5) {
        f1("");
        return 1;
      }
      f3();
      return 100;
    }

    if (y < 5) {
      f1("");
      return 100;
    }
    f3();
    return 100;
  }
}
