/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import java.util.Set;
import java.util.Vector;

import gov.nasa.jpf.ltl.finite.trans.Node;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;

/**
 * We treat every execution step as the branching point, so a ChoiceGenerator is
 * created and set to the nextCG of the SystemState to break transition.
 * Instance of this class acts as an PCChoiceGenerator. It return true when an
 * symbolic bytecode instruction check for (pc instanceof PCChoiceGenerator) and
 * hold the current path condition.
 * 
 * @author Phuc Nguyen Dinh
 * 
 */
public class AutomataChoiceGenerator extends PCChoiceGenerator {
  private Vector<PathCondition> pathConditions;
  private Set<Node> successors;
  private Set<Node> prevState;

  /**
   * Differ from the PCChoiceGenerator, all path conditions must defined in the
   * initialization.
   * 
   * @param pcs
   *          Satisfied path conditions. Actually, a path condition in this list
   *          is the conjunction of symbolic path condition and the Buchi guard
   *          constraint.
   */
  public AutomataChoiceGenerator(Vector<PathCondition> pcs, Set<Node> nextStates, Set<Node> prevState) {
    super(pcs.size());
    pathConditions = pcs;
    successors = nextStates;
    this.prevState = prevState;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.symbc.numeric.PCChoiceGenerator#getCurrentPC()
   */
  public PathCondition getCurrentPC() {
    int nextChoice = getNextChoice();
    assert nextChoice < pathConditions.size();
    PathCondition pc = pathConditions.elementAt(nextChoice);
    if (pc != null)
      return pc.make_copy();
    else
      return null;
  }
  
  public Set<Node> getCurrentState() {
    return successors;
  }
  
  public Set<Node> getPrevState() {
    return prevState;
  }
}