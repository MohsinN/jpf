package infinite.symbolic;

/**
 * A simple program with infinite loops for testing LTL verification
 *
 * @author Franco Raimondi
 */

import gov.nasa.jpf.ltl.LTLSpec;

//@LTLSpec("[](<> done() && <> foo())")
//@LTLSpec("[](done() && foo())")
//@LTLSpec("[](foo())")
@LTLSpec("<>(foo())")
public class Raimondi {
	public static void main(String[] args) {
		test(1);
	}

	public static void test(int y) {
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
