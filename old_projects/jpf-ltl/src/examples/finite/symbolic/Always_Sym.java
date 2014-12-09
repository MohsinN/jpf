package finite.symbolic;

import gov.nasa.jpf.ltl.LTLSpec;
import gov.nasa.jpf.symbc.Symbolic;

/**
 * The checker should stop when there is a path in SUT following a path not
 * enabled by any transitions in automaton. That means there is a path in SUT
 * which automaton cannot move to a new state following that path.
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 */

@LTLSpec("[](counter == 0)")
public class Always_Sym {
  @Symbolic("true")
  static int counter;

  public static void main(String[] args) {
    Always_Sym until = new Always_Sym();
  }

}
