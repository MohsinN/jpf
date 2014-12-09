/**
 * 
 */
package gov.nasa.jpf.ltl.ddfs;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import gov.nasa.jpf.ltl.finite.Atom;
import gov.nasa.jpf.ltl.finite.LTLSpecLexer;
import gov.nasa.jpf.ltl.finite.LTLSpec_SymbolicAtom;
import gov.nasa.ltl.graph.Guard;
import gov.nasa.ltl.graph.Literal;
import gov.nasa.ltl.graph.Node;

/**
 * @author estar
 *
 */
class AtomBuchiCG extends BuchiCG<String> {
  public AtomBuchiCG (Node<String> n) {
    super (n);
  }

  /* (non-Javadoc)
   * @see gov.nasa.jpf.ltl.ddfs.BuchiCG#testGuard(gov.nasa.ltl.graph.Guard)
   */
  @Override
  protected boolean testGuard (Guard<String> g) {
    for (Literal<String> l: g) {
      Atom a = stringToAtom (l.getAtom ());
//      System.err.println ("tG: " +
//          (l.isNegated () ? "!" : "") +
//          l.getAtom ());
      // TODO: incomplete!
      if (!(a.isSatisfiable () ^ l.isNegated ()))
        return false;
    }
    return true;
  }
  
  protected Atom stringToAtom (String a) {
    ANTLRStringStream stream = new ANTLRStringStream (a);
    LTLSpec_SymbolicAtom p = new LTLSpec_SymbolicAtom (new CommonTokenStream (
        new LTLSpecLexer (stream)), "");
    try {
      return p.atom ();
    } catch (RecognitionException e1) {
      // TODO
      assert false;
      return null;
    }
  }
}
