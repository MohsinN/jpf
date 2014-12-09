/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import gov.nasa.jpf.ltl.finite.trans.Node;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.Set;
import java.util.Vector;

/**
 * This is a {@code ChoiceGenerator} and can be used for branching purpose after
 * executing an <code>Instruction</code> in symbolic mode. Whenever a guard
 * condition (labeling the automaton transition) is enabled the
 * <code>PathCondition</code> object is updated with a symbolic constraint
 * converted from the guard condition. When the <code>PathCondition</code>
 * object changes after executing an instruction, an instance of this class is
 * created and is set to <code>SystemState.nextCG</code> to break the transition
 * in the next step. Instance of this class acts as a
 * <code>PCChoiceGenerator</code>.
 * 
 * @see gov.nasa.jpf.symbc.numeric.PCChoiceGenerator
 * @see gov.nasa.jpf.symbc.numeric.PathCondition
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public class AutomataChoiceGenerator extends PCChoiceGenerator {
  private Vector<PathCondition> pathConditions;

  /**
   * Creates a new instance of this CG. Differs from
   * <code>PCChoiceGenerator</code>, all path conditions must be defined in the
   * initialization.
   * 
   * @param pcs
   *          Satisfied path conditions. Actually, a path condition in this
   *          vector is the conjunction of path condition and the guard
   *          constraint labeling the transition in automata.
   * @param nextStates
   *          The next successors to the current states in next execution step.
   * @param prevStates
   *          Set of states that need to be stored for later backtracking.
   */
  public AutomataChoiceGenerator(Vector<PathCondition> pcs) {
    super(pcs.size());
    pathConditions = pcs;
  }

  /**
   * Gets the path constraint on current branch of the program. The returned
   * path constraint is the conjunction of symbolic execution path condition and
   * the symbolic constraint representing the corresponding guard condition in
   * the automaton's transition for the current branch.
   * 
   * @see gov.nasa.jpf.symbc.numeric.PCChoiceGenerator#getCurrentPC()
   */
  @Override
  public PathCondition getCurrentPC() {
    int nextChoice = getNextChoice();
    assert nextChoice < pathConditions.size();
    PathCondition pc = pathConditions.elementAt(nextChoice);
    if (pc != null)
      return pc.make_copy();
    else
      return null;
  }
}