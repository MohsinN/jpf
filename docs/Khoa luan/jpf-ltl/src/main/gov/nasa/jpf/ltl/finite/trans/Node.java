/**
 * 
 */
package gov.nasa.jpf.ltl.finite.trans;

import gov.nasa.ltl.trans.Formula;
import gov.nasa.ltl.trans.Formula.Content;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

/**
 * @author Phuc Nguyen Dinh
 * 
 */
public class Node implements Comparable<Node> {
  private List<Edge> incoming;
  private List<Edge> outgoing;

  // the set of nodes that lead to this node
  private Vector<Node> incomingNode;

  // the set of ltl formula that must hold on the current state but have not
  // been processed yet
  private TreeSet<Formula<String>> newFormula;

  // the set of ltl formulae that have already been processed. Each formula in
  // newFormula
  // that gets processed is transferred to oldFormula
  // private TreeSet<Formula<String>> oldFormula;
  private Vector<TreeSet<Formula<String>>> oldFormula;

  // the set of ltl formulae that must hold at all immediate successor of this
  // node
  private TreeSet<Formula<String>> next;

  private boolean isAccepting;

  private int id;

  Node() {
    incoming = new ArrayList<Edge>();
    outgoing = new ArrayList<Edge>();
    incomingNode = new Vector<Node>();
    newFormula = new TreeSet<Formula<String>>();
    oldFormula = new Vector<TreeSet<Formula<String>>>();
    oldFormula.add(new TreeSet<Formula<String>>());
    next = new TreeSet<Formula<String>>();
  }

  public Node(boolean isAccepting, int id) {
    incoming = new ArrayList<Edge>();
    outgoing = new ArrayList<Edge>();
    this.isAccepting = isAccepting;
    this.id = id;
  }

  Node(Formula<String> f) {
    this();
    newFormula.add(f);
  }

  private Node(Vector<Node> in, TreeSet<Formula<String>> newForm,
      Vector<TreeSet<Formula<String>>> done, TreeSet<Formula<String>> nx) {
    incoming = new ArrayList<Edge>();
    outgoing = new ArrayList<Edge>();
    incomingNode = new Vector<Node>(in);
    newFormula = new TreeSet<Formula<String>>(newForm);
    oldFormula = new Vector<TreeSet<Formula<String>>>();
    for (TreeSet<Formula<String>> old : done) {
      oldFormula.add(new TreeSet<Formula<String>>(old));
    }
    next = new TreeSet<Formula<String>>(nx);
  }

  void addIncomingNode(Node node) {
    incomingNode.add(node);
  }

  public void addIncomingEdge(Edge edge) {
    incoming.add(edge);
  }

  public void addOutgoingEdge(Edge edge) {
    outgoing.add(edge);
  }

  private void addToNew(Formula<String> f) {
    if (!oldFormula.get(0).contains(f))
      newFormula.add(f);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Node o) {
    if (this.equals(o))
      return 0;
    else
      return 1;
  }

  Vector<Node> expand(Vector<Node> nodes) {
    if (isProcessed()) {
      Node temp = null;
      for (Node curNode : nodes) {
        if (curNode.getNext().equals(next))
          temp = curNode;
      }
      if (temp != null) {
        temp.modify(this);
        return nodes;
      } else {
        nodes.add(this);
        Node newNode = getImmediate();
        return newNode.expand(nodes);
      }
    } else {
      Formula<String> f = newFormula.first();
      newFormula.remove(f);
      if (isContradicting(f))
        return nodes;
      if (f.isLiteral()) {
        if(f.getContent() != Content.TRUE)
          oldFormula.get(0).add(f);
        return expand(nodes);
      } else {
        switch (f.getContent()) {
        case NEXT:
          next.add(f.getSub1());
          return expand(nodes);
        case AND:
          addToNew(f.getSub1());
          addToNew(f.getSub2());
          return expand(nodes);
        case OR:
        case UNTIL:
        case WEAK_UNTIL:
        case RELEASE:
          Node temp = split(f);
          return temp.expand(this.expand(nodes));
        default:
          System.out.println("Expand switch entered");
          return null;
        }
      }
    }
  }

  public int getId() {
    return id;
  }

  private Node getImmediate() {
    Node node = new Node();
    node.incomingNode.add(this);
    node.newFormula.addAll(next);
    return node;
  }

  public List<Edge> getIncomingEdges() {
    return incoming;
  }

  TreeSet<Formula<String>> getNewFormula() {
    return newFormula;
  }

  TreeSet<Formula<String>> getNext() {
    return next;
  }

  public List<Edge> getOutgoingEdges() {
    return outgoing;
  }

  public boolean isAccepting() {
    return isAccepting;
  }

  private boolean isContradicting(Formula<String> f) {
    if (!f.isLiteral())
      return false;
    if (f.getContent() == Content.FALSE)
      return true;
    if (f.getContent() == Content.NOT) {
      return oldFormula.get(0).contains(f.negate());
    } else {
      for (Formula<String> old : oldFormula.get(0)) {
        if (old.negate().equals(f))
          return true;
      }
      return false;
    }
  }

  boolean isProcessed() {
    return newFormula.isEmpty();
  }

  private void modify(Node node) {
    // incomingNode.addAll(node.incomingNode);
    for (Node in : node.incomingNode) {
      boolean edgeExisted = false;
      for (int i = 0; i < incomingNode.size(); i++) {
        if (incomingNode.get(i).equals(in)
            && oldFormula.get(i).equals(node.oldFormula.get(0)))
          edgeExisted = true;
      }
      if (edgeExisted)
        continue;

      if (incomingNode.size() != 0) {
        incomingNode.add(in);
        oldFormula.add(node.oldFormula.get(0));
      } else {// the equal state already in the graph is the initial state
        incomingNode.add(in);
        oldFormula.remove(0);
        oldFormula.add(node.oldFormula.get(0));
      }

    }
  }

  public void removeIncomingEdge(Edge edge) {
    incoming.remove(edge);
  }

  public void removeOutgoingEdge(Edge edge) {
    outgoing.remove(edge);
  }

  public void setId(int id) {
    this.id = id;
  }

  private Node split(Formula<String> f) {
    Node node = new Node(incomingNode, newFormula, oldFormula, next);
    node.addToNew(f.getSub2());
    if (f.getContent() == Content.RELEASE) {
      node.addToNew(f.getSub1()); // also add sub1 to new2
    }
    addToNew(f.getSub1());

    // push obligations to immediate successors
    switch (f.getContent()) {
    case UNTIL:
    case RELEASE:
    case WEAK_UNTIL: // f1 W f2 <=> f2 \/ (f1 /\ X(f1 W f2))
      next.add(f);
    }

    return node;
  }

  void update() {
    isAccepting = true;
    for (Formula<String> f : next) {
      if (f.getContent() == Content.UNTIL)
        isAccepting = false;
    }

    if (incomingNode.size() > 0 && incomingNode.size() != oldFormula.size())
      System.err.print(incomingNode.size() + "incoming node and old size is diff" + oldFormula.size());

    for (int i = 0; i < incomingNode.size(); i++) {
      Node in = incomingNode.get(i);
      Edge edge = new Edge(oldFormula.get(i), in, this);
      incoming.add(edge);
      in.outgoing.add(edge);
    }
  }

}
