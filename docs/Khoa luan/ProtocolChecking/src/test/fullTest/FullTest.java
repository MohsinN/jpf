package fullTest;

import java.util.Random;
import gov.nasa.jpf.symbc.*;
import annotations.*;

/**
 * Full test class
 * 
 * @author Phuc Nguyen Dinh
 * 
 */
public class FullTest {
	O1 a1, a2;
	L2 b;
	L3 c;

	public FullTest() {
		a1 = new O1();
		a2 = new O1();
		b = new L2();
		c = new L3();
	}

	public static void main(String[] args) {
		FullTest test = new FullTest();
		// test.fullTest(1, 2);
		test.skipOptTest(1, 2);
		test.lackMessageFailTest(1, 2);
		/*
		 * test.ifAltTest(true); test.elseAltTest(); test.skipLoopTest();
		 * test.invalidBeginTest(); test.ifAndElseAltFailTest();
		 * test.notFinishedTest();
		 * test.coincideClassNameAndMessageNameFailTest();
		 * test.lackMessageFailTest(4, 2);
		 */
	}

	/**
	 * Pass test that skip opt fragment. Note: In this situation, opt is end of
	 * diagram so, the state machine have two final state and skip option
	 * fragment is still be allow.
	 */
	@UmlSpec(sequenceDiagram = { ".\\xmi\\fullTest.xmi" })
	public void skipOptTest(int x, int y) {
		if (x < y) {
			System.out.print("x < y ");
			b.m();
		} else {
			System.out.print("x >= y ");
			a1.m6();
		}
		// else region
		elseAlt();
		if (x + y < 12) {
			a2.m6();
			System.out.print("x + y < 12 ");
		}
		// loop region
		if (x > 2) {
			System.out.print("x >>>>>>> 2");
			System.out.println("");
			// loop(3);
		}

		c.m7();

		// opt region
		opt();
	}

	/**
	 * Fail test that execute both two blocks in alternative fragment
	 */
	public void ifAndElseAltFailTest() {
		b.m();
		// if region
		ifAlt();
		elseAlt();

		a2.m6();

		// loop region
		loop(3);

		c.m7();

		// opt region
		opt();
	}

	/**
	 * Pass Test that execute the first block in alternative fragment
	 */
	// @Protocol(".\\xmi\\fullTest.xmi")
	public void ifAltTest(boolean x) {
		if (x)
			b.m();

		// if region
		ifAlt();

		a2.m6();

		// loop region
		loop(3);

		c.m7();

		// opt region
		opt();
	}

	/**
	 * Pass test that execute the second block in alternative fragment
	 */
	public void elseAltTest() {
		b.m();
		// else region
		elseAlt();

		a2.m6();

		// loop region
		loop(3);

		c.m7();

		// opt region
		opt();
	}

	/**
	 * Pass test that skip loop fragment
	 */
	public void skipLoopTest() {
		b.m();
		// else region
		elseAlt();

		a2.m6();

		// loop region
		loop(0);

		c.m7();

		// opt region
		opt();
	}

	/**
	 * Fail test that we ignore the first message
	 */
	public void invalidBeginTest() {
		// skip first message
		// b.m();

		// if region
		ifAlt();

		a2.m6();

		// loop region
		loop(3);

		c.m7();

		// opt region
		opt();
	}

	/**
	 * Fail test that we ignore some end last messages (former messages must be
	 * correct in order)
	 */
	public void notFinishedTest() {
		b.m();
		// if region
		ifAlt();

		a2.m6();

		// loop region
		loop(3);

		// c.m7();

		// opt region
		// opt();
	}

	/**
	 * Fail test that we replace the expected object by another coincide class
	 * object in a message
	 */
	public void coincideClassNameAndMessageNameFailTest() {
		b.m();
		// if region
		ifAlt();

		// expect is a2.m6() but we use a1.m6()
		a1.m6();

		// loop region
		loop(3);

		c.m7();

		// opt region
		opt();
	}

	/**
	 * Fail test that we ignore a message
	 */
	@UmlSpec(sequenceDiagram = { ".\\xmi\\fullTest.xmi" })
	public void lackMessageFailTest(int x, int y) {
		if (x < y)
			b.m();
		// if region
		ifAlt();

		// lack message
		// a2.m6();

		// loop region
		loop(3);

		c.m7();

		// opt region
		opt();
	}

	public void fullTest(int z, int a) {
		b.m();
		if (z < a)
			ifAlt();
		// else
		// elseAlt();
		// Debug.printPC("fullTest pc1" );
		a2.m6();
		loop(3);
		c.m7();
		opt();
	}

	private void ifAlt() {
		a2.m2();
		c.m3();
	}

	private void elseAlt() {
		c.m4();
		a2.m5();
	}

	private void loop(int time) {
		for (int i = 0; i < time; i++) {
			c.m7();
			a1.m6();
			b.m8();
		}
	}

	private void opt() {
		b.m();
		a2.m6();
	}
}