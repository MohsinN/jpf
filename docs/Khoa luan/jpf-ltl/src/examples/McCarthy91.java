import gov.nasa.jpf.jvm.Verify;
import gov.nasa.jpf.ltl.LTLSpec;

/**
 * This simple program implements McCarthy91 function
 *
 * @author Anh Cuong
 */
//@LTLSpec("<>(McCarthy91.mcCarthy91(int).n==90)")
@LTLSpec("<>(McCarthy91.mcCarthy91(int).n==91)")
public class McCarthy91 {
    /**
     * @param n     the input for mccarthy91 function
     * @return      the mccarthy91 value
     */
    public static int mcCarthy91(int n){
        n = (n > 100) ? n - 10 : mcCarthy91(mcCarthy91(n+11));
        System.out.println(n);
        return n;
    }


    public static void main(String[] args){
      int rand = Verify.random(101);
      //int rand = 101;
      mcCarthy91(rand);
    }

}
