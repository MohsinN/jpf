/**
 * 
 */

import java.util.Vector;

import gov.nasa.jpf.ltl.LTLSpec;
import gov.nasa.jpf.ltl.LTLSpecFile;

/**
 * @author Phuc Nguyen Dinh
 * 
 */
@LTLSpecFile("src/examples/sample.ltl")
public class FormulaSample extends Parent {
  float declared;
  static boolean staticDeclared = true;

  public static void main(String[] args) {
    FormulaSample sample = new FormulaSample();
    String a = null;
    sample.test(2, a);
  }

  /**
   * A sample method annotated with a ltl formula
   */
  public void test(float i, String a) {
    int local1 = 1;
    float localInit = i;
    declared = 2;
    if (i < 3) {
      declared = i + 1;
      // System.out.println(declared);
      // System.out.println(declare2);
    } else {
      declared = 5;

    }
  }
}

/*
 * Buchi automata of negated formula
 * 
 * never { S0: if :: (1) -> goto S0 :: (a && !b) -> goto accept_S1 fi;
 * accept_S1: if :: (!b) -> goto accept_S1 fi;
 * 
 * }
 */
