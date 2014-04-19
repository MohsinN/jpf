package gov.nasa.jpf.ltl.infinite;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graph.Node;

public class DDFSearch extends Search {
	protected HashMap<Integer, BitSet> dfs1Table = new HashMap<Integer, BitSet>();
	protected HashMap<Integer, BitSet> dfs2Table = new HashMap<Integer, BitSet>();

	protected LinkedList<Pair> dfs1Stack = new LinkedList<Pair>();
	protected LinkedList<Pair> dfs2Stack = new LinkedList<Pair>();

	protected Graph<String> spec = null;
	protected Graph<String> setSpec = null;

	protected String specText = null;
	protected String specSource = null;

	protected LinkedList<BuchiCG<String>> buchiCGs = new LinkedList<BuchiCG<String>>();

	protected SymbolicLTLListener symbolicLTLListener;

	protected Property prop = null;

	protected boolean haveProgress = true;

	protected class Pair {
		private int state;
		private int node;

		Pair(int state, Node<String> node) {
			this.state = state;
			this.node = node != null ? node.getId() : Integer.MIN_VALUE;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof Pair)) {
				return false;
			}

			Pair p = (Pair) obj;
			return state == p.state && node == p.node;
		}

		@Override
		public String toString() {
			return "(" + state + ", " + node + ")";
		}
 	}

	public DDFSearch(Config config, JVM vm) {
		super(config, vm);

		symbolicLTLListener = new SymbolicLTLListener();
		addListener(symbolicLTLListener);

		if (config.getBoolean("search.multiple_errors")) {
			config.setProperty("search.multiple_errors", "false");
		}
	}

	@Override
	public void search() {
		notifySearchStarted();

		recordVisit(dfs1Stack);
		recordVisit(dfs1Table);

		while (true) {
			if (setSpec != null) {
				spec = setSpec;
				setSpec = null;

				BuchiCG<String> initCg = new AtomBuchiCG(spec.getInit());
				initCg.advance();
				buchiCGs.push(initCg);

				prop = new Property(specText, specSource);
				addProperty(prop);
			}

			if (forward()) {
				if (seenBefore(dfs1Table)) {
					backtrack();
				}
				else {
					recordVisit(dfs1Stack);
					recordVisit(dfs1Table);

					if (isPropertyViolated()) {
						break;
					}
				}
			}
			else {
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
				boolean canBackTrack = backtrack();
				assert canBackTrack : "Can not dfs1.backtrack";
			}
		}

		notifySearchFinished();
	}

	protected boolean dfs2() {
		final int startDepth = depth;

		if (vm.getChoiceGenerator() != null) {
			vm.getChoiceGenerator().reset();
		}

		while (true) {
			if (seenBefore(dfs2Table)) {
				assert depth > startDepth;
				backtrack();
			}
			else {
				recordVisit(dfs2Table);
			}

			if (seenBefore(dfs1Stack) && depth > startDepth) {
				return true;
			}

			while (!forward()) {
				if (depth == startDepth) {
					return false;
				}
				backtrack();
			}
		}
	}

	@Override
	protected boolean forward() {
		if (vm.isEndState()) {
			return false;
		}

		boolean canForward = true;
		int runsWithoutProgress = 0;
		int id;
		do {
			if (!vm.getCurrentThread().isRunnable()) {
				canForward = false;
				break;
			}

			assert runsWithoutProgress < 2 : "Run without progress more than 2 times";

			haveProgress = true;
			symbolicLTLListener.setFirstStep();
			id = vm.getStateId();
			canForward = super.forward();

			if (!haveProgress) {
				assert (haveProgress || vm.getSystemState().getNextChoiceGenerator() != null) : "Have no progress " + haveProgress + ", cg=" + vm.getSystemState().getNextChoiceGenerator();
			}

			runsWithoutProgress++;
	    }
		while (canForward && !haveProgress && vm.getStateId() == id);

		if (canForward) {
			if (!createNextCg()) {
				super.backtrack();
				return false;
			}

			System.err.println ("forward: PC = " + vm.getCurrentThread ().getPC());

			depth++;
			notifyStateAdvanced();

			return true;
		}
		else {
			if (buchiCGs.isEmpty()) {
				return false;
			}

			BuchiCG<String> bcg = buchiCGs.peek();
			if (!bcg.hasMoreChoices()) {
				return false;
			}
			bcg.advance();

			ChoiceGenerator<?> cg = vm.getChoiceGenerator();
			cg.reset();

			return forward();
		}
	}

	@Override
	protected boolean backtrack() {
		if (super.backtrack() || // TODO: SNAFU in jpf-core
				(vm.getStateId() == -1 && vm.getStateSet() != null)) {
			if (spec != null) {
				buchiCGs.pop();
			}

			depth--;
			notifyStateBacktracked();

			return true;
		}
		else {
			return false;
		}
	}

	protected boolean seenBefore(HashMap<Integer, BitSet> table) {
		int stateId = vm.getStateId();
		if (!table.containsKey(stateId)) {
			return false;
		}

		Node<String> n = currentNode();
		if (n != null) {
			return table.get(stateId).get(currentNode().getId() + 1);
		}
		else {
			return table.get(stateId).get(0);
		}
	}

	protected boolean seenBefore(LinkedList<Pair> stack) {
		return stack.contains(new Pair(vm.getStateId(), currentNode()));
	}

	protected void recordVisit(HashMap<Integer, BitSet> table) {
		BitSet s;
		int stateId = vm.getStateId();
		if (!table.containsKey(stateId)) {
			if (spec != null)
				s = new BitSet(spec.getNodeCount() + 1);
			else {
				s = new BitSet(1);
			}
			table.put(stateId, s);
		}
		else {
			s = table.get(stateId);
		}

		Node<String> n = currentNode();
		if (n != null) {
			if (s.size() == 1) {
				// expand to spec nodes + 1 size
				s.set(spec.getNodeCount(), false);
			}
			s.set(n.getId() + 1);
		}
		else {
			s.set(0);
		}
	}

	protected void recordVisit(LinkedList<Pair> stack) {
		stack.push(new Pair(vm.getStateId(), currentNode()));
	}

	protected Node<String> currentNode() {
		return buchiCGs.size() > 0 ? buchiCGs.peek().node : null;
	}

	protected BuchiCG<String> currentBuchiCG() {
		return buchiCGs.size() > 0 ? buchiCGs.peek() : null;
	}

	protected boolean inAcceptingState() {
		Node<String> n = currentNode();
		if (n == null) {
			return false;
		}
		return n.getBooleanAttribute("accepting");
	}

	protected boolean createNextCg() {
		BuchiCG<String> cg = currentBuchiCG();
		if (cg == null) {
			assert spec == null;
			return true;
		}

		assert cg != null;

		Node<String> nextNode = cg.getNextChoice();
		BuchiCG<String> cgNew = new AtomBuchiCG(nextNode);
		if (cgNew.hasMoreChoices()) {
			cgNew.advance();
		}
		else {
			return false;
		}
		buchiCGs.push(cgNew);
		return true;
	}

	void noProgress() {
		haveProgress = false;
	}

	void setSpec(Graph<String> g, String ltl, String location) {
		assert spec == null : "Attempted to set Buchi spec but it was already set";

		setSpec = g;
		specText = ltl;
		specSource = location;
	}

	Graph<String> getSpec() {
		return spec;
	}

	protected String firstFewCGs() {
		ChoiceGenerator<?> cg = vm.getSystemState().getNextChoiceGenerator();
		String r = "NEXT: ";
		if (cg != null) {
			r += vm.getSystemState().getNextChoiceGenerator().toString().trim() + "[" + cg.getProcessedNumberOfChoices() + "/" + cg.getTotalNumberOfChoices() + "]";
		}
		else {
			r += "null";
			r += ", CUR: ";
			cg = vm.getChoiceGenerator();
			for (int i = 0; i < 3 && cg != null; i++, cg = cg.getPreviousChoiceGenerator()) {
				r += cg.toString().trim() + "[" + cg.getProcessedNumberOfChoices() + "/" + cg.getTotalNumberOfChoices() + "], ";
			}
		}
		return r;
	}
}
