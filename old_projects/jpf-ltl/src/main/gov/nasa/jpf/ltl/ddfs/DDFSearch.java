package gov.nasa.jpf.ltl.ddfs;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graph.Node;

/**
 * @author Ewgenij Starostin
 * 
 */
public class DDFSearch extends Search {
  // TODO: HashMap on [0, n] ints??
  protected HashMap<Integer, BitSet> seenByDfs1 = new HashMap<Integer, BitSet>(),
      seenByDfs2 = new HashMap<Integer, BitSet>();
  protected LinkedList<Pair> dfs1Stack = new LinkedList<Pair>();
  // TODO: Temporarily...
  protected Graph<String> spec = null, setSpec = null;
  protected String specText = null, specSource = null;
  protected LinkedList<BuchiCG<String>> buchiStates = new LinkedList<BuchiCG<String>>();
  protected Listener l;
  protected Property prop = null;

  public DDFSearch(Config config, JVM vm) {
    super(config, vm);
    l = new Listener();
    vm.addListener(l);
    addListener(l);
    if (config.getBoolean("search.multiple_errors")) {
      // TODO: Warn.
      config.setProperty("search.multiple_errors", "false");
    }
  }

  @Override
  public void search() { // try {
    notifySearchStarted();
    recordVisit(dfs1Stack);
    recordVisit(seenByDfs1);
    while (true) {
      if (setSpec != null) {
        assert spec == null;
        spec = setSpec;
        setSpec = null;
        BuchiCG<String> initCg = new AtomBuchiCG(spec.getInit());
        initCg.advance();
        buchiStates.push(initCg);
        prop = new Property(specText, specSource);
        addProperty(prop);
      }
      if (forward()) {
        if (seenBefore(seenByDfs1)) {
          // System.err.println ("DFS1: Seen before.");
          backtrack();
        } else {
          // System.err.println ("DFS1: New state.");
          recordVisit(dfs1Stack);
          recordVisit(seenByDfs1);
          if (isPropertyViolated())
            break;
        }
      } else {
        // System.err.println ("DFS1: forward() failed.");
        if (spec != null && inAcceptingState() && dfs2()) {
          prop.setViolated();
          isPropertyViolated();
          break;
        }
        if (depth == 0) {
          terminate();
          break;
        }
        dfs1Stack.pop();
        boolean r = backtrack();
        assert r;
      }
    }
    notifySearchFinished();
    // int space = 0;
    // for (int key: seenByDfs1.keySet ())
    // space += seenByDfs1.get (key).cardinality ();
    // System.err.println ("State space: " + space);
    // } catch (Throwable t) {t.printStackTrace (); throw new RuntimeException
    // ();}
  }

  protected class Pair {
    private int state;
    private int node;

