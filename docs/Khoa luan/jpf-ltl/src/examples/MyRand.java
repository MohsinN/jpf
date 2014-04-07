
import java.util.Random;

import gov.nasa.jpf.ltl.LTLSpec;
import gov.nasa.jpf.ltl.finite.*;
/**
 * @author Tuyen Luu
 * An examples from jpf-core
 *
 */

@LTLSpec("[](!(MyRand.main(String []).c == 2))")
public class MyRand {
  public static void main (String[] args) {

    //System.out.println("computing c = a/(b+a - 2)..");
    Random random = new Random(42);      // (1)

    int a = random.nextInt(2);           // (2)
    System.out.println("a=" + a);

    //... lots of code here

    int b = random.nextInt(3);           // (3)
    System.out.println("  b=" + b + "       ,a=" + a);

    int c = b+a;                  // (4)
    System.out.println("=>  a + b = "+c);
  }  
}
