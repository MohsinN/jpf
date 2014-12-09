/**
 * 
 */
package gov.nasa.jpf.ltl.finite.trans;

import java.util.TreeSet;

import gov.nasa.ltl.trans.Formula;

/**
 * This represents a transition in the automta. A transition has a source state
 * and a target state together with a guard condition.
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public class Edge {
  private Guard guard;
  private Node next;
  private Node source;

  public void addGuard(Guard guard) {
    this.guard = guard;
  }

  public Edge() {

  }

  Edge(TreeSet<Formula<String>> atoms, Node src, Node nx) {
    source = src;
    next = nx;
    guard = new Guard(atoms);
  }

  public Guard getGuard() {
    return guard;
  }

  public Node getNext() {
    return next;
  }

  public Node getSource() {
    return source;
  }

  public void setGuard(Guard guard) {
    this.guard = guard;
  }

  public void setNext(Node next) {
    this.next = next;
  }

  public void setSource(Node source) {
    this.source = source;
  }

}
