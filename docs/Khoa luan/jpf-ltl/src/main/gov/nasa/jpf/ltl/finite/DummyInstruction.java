/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import java.util.Set;

import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import org.apache.bcel.classfile.ConstantPool;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.ltl.finite.trans.Node;

/**
 * Because we treat every execution step as the branching point, so every
 * transition has only one instruction. However, the bytecodes never create the
 * ChoiceGenerator at the beginning of transition. Therefore, we use this
 * instruction to hold the first slot in a transition. Actually, this
 * instruction is never executed; it always be skipped.
 * 
 * @author Phuc Nguyen Dinh
 * 
 */
public class DummyInstruction extends Instruction {

  private Instruction nextPc;
  Set<Node> successors;

  public DummyInstruction(Instruction nextIns, Set<Node> nextStates) {
    nextPc = nextIns;
    successors = nextStates;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nasa.jpf.jvm.bytecode.Instruction#execute(gov.nasa.jpf.jvm.SystemState,
   * gov.nasa.jpf.jvm.KernelState, gov.nasa.jpf.jvm.ThreadInfo)
   */
  @Override
  public Instruction execute(SystemState ss, KernelState ks, ThreadInfo ti) {
    return nextPc;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nasa.jpf.jvm.bytecode.Instruction#getByteCode()
   */
  @Override
  public int getByteCode() {
    return 0;
  }

  /**
   * @return The actual instruction to execute
   */
  @Override
  public Instruction getNext() {
    return nextPc;
  }

  /*
   * (non-Javadoc)
   * 
   * @seegov.nasa.jpf.jvm.bytecode.Instruction#setPeer(org.apache.bcel.generic.
   * Instruction, gov.nasa.jpf.jvm.bytecode.ConstantPool)
   */
  @Override
  protected void setPeer(org.apache.bcel.generic.Instruction i, ConstantPool cp) {

  }

}
