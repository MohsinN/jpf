package finite.eventually;

import gov.nasa.jpf.ltl.LTLSpec;

/**
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 */
@LTLSpec("<> k#1 + 2 == k#2")
public class MultiInstance {
  int k;
  public void foo() {
    k++;
  }
  
  public static void main(String [] args) {
    new MultiInstance(); 
    MultiInstance a = new MultiInstance();
    a.foo();
    //a.foo();
  }
}
