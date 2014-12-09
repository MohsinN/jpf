package infinite.symbolic;

/**
 * A simple program with infinite loops for testing LTL verification
 *
 * @author Franco Raimondi
 */

import gov.nasa.jpf.ltl.LTLSpec;
import gov.nasa.jpf.symbc.Symbolic;

//@LTLSpec("[](<> done() && <> foo())")
//@LTLSpec("[](done() && foo())")
//@LTLSpec("[](foo())")
//@LTLSpec("<>(foo())")
@LTLSpec("[]((y == 1) -> foo())")
//@LTLSpec("[](y == 1)")
public class Raimondi {
	@Symbolic("true")
	static int y = 0;

	public static void main(String[] args) {
		test(0);
	}

	public static void test(int x) {
		while (true) {
			done();
			if (y != 1) {
				foo();
			}
		}
	}

	public static void done() {
	}

	public static void foo() {
	}
}
