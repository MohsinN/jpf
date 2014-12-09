/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package finite;

/**
 *
 * A simple program with infinite loops for testing LTL verification
 * 
 * @author Michele
 */

import gov.nasa.jpf.ltl.LTLSpec;

@LTLSpec("[](<>\"done()\" \\/ <>\"foo()\")")
public class AlwaysDoubleEventuallyFinite1 {

  public static void main(String[] args) {
    int i=-1;
    while (i!=5){
      i = (int)(Math.random()*10);
      if(i<5){
        System.out.println(i);
        done();
      }else{
        System.out.println(i);
        foo();
      }
    }
    foo();
  }

  public static void done() {
  }

  public static void foo() {
  }

}
