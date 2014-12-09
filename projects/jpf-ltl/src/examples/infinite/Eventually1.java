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

@LTLSpec("<> \"done(int,String,string,Prova)\"")
public class Eventually1 {

  public static void main(String[] args) {
    Random random = new Random();
    while (true) {
      int a = random.nextInt(2);
      if(a==0){
        System.out.println(a);
        done(a);
      }else{
        System.out.println(a);
        done(a);
      }
    }
  }

  public static void done(int a) {
  }

  public static void foo() {
  }

}
