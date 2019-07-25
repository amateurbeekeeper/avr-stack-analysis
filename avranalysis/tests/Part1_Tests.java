package avranalysis.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import avranalysis.core.StackAnalysis;
import javr.core.AvrInstruction;
import javr.io.HexFile;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Part1_Tests {

	@Test
	public void test_01() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.NOP()
		};
		// Check computation
		assertEquals(0,computeStackUsage(instructions));
	}

	@Test
	public void test_02() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.LDI(16,1)
		};
		// Check computation
		assertEquals(0,computeStackUsage(instructions));
	}

	@Test
	public void test_03() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.LDI(16,1),
				new AvrInstruction.LDI(17,2),
				new AvrInstruction.ADD(16, 17)
		};
		// Check computation
		assertEquals(0,computeStackUsage(instructions));
	}

	@Test
	public void test_04() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.LDI(16,1),
				new AvrInstruction.LDI(17,2),
				new AvrInstruction.SUB(16, 17)
		};
		// Check computation
		assertEquals(0,computeStackUsage(instructions));
	}

	@Test
	public void test_05() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.RJMP(1),
				new AvrInstruction.NOP(),
				new AvrInstruction.LDI(16,1)
		};
		// Check computation
		assertEquals(0,computeStackUsage(instructions));
	}

	@Test
	public void test_06() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.LDI(16,1),
				new AvrInstruction.PUSH(16)
		};
		// Check computation
		assertEquals(1,computeStackUsage(instructions));
	}

	@Test
	public void test_07() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.LDI(16,1),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.POP(16)
		};
		// Check computation
		assertEquals(1,computeStackUsage(instructions));
	}

	@Test
	public void test_08() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.LDI(16,1),
				new AvrInstruction.LDI(17,1),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.PUSH(17),
				new AvrInstruction.POP(17),
				new AvrInstruction.POP(16)
		};
		// Check computation
		assertEquals(2,computeStackUsage(instructions));
	}

	@Test
	public void test_09() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.LDI(16,1),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.LDI(17,1),
				new AvrInstruction.PUSH(17),
				new AvrInstruction.LDI(18,1),
				new AvrInstruction.PUSH(18),
				new AvrInstruction.POP(18),
				new AvrInstruction.POP(17),
				new AvrInstruction.POP(16)
		};
		// Check computation
		assertEquals(3,computeStackUsage(instructions));
	}

	@Test
	public void test_10() {
		// NOTE: JMP is a 32-bit instruction.
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.JMP(0x0004), // 0x0000
				new AvrInstruction.LDI(16,1),   // 0x0002
				new AvrInstruction.PUSH(16),    // 0x0003
				new AvrInstruction.LDI(16,1)    // 0x0004
		};
		// Check computation
		assertEquals(0,computeStackUsage(instructions));
	}

	@Test
	public void test_11() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.RJMP(2),
				new AvrInstruction.LDI(16,1),
				new AvrInstruction.POP(16),
				new AvrInstruction.LDI(16,1)
		};
		// Check computation
		assertEquals(0,computeStackUsage(instructions));
	}


	@Test
	public void test_12() {
		AvrInstruction[] instructions = new AvrInstruction[] {
				new AvrInstruction.RJMP(2),
				new AvrInstruction.LDI(16,1),
				new AvrInstruction.PUSH(16),
				new AvrInstruction.RJMP(2),
				new AvrInstruction.LDI(16,1),
				new AvrInstruction.POP(16),
				new AvrInstruction.NOP()
		};
		// Check computation
		assertEquals(0,computeStackUsage(instructions));
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
