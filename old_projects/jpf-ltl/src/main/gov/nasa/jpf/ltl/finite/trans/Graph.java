/**
 * 
 */
package gov.nasa.jpf.ltl.finite.trans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

/**
 * Represents a finite state automaton. An automaton is described by an initial
 * state. The initial state will contains pointer to the next states and so on.
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public class Graph {
  private Node init;
  private int count;
  private Hashtable<Integer, Node> nodes = new Hashtable<Integer, Node>();

  /**
   * creates a new empty automaton
   */
  public Graph() {
    count = 0;
  }

  /**
   * creates a new automaton with an initial state
   * 
   * @param initState
   *          the first state in the automata, which has id = 0
   */
  public Graph(Node initState) {
    this.init = initState;
    initState.setId(0);
    nodes.put(0, initState);
    count = 1;
  }

  /**
   * Adds a state to this automaton.
   * 
   * @param node
   *          the state need to add
   */
  void addNode(Node node) {
    if (nodes.contains(node))
      return;
    int id = count++;
    node.setId(id);
    if (id == 0)
      init = node;
    nodes.put(id, node);
  }

  /**
   * Adds a state with a given <code>id</code>
   * 
   * @param node
   * @param id
   */
  public void addNode(Node node, int id) {
    if (nodes.containsKey(id))
      return;
    nodes.put(id, node);
    node.setId(id);
    count++;
    if (id == 0)
      init = node;
  }

  /**
   * Indicates whether this automaton contains a state with a specified
   * <code>id</code> or not
   * 
   * @param id
   * @return {@code true} if this automaton contains a state with the given
   *         <code>id</code>, {@code false} otherwise
   */
  public boolean contains(int id) {
    return nodes.get(id) != null;
  }

  /**
   * Returns the equivalence state from this automaton. Two state are considered
   * as equivalence if they have the same <code>next</code> field.
   * 
   * @param node
   * @return the equivalence state and <code>null</code> else
   */
  Node getEqualNode(Node node) {
    Collection<Node> nodeList = nodes.values();
    for (Node curNode : nodeList) {
      if (curNode.getNext().equals(node.getNext()))
        return curNode;
    }
    return null;
  }

  /**
   * Retrieves the initial state of this automaton.
   * 
   * @return the initial state
   */
  public Node getInitState() {
    return init;
  }

  /**
   * Retrieves a state with a given <code>id</code>.
   * 
   * @param id
   *          the identity number of the needed state
   * @return the state with the given <code>id</code> in this automaton,
   *         <code>null</code> if there doesn't exist such a state in this
   *         automaton
   */
  public Node getNode(int id) {
    return nodes.get(id);
  }

  /**
   * Gets all states in this automaton
   * 
   * @return the list of all states, with initial state is the first item in the
   *         list.
   */
  public List<Node> getStates() {
    List<Node> states = new ArrayList<Node>();
    Collection<Node> nodeList = nodes.values();
    for (Node node : nodeList) {
      states.add(node);
    }
    return states;
  }

  /**
   * Displays this automaton in the SPIN format.
   */
  public void print() {
    if (init == null) {
      System.out.println("Empty");
      return;
    }

    System.out.println("never {");
    print(init);
    System.out.println();
    for (Node state : getStates()) {
      if (state.equals(init))
        continue;
      print(state);
      System.out.println();
    }
    System.out.println("}");
  }

  private void print(Edge e) {
    System.out.print('(');
    print(e.getGuard());
    System.out.print(") ");
    System.out.print("-> goto ");
    Node next = e.getNext();
    if (next.isAccepting())
      System.out.print("accept_");
    System.out.println("S" + next.getId());
  }

  private void print(Guard g) {
    boolean first = true;
    if (g.isTrue()) {
      System.out.print('1');
      return;
    }
    for (Literal l : g.getLiterals()) {
      if (first)
        first = false;
      else
        System.out.print(" && ");
      print(l);
    }
  }

  private void print(Literal l) {
    if (l.isNegated())
      System.out.print('!');
    System.out.print(l.getAtom());
  }

  private void print(Node node) {
    if (node.isAccepting())
      System.out.print("accept_");
    System.out.println("S" + node.getId() + ":");
    System.out.println("     if");
    for (Edge e : node.getOutgoingEdges()) {
      System.out.print("     :: ");
      print(e);
    }
    System.out.println("     fi;");
  }
}
