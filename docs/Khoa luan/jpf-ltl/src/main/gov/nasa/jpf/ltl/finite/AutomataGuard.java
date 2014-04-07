/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import java.util.List;

import gov.nasa.jpf.ltl.finite.trans.Guard;
import gov.nasa.jpf.ltl.finite.trans.Literal;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 * This stores the guard constraint of Buchi's transition. It can be used to
 * check for satisfiability of guard constraint and conjunction of guard
 * constraint with the current symbolic path condition.
 * 
 * @author Phuc Nguyen Dinh
 * 
 */

public class AutomataGuard {
  private Guard guard;
  private String invokedMethod;
  private PathCondition currentPC;
  private boolean isSatisfiable;
  private boolean isPCChanged;

  /**
   * @param guard
   *            transition's constraint
   * @param currentPC
   *            symbolic execution path condition
   * @param longName
   *            method name of current instruction
   */
  public AutomataGuard(Guard guard, PathCondition currentPC,
      String longName) {
    invokedMethod = longName;
    this.guard = guard;
    this.currentPC = currentPC.make_copy();
    isSatisfiable = isGuardSatisfiable();
  }

  /**
   * Get the conjunction of guard constraint and the symbolic path condition if
   * it is satisfiable
   * 
   * @return The combined path condition, null if it's not satisfiable
   */
  public PathCondition getPathCondition() {
    return currentPC;
  }

  private boolean isGuardSatisfiable() {
    if (guard.isTrue())
      return true;
    List<Literal> literals = guard.getLiterals();
    for (Literal literal : literals) {
      if (!parseAtom(literal))
        return false;
    }
    return true;
  }

  /**
   * Check if the conjunction of the guard and the symbolic path condition is
   * satisfiable
   * 
   * @return true if it's satisfiable
   */
  public boolean isSatisfiable() {
    return isSatisfiable;
  }
  
  public boolean isPCChanged() {
    return isPCChanged;
  }

  /*
   * Indicate whether an atomic propositions is satisfiable or not
   * 
   * @param literal
   *          the atomic proposition need to check
   * @return true if atom is satisfiable
   */
  private boolean parseAtom(Literal literal) {
    String propT = literal.getAtom();
    ANTLRStringStream input = new ANTLRStringStream(propT);
    LTLSpecLexer lexer = new LTLSpecLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    LTLSpec_SymbolicAtom parser = new LTLSpec_SymbolicAtom(tokens, invokedMethod);
    Atom atom = null;
    try {
      atom = parser.atom();
    } catch (RecognitionException e) {
      e.printStackTrace();
    }

    boolean result;
    if (literal.isNegated())
      result = !atom.isSatisfiable();
    else
      result = atom.isSatisfiable();

    if (result) {
      Constraint t = atom.getConstraint();
      if(t != null)
        if(currentPC.prependUnlessRepeated(t))
          isPCChanged = true;
      return currentPC.simplify();
    }
    return false;
  }
}
