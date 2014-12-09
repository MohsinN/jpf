/**
 * 
 */
package gov.nasa.jpf.ltl.finite.trans;

import gov.nasa.ltl.trans.Formula;
import gov.nasa.ltl.trans.Formula.Content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;

/**
 * represents the automaton which can be checked by jpf-automata
 * @author Phuc Nguyen Dinh - luckymaphuc@gmail.com
 * 
 */
public class Graph {
  private Node init;
  private int count;
  private Hashtable<Integer, Node> nodes = new Hashtable<Integer, Node>();

  /**
   * creates a new empty automata
   */
  public Graph() {
    count = 0;
  }

  /**
   * creates a new automata with an initial state
   * @param initState the first state in the automata, which has id = 0
   */
  public Graph(Node initState) {
    this.init = initState;
    initState.setId(0);
    nodes.put(0, initState);
    count = 1;
  }

  /**
   * Adds a state to this automata.
   * @param node the state need to add
   */
  void addNode(Node node) {
    if (nodes.contains(node))
      return;
    int id = count++;
    node.setId(id);
    if(id == 0) 
      init = node;
    nodes.put(id, node);
  }
  
  /**
   * Return the equivalence state from this automata. Two state are considered as equivalence if they have the same <code>next</code> field.
   * @param node
   * @return the equivalence state and <code>null</code> else
   */
  Node getEqualNode(Node node) {
    Collection<Node> nodeList = nodes.values();
    for(Node curNode: nodeList) {
      if(curNode.getNext().equals(node.getNext()))
        return curNode;
    }
    return null;
    /*
    for(Node existed: nodeList) {
      boolean isEqual = true;
      List<Formula<String>> existedNext = existed.getNext();
      List<Formula<String>> nodeNext = node.getNext();
      for(Formula<String> f1: existedNext) {
        boolean isContain = false;
        for(Formula<String> f2: nodeNext) {
          if(compareFormula(f1, f2)) {
            isContain = true;
            break;
          }
        }
        if(!isContain) {
          isEqual = false;
          break;
        }
      }
      if(isEqual)
        return existed;
    }
    return null;
    */
  }
  
  private boolean compareFormula(Formula<String> f1, Formula<String> f2) {
    if(f1.getContent() != f2.getContent()) 
      return false;
    if(f1.isLiteral() && f2.isLiteral()) {
      switch(f1.getContent()) {
      case TRUE: 
      case FALSE:
        return true;
      case PROPOSITION:
        return f1.getName().equals(f2.getName());
      case NOT:
        return f1.getSub1().getName().equals(f2.getSub1().getName());
      }
    }
    else {
      switch(f1.getContent()) {
      case AND: 
      case OR:
      case UNTIL:
      case RELEASE:
      case WEAK_UNTIL:
        return compareFormula(f1.getSub1(), f2.getSub1()) && compareFormula(f1.getSub2(), f2.getSub2());
      case NEXT:
        return compareFormula(f1.getSub1(), f2.getSub1());
      }
    }
    return false;
  }

  /**
   * Adds a state with a given <code>id</code>
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
   * Indicates whether this automata contains a state with a specified <code>id</code> or not
   * @param id
   * @return
   */
  public boolean contains(int id) {
    return nodes.get(id) != null;
  }

  public Node getInitState() {
    return init;
  }

  public Node getNode(int id) {
    return nodes.get(id);
  }

  public List<Node> getStates() {
    List<Node> states = new ArrayList<Node>();
    Collection<Node> nodeList = nodes.values();
    for (Node node : nodeList) {
      states.add(node);
    }
    return states;
  }

  public void removeNode(int id) {
    nodes.remove(id);
    count--;
  }

  /**
   * Displays this automata in the SPIN format.
   */
  public void print() {
    if(init == null) {
      System.out.println("Empty");
      return ;
    }
    
    System.out.println("never {");
    print(init);
    System.out.println ();
    for (Node state: getStates()) {
      if (state.equals(init))
        continue;
      print((Node)state);
      System.out.println();
    }
    System.out.println ("}");
  }
  
  private void print(Node node) {
    if(node.isAccepting())
      System.out.print("accept_");
    System.out.println ("S" + node.getId () + ":");
    System.out.println ("     if");
    for (Edge e: node.getOutgoingEdges()) {
      System.out.print ("     :: ");
      print((Edge) e);
    }
    System.out.println ("     fi;");
  }
  
  private void print(Edge e) {    
    System.out.print ('(');
    print((Guard) e.getGuard());
    System.out.print (") ");
    System.out.print ("-> goto ");
    Node next = (Node) e.getNext();
    if (next.isAccepting())
      System.out.print ("accept_");
    System.out.println("S" + next.getId());
  }

  private void print(Guard g) {
    boolean first = true;
    if (g.isTrue ()) {
      System.out.print ('1');
      return;
    }
    for (Literal l: g.getLiterals()) {
      if (first)
        first = false;
      else
        System.out.print (" && ");
      print((Literal) l);
    }
  }
  
  private void print(Literal l) {
    if (l.isNegated ())
      System.out.print ('!');
    System.out.print(l.getAtom ());
  }
}
