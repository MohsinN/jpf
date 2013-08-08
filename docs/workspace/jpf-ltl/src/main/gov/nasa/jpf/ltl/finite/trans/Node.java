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
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public class Node implements Comparable<Node> {
  private List<Edge> incoming;
  private List<Edge> outgoing;

  // the set of nodes that lead to this node
  private Vector<Node> incomingNode;

  // the set of ltl formula that must hold on the current state but have not
  // been processed yet
  private TreeSet<Formula<String>> newFormulae;

  // the set of ltl formulae that have already been processed. Each formula in
  // newFormulae
  // that gets processed is transferred to oldFormulae
  // private TreeSet<Formula<String>> oldFormulae;
  private Vector<TreeSet<Formula<String>>> oldFormulae;

  // the set of ltl formulae that must hold at all immediate successor of this
  // node
  private TreeSet<Formula<String>> next;

  private boolean isAccepting;

  private int id;

  Node() {
    incoming = new ArrayList<Edge>();
    outgoing = new ArrayList<Edge>();
    incomingNode = new Vector<Node>();
    newFormulae = new TreeSet<Formula<String>>();
    oldFormulae = new Vector<TreeSet<Formula<String>>>();
    oldFormulae.add(new TreeSet<Formula<String>>());
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
    newFormulae.add(f);
  }

  private Node(Vector<Node> in, TreeSet<Formula<String>> newForm,
      Vector<TreeSet<Formula<String>>> done, TreeSet<Formula<String>> nx) {
    incoming = new ArrayList<Edge>();
    outgoing = new ArrayList<Edge>();
    incomingNode = new Vector<Node>(in);
    newFormulae = new TreeSet<Formula<String>>(newForm);
    oldFormulae = new Vector<TreeSet<Formula<String>>>();
    for (TreeSet<Formula<String>> old : done) {
      oldFormulae.add(new TreeSet<Formula<String>>(old));
    }
    next = new TreeSet<Formula<String>>(nx);
  }

  public void addIncomingEdge(Edge edge) {
    incoming.add(edge);
  }

  void addIncomingNode(Node node) {
    incomingNode.add(node);
  }

  public void addOutgoingEdge(Edge edge) {
    outgoing.add(edge);
  }

  private void addToNew(Formula<String> f) {
    if (!oldFormulae.get(0).contains(f))
      newFormulae.add(f);
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

  /**
   * Expands the graph node. Formulae in {@code newFormulae} field are removed
   * one by one by processing them in the following way. Each formula is broken
   * down until we get to the literals that must hold to make it true. If there
   * are alternative ways to make a formula true, the node is split in two nodes
   * where each of them represents one way of making the formula true.
   * 
   * @param nodes
   *          set of graph nodes that needs to expand
   * @return the graph node after all nodes are expanded
   */
  Vector<Node> expand(Vector<Node> nodes) {
    if (isProcessed()) {
      // Once a node is processed, it is added to the automaton unless there
      // exists an equivalent node there.
      Node temp = null;
      for (Node curNode : nodes) {
        if (curNode.getNext().equals(next))
          temp = curNode; // two nodes are considered as equivalent nodes if
        // they have the same next field
      }
      if (temp != null) {
        temp.modify(this); // if there already exists an equivalent node in the
        // automaton,
        // we modify that one instead of adding a new node
        return nodes;
      } else {
        nodes.add(this);
        Node newNode = getImmediate();
        return newNode.expand(nodes); // expands the immediate successors of
        // this node
      }
    } else {
      Formula<String> f = newFormulae.first(); // process the formula in
      // newFormulae one by one
      newFormulae.remove(f);
      if (isContradicting(f))
        return nodes; // if we obtain contradicting formulae after processing
      // the formula, the node is discarded
      if (f.isLiteral()) {
        if (f.getContent() != Content.TRUE)
          oldFormulae.get(0).add(f); // if the formula is a literal, simply add
        // it to the oldFormulae field
        return expand(nodes); // then continue processing other formulae
      } else {
        switch (f.getContent()) {
        case NEXT:
          next.add(f.getSub1());
          return expand(nodes);
        case AND: // both sub formulae must hold to make f true, so both are
          // added to the newFormulae field
          addToNew(f.getSub1());
          addToNew(f.getSub2());
          return expand(nodes);
        case OR:
        case UNTIL:
        case WEAK_UNTIL:
        case RELEASE:
          Node temp = split(f); // there are alternative ways to make f true, so
          // the node is split two nodes
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

  /**
   * Returns the node representing the immediate successors of this node.
   */
  private Node getImmediate() {
    Node node = new Node();
    node.incomingNode.add(this);
    node.newFormulae.addAll(next);
    return node;
  }

  public List<Edge> getIncomingEdges() {
    return incoming;
  }

  TreeSet<Formula<String>> getNewFormula() {
    return newFormulae;
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

  /**
   * Checks if contradictions are obtained during processing a formula.
   * 
   * @param f
   *          the processing formula
   * @return true if after processing the formula, the {@code oldFormulae} field
   *         contains contradicting formulae (f and !f), {@code false}
   *         otherwise.
   */
  private boolean isContradicting(Formula<String> f) {
    if (!f.isLiteral())
      return false;
    if (f.getContent() == Content.FALSE)
      return true;
    if (f.getContent() == Content.NOT) {
      return oldFormulae.get(0).contains(f.negate());
    } else {
      for (Formula<String> old : oldFormulae.get(0)) {
        if (old.negate().equals(f))
          return true;
      }
      return false;
    }
  }

  /**
   * Indicates if this node represents a node of the automaton by checking if
   * all formulae in its {@code newFormulae} are processed.
   * 
   * @return {@code true} if this node is processed, {@code false} otherwise.
   */
  boolean isProcessed() {
    return newFormulae.isEmpty();
  }

  /**
   * Modifies an existing node with a new equivalent node. Because only the
   * {@code next} field is considered when we compare two nodes, the resulting
   * node of this modifying process must record the information of each node's
   * {@code incomingNode} field and its associated {@code oldFormulae} field.
   * 
   * @param node
   *          the new equivalent node which this node is modifying according to
   */
  private void modify(Node node) {
    // incomingNode.addAll(node.incomingNode);
    for (Node in : node.incomingNode) {
      boolean edgeExisted = false;
      for (int i = 0; i < incomingNode.size(); i++) {
        if (incomingNode.get(i).equals(in)
            && oldFormulae.get(i).equals(node.oldFormulae.get(0)))
          edgeExisted = true;
      }
      if (edgeExisted)
        continue;

      if (incomingNode.size() != 0) {
        incomingNode.add(in);
        oldFormulae.add(node.oldFormulae.get(0));
      } else {// the equal state already in the graph is the initial state
        incomingNode.add(in);
        oldFormulae.remove(0);
        oldFormulae.add(node.oldFormulae.get(0));
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

  /**
   * splits this node in two nodes when there're alternative ways to make a
   * formula true. Each of the resulting nodes represents one way of making the
   * formula true.
   * 
   * @param f
   *          the formula that needs to hold
   * @return the second node after splitting
   */
  private Node split(Formula<String> f) {
    Node node = new Node(incomingNode, newFormulae, oldFormulae, next);
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

  /**
   * Updates the accepting condition and creates incoming edges to this node. A
   * node is accepting iff there doesn't exist any Until formula in the {@code
   * next} field.
   */
  void update() {
    isAccepting = true;
    for (Formula<String> f : next) {
      if (f.getContent() == Content.UNTIL)
        isAccepting = false;
    }

    for (int i = 0; i < incomingNode.size(); i++) {
      Node in = incomingNode.get(i);
      Edge edge = new Edge(oldFormulae.get(i), in, this);
      incoming.add(edge);
      in.outgoing.add(edge);
    }
  }

}
