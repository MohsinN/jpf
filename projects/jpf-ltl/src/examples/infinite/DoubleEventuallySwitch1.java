/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infinite;

/**
 *
 * A simple program with infinite loops for testing LTL verification
 * 
 * @author Michele
 */

import gov.nasa.jpf.ltl.LTLSpec;
import java.util.Random;

@LTLSpec("<> \"done()\" \\/ <>\"foo()\"")
public class DoubleEventuallySwitch1 {

  public static void main(String[] args) {
    Random random = new Random();
    int a, b;
    while (true) {
      a = random.nextInt(2);
      b = random.nextInt(3);
      System.out.println("a = "+a);
      System.out.println("b = "+b);
      if(a==0){
        switch(b){
          case 0: foo();
          case 1: foo();
          case 2: done();
        }
      }else{
        System.out.println(a);
        done();
      }
    }
  }

  public static void done() {
  }

  public static void foo() {
  }

}
