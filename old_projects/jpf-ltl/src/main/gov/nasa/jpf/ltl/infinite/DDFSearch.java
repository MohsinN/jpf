package gov.nasa.jpf.ltl.infinite;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
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
		vm.addListener(symbolicLTLListener);
		System.out.println("DDFSearch: " + symbolicLTLListener);

		if (config.getBoolean("search.multiple_errors")) {
			config.setProperty("search.multiple_errors", "false");
		}
	}

	@Override
	public void search() {
		notifySearchStarted();

		log("search", "start dfs1");
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
				log("search", "forwarded");
				if (seenBefore(dfs1Table, 1)) {
					log("search", "in table 1 and backtrack");
					backtrack();
				}
				else {
					log("search", "not in table 1 and record");
					recordVisit(dfs1Stack);
					recordVisit(dfs1Table);

					if (isPropertyViolated()) {
						log("search", "not in table 1, record and isPropertyViolated");
						break;
					}
				}
			}
			else {
				log("search", "can not forward");
				if (spec != null && inAcceptingState() && dfs2()) {
					log("search", "can not forward and end dfs2 and violated");
					prop.setViolated();
					isPropertyViolated();
					break;
				}

				if (depth == 0) {
					log("search", "can not forward but depth=0 -> terminated");
					terminate();
					break;
				}

				log("search", "can not forward and backtrack");
				dfs1Stack.pop();
				boolean canBackTrack = backtrack();
				assert canBackTrack : "Can not dfs1.backtrack";
			}
		}

		notifySearchFinished();
	}

	protected boolean dfs2() {
		final int startDepth = depth;

		log("dfs2", "start dfs2 " + startDepth);

		if (vm.getChoiceGenerator() != null) {
			vm.getChoiceGenerator().reset();
		}

		while (true) {
			if (seenBefore(dfs2Table, 2)) {
				log("dfs2", "in table 2 -> backtrack " + depth + ", " + startDepth);
				if (depth == startDepth) {
					return false;
				}
				assert depth > startDepth : "depth > startDepth " + depth + ", " + startDepth;
				backtrack();
			}
			else {
				log("dfs2", "not in table 2 -> record");
				recordVisit(dfs2Table);
			}

			if (seenBefore(dfs1Stack, 1) && depth > startDepth) {
				log("dfs2", "in stack 1 -> return and will be violated");
				return true;
			}

			while (!forward()) {
				log("dfs2", "can not forward");
				if (depth == startDepth) {
					log("dfs2", "can not forward and backtracked some times and can not backtrack now -> return and will not be violated");
					return false;
				}
				log("dfs2", "can not forward -> backtrack");
				backtrack();
			}
		}
	}

	@Override
	protected boolean forward() {
		if (vm.isEndState()) {
			log("forward", "is end state -> can not foward");
			return false;
		}

		boolean canForward = true;
		int runsWithoutProgress = 0;
		int id;
		do {
			if (!vm.getCurrentThread().isRunnable()) {
				canForward = false;
				log("forward.not runable");
				break;
			}

			assert runsWithoutProgress < 2 : "Run without progress more than 2 times";

			haveProgress = true;
			symbolicLTLListener.setFirstStep();
			id = vm.getStateId();
			log("forward.while", haveProgress + ", " + id + ", " + runsWithoutProgress + ", " + canForward);
			canForward = super.forward();

			if (!haveProgress) {
				assert (haveProgress || vm.getSystemState().getNextChoiceGenerator() != null) : "Have no progress " + haveProgress + ", cg=" + vm.getSystemState().getNextChoiceGenerator();
			}

			runsWithoutProgress++;
			log("forward.while-->", haveProgress + ", " + id + ", " + runsWithoutProgress + ", " + canForward);
			if (canForward && !haveProgress && vm.getStateId() == id) {
				log("forward.while-->", "no progress -> forward again \n\n" + firstFewCGs());
			}
	    }
		while (canForward && !haveProgress && vm.getStateId() == id);

		log("forward?", canForward);
		if (canForward) {
			if (!createNextCg()) {
				log("forward", "can forward but can not create next cg -> backtrack and return as can not forward");
				super.backtrack();
				notifyStateBacktracked();
				return false;
			}

			log("forward", "can forward and created next cg");

			depth++;
			notifyStateAdvanced();

			return true;
		}
		else {
			log("forward", "can not forward");
			if (buchiCGs.isEmpty()) {
				return false;
			}
			log("forward", "can not forward and buchi not empty");

			BuchiCG<String> bcg = buchiCGs.peek();
			if (!bcg.hasMoreChoices()) {
				return false;
			}
			bcg.advance();
			log("forward", "can not forward and bcg has more choices and advanced bcg --> reset cg and try to forward again");

			ChoiceGenerator<?> cg = vm.getChoiceGenerator();
			cg.reset();

			return forward();
		}
	}

	@Override
	protected boolean backtrack() {
		boolean canBacktrack = super.backtrack();
		log("backtrack?", canBacktrack);

		if (canBacktrack || // TODO: SNAFU in jpf-core
				(vm.getStateId() == -1 && vm.getStateSet() != null)) {
			if (spec != null) {
				buchiCGs.pop();
			}

			depth--;
			notifyStateBacktracked();
			log("backtracked", "canBacktrack=" + canBacktrack + ", buchiCGs.size=" + buchiCGs.size());

			return true;
		}
		else {
			log("not backtracked", "canBacktrack=" + canBacktrack + ", buchiCGs.size=" + buchiCGs.size());
			return false;
		}
	}

	protected boolean seenBefore(HashMap<Integer, BitSet> table, int tableNum) {
		boolean seen;
		int stateId = vm.getStateId();
		if (!table.containsKey(stateId)) {
			seen = false;
		}
		else {
			Node<String> n = currentNode();
			if (n != null) {
				seen = table.get(stateId).get(currentNode().getId() + 1);
			}
			else {
				seen = table.get(stateId).get(0);
			}
		}
		log("isInTable?", seen + ", " + tableNum);
		return seen;
	}

	protected boolean seenBefore(LinkedList<Pair> stack, int stackNum) {
		boolean seen = stack.contains(new Pair(vm.getStateId(), currentNode()));
		log("isInStack?", seen + ", " + stackNum);
		return seen;
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

		Node<String> nextNode = cg.getNextChoice();
		BuchiCG<String> cgNew = new AtomBuchiCG(nextNode);
		if (cgNew.hasMoreChoices()) {
			cgNew.advance();
		}
		else {
			return false;
		}
		buchiCGs.push(cgNew);
		log("createNextCg", "buchiCGs.size=" + buchiCGs.size() + ", cgNew=" + cgNew);
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
		}
		r += ",\nCUR: ";
		cg = vm.getChoiceGenerator();
		for (; cg != null; cg = cg.getPreviousChoiceGenerator()) {
			if ((cg instanceof PCChoiceGenerator)) {
				r += cg.toString().trim() + "[" + cg.getProcessedNumberOfChoices() + "/" + cg.getTotalNumberOfChoices() + "]\n";
			}
		}
		return r;
	}

	private void log(String title) {
		log(title, "");
	}

	private void log(String title, Object others) {
		System.err.println(title + ": depth=" + depth + ", stateId=" + vm.getStateId() + /*", PC=" + vm.getCurrentThread ().getPC() + */", " + others /*+ ", pc=" + PathCondition.getPC(JVM.getVM())*/);
	}
}
