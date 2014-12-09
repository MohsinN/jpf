/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.ltl.ddfs;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ChoiceGeneratorBase;
import gov.nasa.ltl.graph.Edge;

import java.io.PrintWriter;
import java.util.List;
import java.util.ListIterator;

/**
 * ChoiceGenerator that represents method calls
 */
public class EdgeCG extends ChoiceGeneratorBase<Edge> {

  protected List<Edge> edges;
  protected Edge cur;
  protected ListIterator<Edge> it;
  
  public EdgeCG (String id, List<Edge> edges){
    super(id);
    
    this.edges = edges;
    
    it = edges.listIterator();
  }
  
  @Override
  public void advance () {
    cur = it.next();
  }

  @Override
  public Class<Edge> getChoiceType () {
    return Edge.class;
  }

  @Override
  public Edge getNextChoice () {
    return cur;
  }

  @Override
  public int getProcessedNumberOfChoices () {
    return it.nextIndex();
  }

  @Override
  public int getTotalNumberOfChoices () {
    return edges.size();
  }

  @Override
  public boolean hasMoreChoices () {
    return it.hasNext();
  }

  @Override
  public ChoiceGenerator<Edge> randomize () {
    // <2do>
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(getClass().getName());
    sb.append(" [");
    int n = edges.size();
    for (int i=0; i<n; i++) {
      if (i > 0) {
        sb.append(',');
      }
      Edge inv = edges.get(i);
      if (inv == cur) {
        sb.append(MARKER);
      }
      sb.append(inv);
    }
    sb.append(']');
    return sb.toString();
  }
  
  public void printOn (PrintWriter pw) {
    pw.print(toString());
  }
  
  @Override
  public void reset () {
    cur = null;
    it = edges.listIterator();

    isDone = false;
  }

}

