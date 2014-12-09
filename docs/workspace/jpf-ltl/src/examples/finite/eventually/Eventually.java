package finite.eventually;

import gov.nasa.jpf.ltl.LTLSpec;

/**
 * @author Phuc Nguyen Dinh
 * 
 */
@LTLSpec("<> k == 10")
public class Eventually {

  public Eventually() {
    k = 100;
  }

  public Eventually(String abc) {
    k = 200;
  }

  public int k = 3;

  public static void main(String[] args) {
    Eventually t = new Eventually();
    int i = Integer.parseInt(args[0]);
    int j = Integer.parseInt(args[1]);
    t.foo(i, j);
  }

  public void foo(int i, int j) {
    if (i < 2 && j > 5)
      k = 10;
    else if (j < 5)
      k = 10;
    else
      k = i + j;
  }
}
