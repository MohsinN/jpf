/**
 * 
 */
package gov.nasa.jpf.ltl.finite.trans;

import gov.nasa.ltl.trans.Formula;
import gov.nasa.ltl.trans.Formula.Content;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * A guard is a conjunction of literals. This enables the transition which it is
 * attached.
 * 
 * @author Phuc Nguyen Dinh - luckymanphu@gmail.com
 * 
 */
public class Guard {
  private List<Literal> literals;
  private boolean isTrue;

  public Guard(boolean isTrue) {
    this.isTrue = isTrue;
    literals = new ArrayList<Literal>();
  }

  public Guard(gov.nasa.ltl.graph.Guard<String> buchiGuard) {
    isTrue = buchiGuard.isTrue();
    literals = new ArrayList<Literal>();
    for (gov.nasa.ltl.graph.Literal<String> buchiLiteral : buchiGuard) {
      literals
          .add(new Literal(buchiLiteral.getAtom(), buchiLiteral.isNegated()));
    }
  }

  Guard(TreeSet<Formula<String>> atoms) {
    literals = new ArrayList<Literal>();
    if (atoms.size() == 1 && atoms.first().getContent() == Content.TRUE) {
      isTrue = true;
    } else {
      for (Formula<String> atom : atoms) {
        if (atom.isLiteral() && atom.getContent() != Content.TRUE) {
          literals.add(new Literal(atom));
        }
      }
    }
  }

  public void addLiteral(Literal literal) {
    literals.add(literal);
  }

  public List<Literal> getLiterals() {
    return literals;
  }

  public boolean isTrue() {
    return isTrue || literals.size() == 0;
  }

}
