package gov.nasa.jpf.ltl.infinite;

import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.ltl.finite.Atom;
import gov.nasa.jpf.ltl.finite.LTLSpecLexer;
import gov.nasa.jpf.ltl.finite.LTLSpec_SymbolicAtom;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.ltl.graph.Guard;
import gov.nasa.ltl.graph.Literal;
import gov.nasa.ltl.graph.Node;

class AtomBuchiCG extends BuchiCG<String> {
	private PathCondition originalPC = null;

	public AtomBuchiCG(Node<String> n) {
		super(n);

		originalPC = PathCondition.getPC(JVM.getVM());
	}

	@Override
	protected boolean testGuard(Guard<String> g) {
		for (Literal<String> l: g) {
			Atom a = stringToAtom(l.getAtom());
			System.err.println("BuchiCG.testGuard " + l.getAtom());
			// TODO: incomplete!
			if (!(a.isSatisfiable() ^ l.isNegated())) {
				return false;
			}
		}
		return true;
	}

	protected Atom stringToAtom(String a) {
		ANTLRStringStream stream = new ANTLRStringStream(a);
		JVM vm = JVM.getVM();
		LTLSpec_SymbolicAtom p = new LTLSpec_SymbolicAtom(new CommonTokenStream(new LTLSpecLexer(stream)), SymbolicLTLListener.invokedMethodName(vm), true);
		try {
			return p.atom();
		}
		catch (RecognitionException e1) {
			e1.printStackTrace();
			assert false : "Can not get atom from literal " + a;
		}
		return null;
  }

	@Override
	public void advance() {
		super.advance();

		System.err.println("AtomBuchiCG.advance: " + toString());

		if (originalPC == null) {
			return;
		}

		PathCondition guardPC = originalPC.make_copy();
		for (Literal<String> l: node.getOutgoingEdges().get(index).getGuard()) {
			Atom a = stringToAtom(l.getAtom());

			Set<Constraint> constraints = a.getConstraints();
			System.err.println("AtomBuchiCG.advance: for guard=" + l.getAtom() + ", atom=" + a.getText() + ", constraints=" + constraints);
			if (constraints == null) {
				continue;
			}
			for(Constraint c: constraints) {
				if (l.isNegated()) {
					guardPC.prependUnlessRepeated(c.not());
				}
				else {
					guardPC.prependUnlessRepeated(c);
				}
			}
		}
		setPC(guardPC);
	}

	// Copied from PathCondition...
	protected void setPC(PathCondition pc) {
		JVM vm = JVM.getVM();
		ChoiceGenerator<?> cg = vm.getChoiceGenerator();
		if (cg != null && !(cg instanceof PCChoiceGenerator)) {
			cg = cg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
		}

		if (cg instanceof PCChoiceGenerator) {
			((PCChoiceGenerator) cg).setCurrentPC(pc);
			System.err.println("AtomBuchiCG.setPc: " + pc);
			return;
		}
		assert false : "Can not set PC " + pc;
	}

	@Override
	public String toString() {
		return super.toString() + ", originalPC=" + originalPC;
	}
}
