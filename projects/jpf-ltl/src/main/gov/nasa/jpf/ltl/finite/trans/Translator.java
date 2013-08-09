/**
 * 
 */
package gov.nasa.jpf.ltl.finite.trans;

import gov.nasa.ltl.trans.Formula;
import gov.nasa.ltl.trans.Rewriter;

import java.util.Vector;

/**
 * The LTL to FSA translator. This translates an LTL formula into an FSA which
 * can be used to check in finite execution traces.
 * 
 * @author Phuc Nguyen Dinh - luckymanphu@gmail.com
 * 
 */
public class Translator {
  public static Graph translate(Formula<String> formula) {
    Graph graph = new Graph();
    Node init = new Node();
    assert init.getId() == 0;
    Vector<Node> nodes = new Vector<Node>();
    formula = new Rewriter<String>(formula).rewrite();

    // starts with the initial node and the formula for which the automaton is
    // being built
    // the formula then be added to the next field of the initial node
    init.getNext().add(formula);

    // other nodes are expanded from the initial node by splitting the formula
    nodes = init.expand(nodes);

    // updates the accepting condition for the received set of nodes then add
    // them to the automaton
    for (Node node : nodes) {
      node.update();
      graph.addNode(node);
    }
    return graph;
  }
}
