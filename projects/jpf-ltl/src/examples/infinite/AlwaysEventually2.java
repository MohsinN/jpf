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

@LTLSpec("[](<> \"done()\")")
public class AlwaysEventually2 {

  public static void main(String[] args) {
    Random random = new Random();
    while (true) {
      int a = random.nextInt(2);
      if(a==0){
        System.out.println(a);
        int b = random.nextInt(3);
        System.out.println(b);
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
