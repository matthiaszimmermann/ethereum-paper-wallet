package org.matthiaszimmermann.ethereum.pwg;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

public class PassPhraseUtility {

	public static final String LEXICON_FILE = "/google-10000-english-no-swears.txt";
	public static final String RANDOM_GENERATOR_ALGORITHM = "SHA1PRNG";
	public static final int RANDOM_SEED_BYTES = 8;
	
	private List<String> word;
	private int words;
	private SecureRandom rng;
	
	public PassPhraseUtility() {
		try {
			initWordList();
			initRandomizer();
		} 
		catch (Exception e) {
			throw new RuntimeException("Failed to create a pass phrase utility", e);
		}
	}	
	
	private void initWordList() throws Exception  {
		word = FileUtility.getResourceAsStrings(LEXICON_FILE);
		words = word.size();
	}
	
	private void initRandomizer() throws NoSuchAlgorithmException {
		rng = SecureRandom.getInstance(RANDOM_GENERATOR_ALGORITHM);
		rng.setSeed(rng.generateSeed(RANDOM_SEED_BYTES));
	}
	
	public String getPassPhrase(int numberOfWords) {
		if(numberOfWords <= 0) {
			throw new IllegalArgumentException("Phass phrase must consist of at least one word");
		}
		
		String [] phrase = new String[numberOfWords];
		for(int i = 0; i < numberOfWords; i++) {
			phrase[i] = getNextWord();
		}
		
		return String.join(" ", phrase);
	}
	
	public String getNextWord() {
		int idx = rng.nextInt(words);
		return word.get(idx);
	}
	
	public String getWord(int index) {
		int words = word.size();
		
		if(index < 0 || index > words - 1) {
			throw new IllegalArgumentException("Index not in allowed range [0.." + words + "), index=" + index);
		}
		
		return word.get(index);
	}
	
	public int words() {
		return word.size();
	}
}
