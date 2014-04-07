/**
 * 
 */
package gov.nasa.jpf.ltl.ddfs;

import java.util.BitSet;
import java.util.List;

import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.ltl.graph.Edge;
import gov.nasa.ltl.graph.Guard;
import gov.nasa.ltl.graph.Node;

/**
 * @author estar
 *
 */
public abstract class BuchiCG<AtomT> extends ChoiceGenerator<Node<AtomT>> {
  protected Node<AtomT> node;
  protected BitSet nextIndices;
  protected int index, choices, bitIndex, ssid, m;
  protected static int n = 0;

  public BuchiCG (Node<AtomT> n) {
    super ();
    node = n;
    List<Edge<AtomT>> edges = n.getOutgoingEdges ();
    nextIndices = new BitSet (edges.size ());
    choices = 0;
    for (int i = 0; i < edges.size (); i++)
      if (testGuard (edges.get (i).getGuard ())) {
        nextIndices.set (i);
        choices++;
      }
    index = -1;
    bitIndex = -1;
    m = BuchiCG.n++;
    setId ("" + m);
    ssid = JVM.getVM ().getStateId ();
  }
  
  abstract protected boolean testGuard (Guard<AtomT> g);
  
  @Override
  public void advance () {
    if (index < choices - 1 && !isDone) {
      index++;
      bitIndex = nextIndices.nextSetBit (bitIndex + 1);
      assert bitIndex > -1;
    }
  }

  @SuppressWarnings ("unchecked")
  @Override
  public Class<Node<AtomT>> getChoiceType () {
    return (Class<Node<AtomT>>) node.getClass ();
  }

  @Override
  public Node<AtomT> getNextChoice () {
    assert index <= choices - 1;
    return node.getOutgoingEdges ().get (bitIndex).getNext ();
  }

  @Override
  public int getProcessedNumberOfChoices () {
    return index + 1;
  }

  @Override
  public int getTotalNumberOfChoices () {
    return choices;
  }

  @Override
  public boolean hasMoreChoices () {
    return index < choices - 1;
  }

  @Override
  public ChoiceGenerator<?> randomize () {
    // TODO?
    // Collections.shuffle () is hard to apply to a BitSet.
    return this;
  }
  
  @Override
  public String toString () {
    return super.toString () + " [ssid=" + ssid + "]";
  }

  @Override
  public void reset () {
    index = -1;
  }
}
