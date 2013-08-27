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
public class DoubleEventually1 {

  public static void main(String[] args) {
    Random random = new Random();
    int a, b;
    while (true) {
      a = random.nextInt(2);
      b = random.nextInt(3);
      System.out.println("a = "+a);
      System.out.println("b = "+b);
      if(a==0){
        if (b==2){
          done();
        } else { 
          if (b==1){
            foo();
          } else {
            foo();
          }
        }
      }else{
        Prova p = new Prova (a, b);
        int i = p.getA();
        p.string();
        System.out.println(a + i);
        swap(a,b);
        done();
      }
    }
  }

  public static void done() {
  }

  public static void foo() {
  }

  private static void swap(int a, int b) {
    int c = a;
    a = b;
    b = c;
  }

}
