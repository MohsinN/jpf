/**
 * 
 */
package gov.nasa.jpf.ltl.finite;

import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.ltl.finite.trans.Node;

import java.util.Set;

import org.apache.bcel.classfile.ConstantPool;

/**
 * The {@code DummyInstruction} actually never executes - it is only a
 * placeholder for the first slot in a transition. As every instruction can
 * create more branches, when an instruction creates a new branch, its remaining
 * instructions are copied to the new branch after a {@code DummyInstruction} in
 * the first place. We need this trick because of the special treatment (no
 * choice generator created in the first instruction of a branch) with the first
 * instruction. This instruction is skipped when executed.
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
public class DummyInstruction extends Instruction {

  private Instruction nextPc;

  /**
   * Constructor.
   * 
   * @param nextIns
   *          The actual instruction that needs to execute.
   * @param nextStates
   *          The next successors that need to be set to the current states when
   *          the actual instruction of this executes.
   */
  public DummyInstruction(Instruction nextIns) {
    nextPc = nextIns;
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
   * @return The actual instruction needs to execute.
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
