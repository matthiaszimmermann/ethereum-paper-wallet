package org.matthiaszimmermann.ethereum.pwg;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.web3j.crypto.MnemonicUtils;

public class MnemonicUtilsTest {

	private static final String SAMPLE_FILE = "src/test/resources/test-vectors.txt";

	public static Collection<String[]> readTestVectors() throws IOException {
		File file = new File(SAMPLE_FILE);
		assertTrue(file.exists());
		
		String data = Files.lines(Paths.get(SAMPLE_FILE)).collect(Collectors.joining("\n"));
		String[] each = data.split("###");

		List<String[]> samples = new ArrayList<>();
		for (String part : each) {
			String [] item = part.trim().split("\n");
			assertTrue(item.length == 3);
			
			samples.add(item);
		}

		return samples;
	}

	@Test
	public void runTests() throws IOException {
		Collection<String[]> testVectors = readTestVectors();

		for (String[] vector : testVectors) {
			String entropy = vector[0];
			String mnemonic = vector[1];
			String seed = vector[2];
			
			// check entropy -> mnemonic
	        String actualMnemonic = MnemonicUtils.generateMnemonic(Hex.decode(entropy));
	        assertEquals(String.format("mnemonic mismatch: expected %s, actual %s", mnemonic, actualMnemonic), mnemonic, actualMnemonic);

	        // check mnemonic -> entropy
	        byte [] actualEntropy = MnemonicUtils.generateEntropy(mnemonic);
	        assertArrayEquals("entropy mismatch", Hex.decode(entropy), actualEntropy);
	        
	        // check mnemonic -> seed
	        // passphrase "TROZOR" used in vector generation, see https://github.com/trezor/python-mnemonic/blob/master/generate_vectors.py
	        byte[] actualSeed = MnemonicUtils.generateSeed(mnemonic, "TREZOR");
	        assertArrayEquals("seed mismatch", Hex.decode(seed), actualSeed);
	        
	        System.out.println(String.format("test vector ok: entropy %s, mnemonic %s, seed %s", entropy, mnemonic, seed));
		}
	}
}
