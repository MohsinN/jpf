/**
 * A simple program with infinite loops for testing LTL verification
 *
 * @author Franco Raimondi
 */

import gov.nasa.jpf.ltl.LTLSpec;

// If you remove the breakTransition it is false.
import gov.nasa.jpf.jvm.Verify;

@LTLSpec("[](<>Raimondi.done() && <>Raimondi.foo())")
public class Raimondi {

    public static void main(String[] args) {
        int y = 11;    
        y = Integer.parseInt(args[0]);
        // loops x from 0 to 9 until x==y
        int x = 0;
        while (x != y) {
            System.out.println(x);
            x = x+1;
            if (x > 9) x = 0;
            done();
            //Verify.breakTransition();
        }
        foo();
        //Verify.breakTransition();
    }
    
    public static void done() {}
    public static void foo() {}

}

