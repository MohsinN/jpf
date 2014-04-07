import gov.nasa.jpf.ltl.LTLSpec;

/**
 * This simple program contains method invocation,
 * assignment statements and control statements
 * 
 * @author Anh Cuong
 */
@LTLSpec("[]( Simple.f2(int, int) -> X(<> Simple.f1(String)))")
//@LTLSpec("[]!Simple.f2(int, int)")//
public class Simple {

  public static void main(String[] args) {
    f2(5, 4);
  }

  public static void f1(String a) {
	  System.out.println(a);
  }

  public static void f3() {
  }

  public static int f2(int x, int y) {
    if (x > 3){
      if (y < 5){
        f1("a");
        return 1;
      }
      f3();
      return 100;
    }

    if (y < 5) {
      f1("a");
      System.out.println("abc");
      return 100;
    }
    f3();
    return 100;
  }
}
