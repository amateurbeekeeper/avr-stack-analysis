package avranalysis.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import avranalysis.core.StackAnalysis;
import javr.io.HexFile;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Part5_Tests {
	/**
	 * Identifies the directory in which the test firmwares are located.
	 */
	private static final String TESTS_DIR = "tests/".replace("/",File.separator);

	@Test
	public void test_01() throws IOException {
		// Check computation
		assertEquals(4,computeStackUsage("fader.hex"));
	}

	@Test
	public void test_02() throws IOException {
		// Check computation
		assertEquals(10,computeStackUsage("blocks.hex"));
	}

	@Test
	public void test_03() throws IOException {
		// Check computation
		assertEquals(10,computeStackUsage("blocks_2.hex"));
	}

	@Test
	public void test_04() throws IOException {
		// Check computation
		assertEquals(12,computeStackUsage("sokoban.hex"));
	}

	@Test
	public void test_05() throws IOException {
		// Check computation
		assertEquals(46,computeStackUsage("snake.hex"));
	}

	@Test
	public void test_06() throws IOException {
		// Check computation
		assertEquals(40,computeStackUsage("tetris.hex"));
	}

	/**
	 * For a given sequence of instructions compute the maximum stack usage.
	 *
	 * @param instructions
	 * @return
	 * @throws IOException
	 */
	private int computeStackUsage(String filename) throws IOException {
		// Read the firmware image
		HexFile.Reader hfr = new HexFile.Reader(new FileReader(TESTS_DIR + filename));
		HexFile hf = hfr.readAll();
		// Compute stack usage
		return new StackAnalysis(hf).apply();
	}
}
