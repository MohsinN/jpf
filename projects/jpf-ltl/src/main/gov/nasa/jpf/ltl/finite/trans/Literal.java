/**
 * 
 */
package gov.nasa.jpf.ltl.finite.trans;

import gov.nasa.ltl.trans.Formula;
import gov.nasa.ltl.trans.Formula.Content;

/**
 * @author Phuc Nguyen Dinh
 * 
 */
public class Literal {
  private String atom;
  private boolean isNegated;

  public Literal(String atom, boolean isNegated) {
    this.atom = atom;
    this.isNegated = isNegated;
  }

  Literal(Formula<String> f) {
    assert f.isLiteral();
    if (f.getContent() == Content.NOT) {
      isNegated = true;
      atom = f.getSub1().getName();
    } else
      atom = f.getName();
  }

  public String getAtom() {
    return atom;
  }

  public boolean isNegated() {
    return isNegated;
  }

  public void setAtom(String atom) {
    this.atom = atom;
  }

  public void setNegated(boolean isNegated) {
    this.isNegated = isNegated;
  }

}
