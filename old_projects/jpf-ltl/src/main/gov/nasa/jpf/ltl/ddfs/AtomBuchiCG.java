/**
 * 
 */
package gov.nasa.jpf.ltl.ddfs;

import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.ltl.atom.Atom;
import gov.nasa.jpf.ltl.finite.LTLListener;
import gov.nasa.jpf.ltl.parser.LTLSpecLexer;
import gov.nasa.jpf.ltl.parser.LTLSpec_SymbolicAtom;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.ltl.graph.Guard;
import gov.nasa.ltl.graph.Literal;
import gov.nasa.ltl.graph.Node;

/**
 * @author Ewgenij Starostin
 * 
 */
class AtomBuchiCG extends BuchiCG<String> {
  public AtomBuchiCG(Node<String> n) {
    super(n);
    originalPC = PathCondition.getPC(JVM.getVM());
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.ltl.ddfs.BuchiCG#testGuard(gov.nasa.ltl.graph.Guard)
   */
  @Override
  protected boolean testGuard(Guard<String> g) {
    for (Literal<String> l : g) {
      Atom a = stringToAtom(l.getAtom());
      // System.err.println ("tG: " +
      // (l.isNegated () ? "!" : "") +
      // l.getAtom ());
      // TODO: incomplete!
      if (!(a.isSatisfiable() ^ l.isNegated()))
        return false;
    }
    return true;
  }

  protected Atom stringToAtom(String a) {
    ANTLRStringStream stream = new ANTLRStringStream(a);
    // TODO: Find a non-static way.
    JVM vm = JVM.getVM();
    LTLSpec_SymbolicAtom p = new LTLSpec_SymbolicAtom(new CommonTokenStream(
        new LTLSpecLexer(stream)), LTLListener.instance.invokedMethodName(vm), true);
    try {
      return p.atom();
    } catch (RecognitionException e1) {
      // TODO
      assert false;
      return null;
    }
  }

  PathCondition originalPC = null;

  @Override
  public void advance() {
    super.advance();
    if (originalPC == null)
      return;
    PathCondition guardPC = originalPC.make_copy();
    for (Literal<String> l : node.getOutgoingEdges().get(index).getGuard()) {
      Atom a = stringToAtom(l.getAtom());
      Set<Constraint> constraints = a.getConstraints();
      for(Constraint c: constraints)
        if (l.isNegated())
          guardPC.prependUnlessRepeated(c.not());
        else
          guardPC.prependUnlessRepeated(c);
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
    }
  }
}
