package sequence_diagram;

import java.util.Arrays;
import java.util.Vector;

import fsm.FSMControl;

/**
 * Tree that store combined fragments. Each node is a combined fragment.
 * The root is the whole diagram which considered as a combined fragment without 
 * any interaction operand. Each child node is a nested combined fragment of its parent
 * 
 * @author Phuc Nguyen Dinh
 */
class CombinedFragmentTree {
	Node root;

	CombinedFragmentTree() {
		root = new Node(null);
	}

	void add(CombinedFragment combinedFragment) {
		root.add(new Node(combinedFragment));
	}

	void postOrderTraverse(FSMControl fsmControl) {
		standardize();
		root.postOrderTraverse(fsmControl);
	}

	boolean isRoot(Node node) {
		return node.data == null;
	}

	void standardize() {
		root.preOrderTraverse();
		root.standardize();
	}

	private class Node implements Comparable<Node> {
		CombinedFragment data;
		Node parent;
		Vector<Node> childs;

		Node(CombinedFragment combinedFragment) {
			data = combinedFragment;
			parent = null;
			childs = new Vector<Node>();
		}

		boolean isContinuousNode(Node preNode) {
			return preNode.data.getFirstMessageAfterFragment().equals(
					data.getFirstMessage());
		}

		void processContinuousNode(Node preNode) {
			if (preNode.data instanceof Alt)
				data.addFirstState(((Alt) preNode.data).getLastIfMessage()
						.getTargetState());
			else
				for(String state: preNode.data.firstStates)
					data.addFirstState(state);
		}

		void standardize() {
			standardizeContinousFragment();
			standardizeLoop();
			standardizeAlt();
			for (Node child : childs)
				child.standardize();
		}

		/**
		 * Process cases where some combined fragments nested at the end of a
		 * loop fragment, so the program can loop without executing operand of
		 * these nested fragment.
		 */
		void standardizeLoop() {
			if (!(data instanceof Loop))
				return;
			Loop loop = (Loop) data;
			for (Node child : childs) {
				if (child.data.getLastMessage().equals(loop.getLastMessage()))
					if (child.data instanceof Alt)
						loop.addEndState(((Alt) child.data).getLastIfMessage()
								.getTargetState());
					else
						for (String state : data.firstStates)
							loop.addEndState(state);
			}
		}

		/**
		 * Add begin state to a fragment if any continuous fragments above it
		 */
		void standardizeContinousFragment() {
			// sort child fragments according to the order in the diagram
			if (childs.size() > 1) {
				Node[] childArray = new Node[childs.size()];
				childArray = childs.toArray(childArray);
				Arrays.sort(childArray);
				childs.removeAllElements();
				for (Node node : childArray)
					childs.add(node);
			}

			for (int i = 1; i < childs.size(); i++) {
				Node node = childs.get(i);
				Node preNode = childs.get(i - 1);
				if(node.isContinuousNode(preNode))
					node.processContinuousNode(preNode);
				for(Node childNode: preNode.childs)
					if(node.isContinuousNode(childNode))
						node.processContinuousNode(childNode);
			}
		}

		/**
		 * Process cases that one fragment is ending of alt's if-operand or
		 * beginning of alt's else-operand
		 */
		void standardizeAlt() {
			// find the nearest covered Alt instance fragment that cover this
			// fragment
			Node ancestor = this;
			while (!isRoot(ancestor)) {
				if (ancestor.data instanceof Alt)
					break;
				ancestor = ancestor.parent;
			}

			if (!isRoot(ancestor) && ancestor.data instanceof Alt
					&& !(data instanceof Break)) {
				Alt alt = (Alt) ancestor.data;

				/*
				 * case 1: this fragment is ending of if-operand, so first
				 * message after this fragment and the one after Alt fragment is
				 * the same
				 */
				if (data.getFirstMessageAfterFragment().equals(
						alt.getFirstElseMessage()))
					data.firstMessageAfterFragment = alt.firstMessageAfterFragment;

				/*
				 * case 2: this fragment is beginning of else-operand, so
				 * beginning state of this combined fragment is the one of alt
				 * fragment
				 */
				if (data.getFirstMessage().equals(alt.getFirstElseMessage()))
					data
							.addFirstState(alt.getFirstIfMessage()
									.getSourceState());
			}
		}

		boolean isInside(CombinedFragment combinedFragment) {
			if (data == null)
				return true;
			if (combinedFragment.left >= data.left
					&& combinedFragment.right <= data.right
					&& combinedFragment.top >= data.top
					&& combinedFragment.bottom <= data.bottom)
				return true;
			return false;
		}

		boolean isHigher(Node node) {
			return data.isHigher(node.data);
		}

		boolean isCover(CombinedFragment combinedFragment) {
			if (data == null)
				return false;
			if (combinedFragment.left <= data.left
					&& combinedFragment.right >= data.right
					&& combinedFragment.top <= data.top
					&& combinedFragment.bottom >= data.bottom)
				return true;
			return false;
		}

		void postOrderTraverse(FSMControl fsmControl) {
			for (Node child : childs)
				child.postOrderTraverse(fsmControl);
			visit(fsmControl);
		}
		
		void preOrderTraverse() {
			visit();
			for(Node child: childs)
				child.preOrderTraverse();
		}
		
		void visit() {
			if(data == null)
				return;
			data.setFirstStates();
		}

		void add(Node node) {
			for (Node child : childs)
				if (child.isInside(node.data)) {
					child.add(node);
					return;
				} else if (child.isCover(node.data)) {
					Node parent = child.parent;
					parent.remove(child);
					node.add(child);
					parent.add(node);
				}
			if (childs.contains(node))
				return;
			childs.add(node);
			node.parent = this;
		}

		void remove(Node node) {
			if (childs.contains(node))
				childs.remove(node);
		}

		void visit(FSMControl fsmControl) {
			if (data == null)
				return;
			data.FSMProcess(fsmControl);
		}

		@Override
		public int compareTo(Node node) {
			if (data.bottom > node.data.top)
				return 1;
			else if (data.bottom < node.data.top)
				return -1;
			return 0;
		}
	}
}