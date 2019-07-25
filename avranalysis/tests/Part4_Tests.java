package avranalysis.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import avranalysis.core.StackAnalysis;
import javr.core.AvrInstruction;
import javr.io.HexFile;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Part4_Tests {
  

	@Test
	public void test_01() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.BREQ(2),
				new AvrInstruction.NOP(),
				new AvrInstruction.RJMP(-3),
				new AvrInstruction.RJMP(-1)
		};
		// Check computation
		assertEquals(0,computeStackUsage(instructions));
	}

	@Test
	public void test_02() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.BREQ(4),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.NOP(),
				new AvrInstruction.POP(16),
				new AvrInstruction.RJMP(-5),
				new AvrInstruction.RJMP(-1)
		};
		// Check computation
		assertEquals(1, computeStackUsage(instructions));
	}

	@Test
	public void test_03() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.RJMP(3),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.NOP(),
				new AvrInstruction.POP(16),
				new AvrInstruction.BRGE(-4),
				new AvrInstruction.RJMP(-1)
		};
		// Check computation
		assertEquals(1, computeStackUsage(instructions));
	}

	@Test
	public void test_04() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.RJMP(2),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.NOP(),
				new AvrInstruction.BRGE(-3),
				new AvrInstruction.RJMP(-1)
		};
		// Check computation
		assertEquals(Integer.MAX_VALUE,computeStackUsage(instructions));
	}

	@Test
	public void test_05() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.RJMP(3),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.RCALL(3),
				new AvrInstruction.POP(16),
				new AvrInstruction.BRGE(-4),
				new AvrInstruction.RJMP(-1),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.NOP(),
				new AvrInstruction.POP(16),
				new AvrInstruction.RET()
		};
		// Check computation
		assertEquals(4,computeStackUsage(instructions));
	}

	@Test
	public void test_06() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.RJMP(4),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.SBRS(16,1),
				new AvrInstruction.RCALL(3),
				new AvrInstruction.POP(16),
				new AvrInstruction.BRGE(-5),
				new AvrInstruction.RJMP(-1),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.NOP(),
				new AvrInstruction.POP(16),
				new AvrInstruction.RET()
		};
		// Check computation
		assertEquals(4,computeStackUsage(instructions));
	}

	@Test
	public void test_07() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.PUSH(16),
				new AvrInstruction.RCALL(4),
				new AvrInstruction.POP(16),
				new AvrInstruction.SBRS(16,1),
				new AvrInstruction.RJMP(-5),
				new AvrInstruction.RJMP(-1),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.NOP(),
				new AvrInstruction.POP(16),
				new AvrInstruction.RET()
		};
		// Check computation
		assertEquals(4,computeStackUsage(instructions));
	}

	@Test
	public void test_08() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.CALL(0x0009), // 0x0000
				new AvrInstruction.CALL(0x0005), // 0x0002
				new AvrInstruction.RJMP(-1),     // 0x0004
				new AvrInstruction.RCALL(1),     // 0x0005
				new AvrInstruction.RET(),        // 0x0006
				new AvrInstruction.RCALL(1),     // 0x0007
				new AvrInstruction.RET(),        // 0x0008
				new AvrInstruction.BREQ(2),      // 0x0009
				new AvrInstruction.NOP(),        // 0x000A
				new AvrInstruction.RJMP(-3),     // 0x000B
				new AvrInstruction.RET(),        // 0x000C
		};
		// Check computation
		assertEquals(6,computeStackUsage(instructions));
	}


	@Test
	public void test_09() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.CALL(0x0009), // 0x0000
				new AvrInstruction.CALL(0x0005), // 0x0002
				new AvrInstruction.RJMP(-1),     // 0x0004
				new AvrInstruction.RCALL(1),     // 0x0005
				new AvrInstruction.RET(),        // 0x0006
				new AvrInstruction.RCALL(1),     // 0x0007
				new AvrInstruction.RET(),        // 0x0008
				new AvrInstruction.PUSH(16),     // 0x0009
				new AvrInstruction.PUSH(17),     // 0x000A
				new AvrInstruction.PUSH(18),     // 0x000B
				new AvrInstruction.BREQ(2),      // 0x000C
				new AvrInstruction.NOP(),        // 0x000D
				new AvrInstruction.RJMP(-3),     // 0x000E
				new AvrInstruction.POP(16),      // 0x000F
				new AvrInstruction.POP(17),      // 0x0010
				new AvrInstruction.POP(18),      // 0x0011
				new AvrInstruction.RET(),        // 0x0012
		};
		// Check computation
		assertEquals(9,computeStackUsage(instructions));
	}
	@Test
	public void test_10() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.RCALL(2),
				new AvrInstruction.RJMP(-1),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.RCALL(-2), // recurse
				new AvrInstruction.POP(16),
				new AvrInstruction.RET()
		};
		// Check computation
		assertEquals(Integer.MAX_VALUE,computeStackUsage(instructions));
	}

	@Test
	public void test_11() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.RCALL(2),
				new AvrInstruction.RJMP(-1),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.RCALL(2), // mutually recurse
				new AvrInstruction.POP(16),
				new AvrInstruction.RET(),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.RCALL(-6), // mutually recurse
				new AvrInstruction.POP(16),
				new AvrInstruction.RET(),
		};
		// Check computation
		assertEquals(Integer.MAX_VALUE,computeStackUsage(instructions));
	}

	@Test
	public void test_12() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.RCALL(2),
				new AvrInstruction.RJMP(-1),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.RCALL(2), // mutually recurse
				new AvrInstruction.POP(16),
				new AvrInstruction.RET(),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.RCALL(2), // mutually recurse
				new AvrInstruction.POP(16),
				new AvrInstruction.RET(),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.RCALL(-10), // mutually recurse
				new AvrInstruction.POP(16),
				new AvrInstruction.RET(),
		};
		// Check computation
		assertEquals(Integer.MAX_VALUE,computeStackUsage(instructions));
	}

	/**
	 * For a given sequence of instructions compute the maximum stack usage.
	 *
	 * @param instructions
	 * @return
	 */
	private int computeStackUsage(AvrInstruction... instructions) {
		// Assemble instructions into hexfile
		HexFile hf = assemble(instructions);
		// Compute stack usage
		return new StackAnalysis(hf).apply();
	}

	/**
	 * Responsible for turning a given sequence of instructions into a hexfile, so
	 * that it can in turn be uploaded to the stack analysis.
	 *
	 * @param instructions
	 * @return
	 */
	private HexFile assemble(AvrInstruction... instructions) {
		byte[][] chunks = new byte[instructions.length][];
		int total = 0;
		// Encode each instruction into a byte sequence
		for(int i=0;i!=instructions.length;++i) {
			byte[] bytes = instructions[i].getBytes();
			chunks[i] = bytes;
			total = total + bytes.length;
		}
		// Flatten the chunks into a sequence
		byte[] sequence = new byte[total];
		//
		for(int i=0,j=0;i!=chunks.length;++i) {
			byte[] chunk = chunks[i];
			System.arraycopy(chunk, 0, sequence, j, chunk.length);
			j = j + chunk.length;
		}
		// Finally, create the hex file!
		return HexFile.toHexFile(sequence,16);
	}
}
