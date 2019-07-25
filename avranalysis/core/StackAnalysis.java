package avranalysis.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javr.core.AvrDecoder;
import javr.core.AvrInstruction;
import javr.core.AvrInstruction.AbsoluteAddress;
import javr.core.AvrInstruction.FlagRelativeAddress;
import javr.core.AvrInstruction.RelativeAddress;
import javr.io.HexFile;
import javr.memory.ElasticByteMemory;

public class StackAnalysis {
  /**
   * Contains the raw bytes of the given firmware image being analysed.
   */
  private ElasticByteMemory firmware;

  /**
   * The decoder is used for actually decoding an instruction.
   */
  private AvrDecoder decoder = new AvrDecoder();

  /**
   * Records the maximum height seen so far.
   */
  private int maxHeight;

  private boolean m;

  private final int worstCase = Integer.MAX_VALUE;
  private Integer[] checkPC;
  private int maxPC = 0;

  /**
   *
   * @param firmware
   */
  public StackAnalysis(HexFile hf) {
    checkPC = new Integer[1000];
    Arrays.fill(checkPC, null);

    m = false;

    // Create firmware memory
    this.firmware = new ElasticByteMemory();
    // Upload image to firmware memory
    hf.uploadTo(firmware);
  }

  /**
   * Apply the stack analysis to the given firmware image producing a maximum
   * stack usage (in bytes).
   *
   * @return
   */
  public int apply() {
    // Reset the maximum, height
    this.maxHeight = 0;
    // Traverse instructions starting at beginning
    traverse(0, 0);
    // Return the maximum height observed
    return maxHeight;
  }

  /**
   * Traverse the instruction at a given pc address, assuming the stack has a
   * given height on entry.
   *
   * @param pc
   *          Program Counter of instruction to traverse
   * @param stackPointer
   *          Current height of the stack at this point (in bytes)
   * @return
   */
  private void traverse(int pc, int stackPointer) {
    maxHeight = Math.max(maxHeight, stackPointer);
    maxPC = Math.max(maxPC, pc);

    // Check whether we have terminated or not
    // We've gone over end of instruction sequence, so stop.
    if ((pc * 2) >= firmware.size()) return;

    if (m) return;

    if (checkPC[pc] != null) {                  // If instruction has been previously seen
      if (checkPC[pc] == stackPointer) {        // stack height unchanged: terminate
        m = true;
        return;
        
      } else if (checkPC[pc] < stackPointer) {  // else: stack height changed:
        stackPointer = worstCase;
        checkPC[pc] = worstCase;

        m = true;
      }         
    } else {                                    // mark as visited, and save currentHeight
      checkPC[pc] = stackPointer;
    }    

    // Move to the next logical instruction as this is always the starting point.
    AvrInstruction i = decodeInstructionAt(pc);
    // maxHeight = Math.max(maxHeight, stackPointer);
    int next = pc + i.getWidth();
    // maxHeight = Math.max(maxHeight, stackPointer);
    process(i, next, stackPointer);
  }

  /**
   * Process the effect of a given instruction.
   *
   * @param i
   *          Instruction to process
   * @param pc
   *          Program counter of following instruction - returning address
   * @param stackPointer
   *          Current height of the stack at this point (in bytes)
   */
  private void process(AvrInstruction i, int pc, int stackPointer) {
    switch (i.getOpcode()) {
   
    case RET: {
      break;
    }
    case RETI: {
      break;
    }
    case BREQ: {
      RelativeAddress branch = (RelativeAddress) i;
      
      if (branch.k != -1) { 
        traverse(pc + branch.k, stackPointer);
        traverse(pc, stackPointer);
        traverse(pc, stackPointer);
      }
      traverse(pc, stackPointer);

      break;
    }
    case BRGE: {
      RelativeAddress branch = (RelativeAddress) i;

      if (branch.k != -1) { 
        traverse(pc + branch.k, stackPointer);
        traverse(pc, stackPointer);
        
      }
      traverse(pc, stackPointer);

      break;
    }
    case BRLT: {
      RelativeAddress branch = (RelativeAddress) i;

      if (branch.k != -1) { 
        traverse(pc + branch.k, stackPointer);
        traverse(pc, stackPointer);
        traverse(pc, stackPointer);
      }

      break;
    }
    case SBRS: {

      
      traverse(pc + 1, stackPointer);
     
      traverse(pc, stackPointer);
      break;
    }
    case CALL: {
      AbsoluteAddress branch = (AbsoluteAddress) i;

      if (branch.k >= 0) {
        traverse(branch.k, stackPointer + 2);
        traverse(pc, stackPointer);
      }

      traverse(pc, stackPointer);

      break;
    }
    case RCALL: {
      RelativeAddress branch = (RelativeAddress) i;

      if (branch.k != -1) {
        traverse(pc + branch.k, stackPointer + 2);
        traverse(pc, stackPointer);
      }
      traverse(pc, stackPointer);

      break;
    }
    case JMP: {
      AbsoluteAddress branch = (AbsoluteAddress) i;

      if (branch.k >= 0) {
        traverse(branch.k, stackPointer);
      }
      break;
    }
    case RJMP: { 
      RelativeAddress branch = (RelativeAddress) i;

      if (branch.k != -1) {
        traverse(pc + branch.k, stackPointer);
      }
      break;
    }
    case PUSH: {
      traverse(pc, stackPointer + 1);
      break;
    }
    case POP: {
      traverse(pc, stackPointer - 1);
      break;
    }
    default: {
      traverse(pc, stackPointer);
      break;
    }

    }

  }

  /**
   * Decode the instruction at a given PC location.
   *
   * @param pc
   * @return
   */
  private AvrInstruction decodeInstructionAt(int pc) {
    return decoder.decode(firmware, pc);
  }
}
