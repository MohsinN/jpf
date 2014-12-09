package finite.symbolic;

import gov.nasa.jpf.ltl.LTLSpec;
import gov.nasa.jpf.symbc.Symbolic;

/**
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 */

@LTLSpec("field == 0 U myMethod(int).z > 0")
public class Until_Sym {
  @Symbolic("true")
  static int field = 0;

  public int myMethod(int param) {
    int z;
    if (param <= 0 && field > 0)
      z = field - param + 1;
    else
      z = param;
    return z; // error when param <= 0 && field <= 0, cannot detect in concrete
              // mode
  }

  public static void main(String[] args) {
    Until_Sym until = new Until_Sym();
    until.myMethod(5);
  }
}
