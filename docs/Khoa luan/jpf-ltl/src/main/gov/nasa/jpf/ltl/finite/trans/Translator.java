/**
 * 
 */
package gov.nasa.jpf.ltl.finite.trans;

import java.util.Vector;

import gov.nasa.ltl.trans.Formula;
import gov.nasa.ltl.trans.Rewriter;

/**
 * @author Phuc Nguyen Dinh
 *
 */
public class Translator {
  public static Graph translate(Formula<String> formula) {
    Graph graph = new Graph();
    Node init = new Node();
    assert init.getId() == 0;
    Vector<Node> nodes = new Vector<Node>();
    //nodes.add(init);
    //Node n = new Node(formula);
    //n.addIncomingNode(init);
    formula = new Rewriter<String> (formula).rewrite();
    init.getNext().add(formula);
    nodes = init.expand(nodes);
    for(Node node: nodes) {
      node.update();
      graph.addNode(node);
    }
    return graph;
  }
}
