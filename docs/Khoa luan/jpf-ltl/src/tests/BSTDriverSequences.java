


import java.util.Iterator;

import gov.nasa.jpf.jvm.Verify;
import gov.nasa.jpf.symbc.Subsumption;


/**
 *
 * @author Mithun Acharya
 *
 *  * Arguments for concrete execution:
 * BSTDriverSequences
 *
 * Arguments for symbolic execution:
 * +vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory
 * +vm.classpath=.
 * +vm.storage.class=
 * +search.multiple_errors=true
 * +symbolic.method=add(sym);remove(sym);find(sym)
 * +jpf.report.console.finished=
 * +jpf.listener=gov.nasa.jpf.symbc.sequences.SymbolicSequenceListener
 * BSTDriverSequences
 *
 */
public class BSTDriverSequences {

	public static void testDriver(int length){
		BST t = new BST();
		
		for (int i=0; i<length; i++){
			Verify.beginAtomic();
			switch (Verify.random(1)){
			case 0:
				System.out.println("adding...");
				t.add(0);
				break;
			case 1:
				System.out.println("removing...");
				t.remove(0);
				break;
			case 2:
				System.out.println("finding...");
				t.find(0);
				break;
			}
			Verify.endAtomic();
			
			Verify.ignoreIf(Subsumption.storeAndCheckSubsumption());

		}
	}
	
	public static void main(String[] args){
		testDriver(4); // with 2 you do not get complete coverage
		Subsumption.printAllStates();
		System.out.println();
	}
}
