import gov.nasa.jpf.ltl.LTLSpec;

/**
 * 
 */

/**
 * @author Phuc Nguyen Dinh
 *
 */
@LTLSpec("<>Eventually.k==10")
public class Eventually {

  public Eventually() {
    k = 100;
  }
  public Eventually(String abc) {
    k = 200;
  }
    public int k = 3;
    
    public static void main (String [] args) {
        Eventually t = new Eventually ();
        t.foo (1, 2);
    }

    public void foo (int i, float j) {
      if(i < 2 && j > 5)
        k = 10;
      else if(j <= 5)
        k = 10; // pass in concrete mode
      //there still has errors in symbolic mode for example: i >= 2 && j > 5
    }
}
