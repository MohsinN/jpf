/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import gov.nasa.jpf.ltl.finite.trans.Guard;
import gov.nasa.jpf.ltl.finite.trans.Literal;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

/**
 * This stores and checks for the satisfiability of a guard condition labeling
 * automaton transition.
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 */

public class AutomataGuard {
  private Guard guard;
  private String invokedMethod;
  private PathCondition currentPC;
  private boolean isSatisfiable;
  private boolean isPCChanged;
  private Set<Constraint> constraints;

  /**
   * Constructor.
   * 
   * @param guard
   *          Guard constraint labeling the automata's transition.
   * @param currentPC
   *          The current symbolic execution <code>PathCondition</code>.
   * @param longName
   *          The signature of the invoked method in
   *          <code>InvokeInstruction</code>, empty string other wise.
   */
  public AutomataGuard(Guard guard, PathCondition currentPC, String longName) {
    invokedMethod = longName;
    this.guard = guard;
    this.currentPC = currentPC.make_copy();
    isSatisfiable = isGuardSatisfiable();
  }

  public Set<Constraint> getConstraints() {
    if (constraints == null)
      constraints = new TreeSet<Constraint>();
    return constraints;
  }

  /**
   * 
   * @return The current path condition.
   */
  public PathCondition getPathCondition() {
    return currentPC;
  }

  /*
   * Because a guard is the conjunction of literals (atom or its negation),
   * checking its satisfiability involves checking all of its literals.
   */
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
   * Indicates if <code>PathCondition</code> object changed after checking if
   * the guard condition is enabled.
   * 
   * @return {@code true} if the current path condition is changed, {@code
   *         false} otherwise.
   * @see gov.nasa.jpf.symbc.numeric.Constraint
   */
  public boolean isPCChanged() {
    return isPCChanged;
  }

  /**
   * 
   * @return {@code true} if <code>guard</code> is satisfiable, {@code false}
   *         otherwise.
   */
  public boolean isSatisfiable() {
    return isSatisfiable;
  }

  /*
   * Checks satisfiability of a literal. Checking a literal is broken down to
   * checking its atom.
   */
  private boolean parseAtom(Literal literal) {
    String propT = literal.getAtom();
    ANTLRStringStream input = new ANTLRStringStream(propT);
    LTLSpecLexer lexer = new LTLSpecLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    LTLSpec_SymbolicAtom parser = new LTLSpec_SymbolicAtom(tokens,
        invokedMethod, true);
    Atom atom = null;
    try {
      atom = parser.atom();
    } catch (RecognitionException e) {
      e.printStackTrace();
    }

    constraints = atom.getConstraints();

    // symbolic mode
    if (constraints != null && !constraints.isEmpty()) {
      for (Constraint constraint : constraints) {
        if (literal.isNegated())
          constraint = constraint.not();

        // if this is a symbolic atom, we need to combine the constraint
        // representing it with the current path condition
        if (currentPC.prependUnlessRepeated(constraint))
          isPCChanged = true;
      }

      // A symbolic value represents a set of concrete values,
      // we consider it as a symbolic constraint and determine whether
      // it is satisfiable in the current symbolic state of SUT.
      if (!currentPC.simplify())
        return false;
    }

    // concrete mode
    if (literal.isNegated())
      return !atom.isSatisfiable();
    else
      return atom.isSatisfiable();
  }

}