    Pair(int state, Node<String> node) {
      this.state = state;
      this.node = node != null ? node.getId() : Integer.MIN_VALUE;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof Pair))
        return false;
      Pair p = (Pair) obj;
      return state == p.state && node == p.node;
    }

    @Override
    public String toString() {
      return "(" + state + ", " + node + ")";
    }
  }

  protected boolean seenBefore(HashMap<Integer, BitSet> set) {
    Node<String> n = currentNode();
    if (!set.containsKey(vm.getStateId()))
      return false;
    if (n != null)
      return set.get(vm.getStateId()).get(currentNode().getId() + 1);
    else
      return set.get(vm.getStateId()).get(0);
  }

  protected boolean seenBefore(LinkedList<Pair> stack) {
    return stack.contains(new Pair(vm.getStateId(), currentNode()));
  }

  protected void recordVisit(HashMap<Integer, BitSet> set) {
    BitSet s;
    Node<String> n = currentNode();
    if (!set.containsKey(vm.getStateId())) {
      if (spec != null)
        s = new BitSet(spec.getNodeCount() + 1);
      else {
        assert n == null;
        s = new BitSet(1);
      }
      set.put(vm.getStateId(), s);
    } else
      s = set.get(vm.getStateId());
    if (n != null) {
      assert spec != null;
      if (s.size() == 1)
        // expand to spec nodes + 1 size
        s.set(spec.getNodeCount(), false);
      s.set(n.getId() + 1);
    } else
      s.set(0);
  }

  protected void recordVisit(LinkedList<Pair> stack) {
    stack.push(new Pair(vm.getStateId(), currentNode()));
  }

  protected Node<String> currentNode() {
    return buchiStates.size() > 0 ? buchiStates.peek().node : null;
  }

  protected BuchiCG<String> currentBuchiCG() {
    return buchiStates.size() > 0 ? buchiStates.peek() : null;
  }

  protected boolean inAcceptingState() {
    Node<String> n = currentNode();
    if (n == null)
      return false;
    return n.getBooleanAttribute("accepting");
  }

  protected boolean dfs2() {
    final int startDepth = depth;
    if (vm.getChoiceGenerator() != null)
      vm.getChoiceGenerator().reset();
    // System.err.println ("DFS2: " + vm.getStateId ());
    // System.err.print ("DFS1 stack is: ");
    // for (int i = 0; i < 10; i++)
    // if (i < dfs1Stack.size ())
    // System.err.print (dfs1Stack.get (i) + ", ");
    // System.err.println ("...");
    while (true) {
      assert seenBefore(seenByDfs1);
      if (seenBefore(seenByDfs2)) {
        // System.err.println ("DFS2: " + vm.getStateId () +
        // " seen before by DFS2");
        assert depth > startDepth;
        backtrack();
      } else {
        // System.err.println ("DFS2: " + vm.getStateId () + " new for DFS2");
        recordVisit(seenByDfs2);
      }
      if (seenBefore(dfs1Stack) && depth > startDepth)
        return true;
      while (!forward()) {
        if (depth == startDepth)
          return false;
        // System.err.println ("DFS2: " + vm.getStateId () +
        // " explored, backtrack");
        backtrack();
      }
    }
  }

  protected boolean haveProgress = true;

  void noProgress() {
    haveProgress = false;
  }

  @Override
  protected boolean forward() {
    int runsWithoutProgress = 0;
    int id;
    if (vm.isEndState())
      return false;
    boolean r;
    do {
      if (!vm.getCurrentThread().isRunnable()) {
        // TODO: Should this happen?
        r = false;
        break;
      }
      assert runsWithoutProgress < 2;
      haveProgress = true;
      l.setFirstStep();
      id = vm.getStateId();
      r = super.forward();
      if (!haveProgress) {
        // System.err.println ("No progress, state = " + vm.getStateId () +
        // ", PC = " + vm.getCurrentThread ().getPC () +
        // ", CGs: " + firstFewCGs ());
        assert vm.getSystemState().getNextChoiceGenerator() != null;
      }
      runsWithoutProgress++;
    } while (r && !haveProgress && vm.getStateId() == id);
    if (r) {
      if (!createNextCg()) {
        super.backtrack();
        return false;
      }
      // (currentNode() != null ? currentNode ().getId () : -1) +
      // " #### " + firstFewCGs ());
      // System.err.println ("PC = " + vm.getCurrentThread ().getPC ());
      depth++;
      notifyStateAdvanced();
      return true;
    } else {
      if (buchiStates.isEmpty()) {
        assert spec == null;
        return false;
      }
      assert !buchiStates.isEmpty();
      BuchiCG<String> bcg = buchiStates.peek();
      if (!bcg.hasMoreChoices())
        return false;
      bcg.advance();
      ChoiceGenerator<?> cg = vm.getChoiceGenerator();
      cg.reset();
      return forward();
    }
  }

  // TODO: Remove eventually.
  protected String firstFewCGs() {
    ChoiceGenerator<?> cg;
    cg = vm.getSystemState().getNextChoiceGenerator();
    String r = "NEXT: ";
    if (cg != null)
      r += vm.getSystemState().getNextChoiceGenerator().toString().trim() + "["
          + cg.getProcessedNumberOfChoices() + "/"
          + cg.getTotalNumberOfChoices() + "]";
    else
      r += "null";
    r += ", CUR: ";
    cg = vm.getChoiceGenerator();
    for (int i = 0; i < 3 && cg != null; i++, cg = cg
        .getPreviousChoiceGenerator())
      r += cg.toString().trim() + "[" + cg.getProcessedNumberOfChoices() + "/"
          + cg.getTotalNumberOfChoices() + "], ";
    return r;
  }

  @Override
  protected boolean backtrack() {
    if (super.backtrack() || // TODO: SNAFU in jpf-core
        (vm.getStateId() == -1 && vm.getStateSet() != null)) {
  
      // (currentNode () != null ? currentNode ().getId () : -1) +
      // "; PC = " + vm.getCurrentThread ().getPC () + "; " + firstFewCGs ());
      if (spec != null) {
        assert buchiStates.size() > 0;
        buchiStates.pop();
      }
      depth--;
      notifyStateBacktracked();
      return true;
    } else
      return false;
  }

  protected boolean createNextCg() {
    BuchiCG<String> cg = currentBuchiCG(), cgNew;
    Node<String> nextNode;
    if (cg == null) {
      assert spec == null;
      return true;
    }
    assert cg != null;
    nextNode = cg.getNextChoice();
    cgNew = new AtomBuchiCG(nextNode);
    if (cgNew.hasMoreChoices())
      cgNew.advance();
    else
      return false;
    buchiStates.push(cgNew);
    return true;
  }

  void setSpec(Graph<String> g, String ltl, String location) {
    assert spec == null : "attempted to set Buchi spec but it was already set";
    setSpec = g;
    specText = ltl;
    specSource = location;
  }

  Graph<String> getSpec() {
    return spec;
  }
}
