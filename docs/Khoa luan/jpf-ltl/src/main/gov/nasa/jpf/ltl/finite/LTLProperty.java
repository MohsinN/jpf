/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import gov.nasa.jpf.ltl.finite.trans.Edge;
import gov.nasa.jpf.ltl.finite.trans.Graph;
import gov.nasa.jpf.ltl.finite.trans.Guard;
import gov.nasa.jpf.ltl.finite.trans.Node;
import gov.nasa.jpf.ltl.finite.trans.Translator;
import gov.nasa.ltl.trans.Formula;
import gov.nasa.ltl.trans.LTL2Buchi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Paint;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.collections15.Transformer;

/**
 * This class represents a linear temporal property. It can be used to translate
 * the LTL formula into Buchi automata and visualize it.
 * 
 * @author Phuc Nguyen Dinh
 */
public class LTLProperty {

  /**
   * This panel used to display the atoms in detail. In the buchi automata, only
   * the short form of atoms such as p0, p1,... is displayed. Therefore, this
   * Panel is used to explain these atoms in detail.
   */
  public static class PropPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static Vector<String> props;
    private static int numberOfAtoms, maxAtomLength = 1;

    public PropPanel() {
      super();
      props = new Vector<String>();
      Enumeration<String> keys = atoms.keys();
      while (keys.hasMoreElements()) {
        String prop = keys.nextElement();
        String label = atoms.get(prop) + ": " + prop;
        props.add(label);
        numberOfAtoms++;
        if (label.length() > maxAtomLength)
          maxAtomLength = label.length();
      }
      this
          .setPreferredSize(new Dimension(maxAtomLength * 6, numberOfAtoms * 20));

    };

    @Override
    public void paint(Graphics g) {
      int lineDis = 15, count = 1;
      for (String prop : props) {
        g.drawString(prop, 10, lineDis * count);
        count++;
      }
    }
  }

  String ltlFormula;
  gov.nasa.ltl.graph.Graph<String> negatedBuchi, buchi;

  Vector<String> fields;
  Graph finiteBuchi;

  static Hashtable<String, String> atoms;

  public LTLProperty(String ltlSpec, boolean isInfinite) {
    ltlFormula = ltlSpec;
    fields = new Vector<String>();
    atoms = new Hashtable<String, String>();
    initProperty(isInfinite);
  }

  /**
   * Converts the Buchi translated by LTL2Buchi to automata which can be used by
   * jpf-automata
   * 
   * @param buchi
   *          the Buchi translated by LTL2Buchi
   * @return the automaton can be checked by jpf-automata
   */
  public Graph convertBuchi(gov.nasa.ltl.graph.Graph<String> buchi) {
    Graph graph = new Graph();
    for (gov.nasa.ltl.graph.Node<String> node : buchi.getNodes()) {
      Node state = new Node(node.getBooleanAttribute("accepting"), node.getId());
      if (!graph.contains(node.getId()))
        graph.addNode(state, node.getId());
      for (gov.nasa.ltl.graph.Edge<String> edge : node.getOutgoingEdges()) {
        Edge transition = new Edge();

        gov.nasa.ltl.graph.Guard<String> buchiGuard = edge.getGuard();
        Guard guard = new Guard(buchiGuard);
        transition.addGuard(guard);

        state.addOutgoingEdge(transition);
        transition.setSource(state);

        gov.nasa.ltl.graph.Node<String> nextNode = edge.getNext();
        Node nextState = graph.getNode(nextNode.getId());
        if (nextState == null) {
          nextState = new Node(nextNode.getBooleanAttribute("accepting"),
              nextNode.getId());
          graph.addNode(nextState, nextNode.getId());
        }
        nextState.addIncomingEdge(transition);
        transition.setNext(nextState);
      }
    }
    return graph;
  }

  public Graph getActualAutomata() {
    return convertBuchi(buchi);
  }

  public gov.nasa.ltl.graph.Graph<String> getActualBuchi() {
    return buchi;
  }

  /**
   * @return A list of fields that appeared in the LTL formulae.
   */
  public Vector<String> getFields() {
    return fields;
  }

  /**
   * @return Buchi translated by gov.nasa.jpf.ltl.automata.Translator
   */
  public Graph getFiniteBuchi() {
    return finiteBuchi;
  }

  /**
   * @return the negated automata of this which can be checked by jpf-automata
   */
  public Graph getNegatedAutomata() {
    return convertBuchi(negatedBuchi);
  }

  /**
   * @return The negated Buchi automata correspond to this LTL formulae.
   */
  public gov.nasa.ltl.graph.Graph<String> getNegatedBuchi() {
    return negatedBuchi;
  }

  public String getProperty() {
    return ltlFormula;
  }

  private void initProperty(boolean isInfinite) {
    LTLSpecLexer lexer = null;
    if (ltlFormula.endsWith(".ltl")) {
      try {
        ANTLRFileStream input = new ANTLRFileStream(ltlFormula);
        lexer = new LTLSpecLexer(input);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    } else {
      ANTLRStringStream input = new ANTLRStringStream(ltlFormula);
      lexer = new LTLSpecLexer(input);
    }

    CommonTokenStream tokens = new CommonTokenStream(lexer);
    LTLSpecParser parser = new LTLSpecParser(tokens);

    try {
      Formula<String> formula = parser.ltlSpec();
      Formula<String> negatedFormula = Formula.Not(formula);
      buchi = LTL2Buchi.translate(formula);
      negatedBuchi = LTL2Buchi.translate(negatedFormula);
      finiteBuchi = Translator.translate(formula);
    } catch (RecognitionException e) {
      e.printStackTrace();
    }

    // Try to extract all class instance fields and local variable from this LTL
    // formulae
    fields = parser.getAttributes();
    Vector<String> props = parser.getAtoms();
    int count = 0;
    for (String atom : props) {
      if (!atoms.containsKey(atom))
        atoms.put(atom, "p" + String.valueOf(count++));
    }
  }

  /**
   * Visualizes the negated Buchi automata. The accepting states are the red
   * vertexes.
   */
  public void showGraph(gov.nasa.ltl.graph.Graph<String> buchi) {
    DirectedSparseMultigraph<gov.nasa.ltl.graph.Node<String>, gov.nasa.ltl.graph.Edge<String>> g = new DirectedSparseMultigraph<gov.nasa.ltl.graph.Node<String>, gov.nasa.ltl.graph.Edge<String>>();
    List<gov.nasa.ltl.graph.Node<String>> nodes = buchi.getNodes();
    for (gov.nasa.ltl.graph.Node<String> node : nodes) {
      List<gov.nasa.ltl.graph.Edge<String>> edges = node.getOutgoingEdges();
      for (gov.nasa.ltl.graph.Edge<String> edge : edges) {
        g.addEdge(edge, node, edge.getNext());
      }
    }

    Layout<gov.nasa.ltl.graph.Node<String>, gov.nasa.ltl.graph.Edge<String>> layout = new CircleLayout<gov.nasa.ltl.graph.Node<String>, gov.nasa.ltl.graph.Edge<String>>(
        g);
    layout.setSize(new Dimension(300, 300));
    BasicVisualizationServer<gov.nasa.ltl.graph.Node<String>, gov.nasa.ltl.graph.Edge<String>> server = new BasicVisualizationServer<gov.nasa.ltl.graph.Node<String>, gov.nasa.ltl.graph.Edge<String>>(
        layout);
    server.setPreferredSize(new Dimension(350, 350));
    Transformer<gov.nasa.ltl.graph.Node<String>, Paint> vertexPaint = new Transformer<gov.nasa.ltl.graph.Node<String>, Paint>() {

      @Override
      public Paint transform(gov.nasa.ltl.graph.Node<String> e) {
        if (e.getBooleanAttribute("accepting"))
          return Color.RED;
        else
          return Color.WHITE;
      }
    };

    Transformer<gov.nasa.ltl.graph.Edge<String>, String> edgeString = new Transformer<gov.nasa.ltl.graph.Edge<String>, String>() {

      @Override
      public String transform(gov.nasa.ltl.graph.Edge<String> e) {
        gov.nasa.ltl.graph.Guard<String> guard = e.getGuard();
        if (guard.isTrue())
          return "true";
        String result = "";
        for (gov.nasa.ltl.graph.Literal<String> literal : guard) {
          if (literal.isNegated())
            result += "!";
          result += atoms.get(literal.getAtom());
          result += " && ";
        }
        if (result.endsWith(" && "))
          return result.substring(0, result.length() - 4);
        return result;
      }
    };

    Transformer<gov.nasa.ltl.graph.Node<String>, String> vertexString = new Transformer<gov.nasa.ltl.graph.Node<String>, String>() {
      public String transform(gov.nasa.ltl.graph.Node<String> e) {
        return "S" + String.valueOf(e.getId());
      }
    };
    server.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
    server.getRenderContext().setVertexLabelTransformer(vertexString);
    server.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
    server.getRenderContext().setEdgeLabelTransformer(edgeString);
    JFrame frame = new JFrame("The Negated Buchi Automata");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel props = new PropPanel();
    frame.add(server, BorderLayout.NORTH);
    frame.add(props, BorderLayout.SOUTH);
    frame.pack();
    frame.setVisible(true);
  }
}