package org.matthiaszimmermann.ethereum.pwg;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PassPhraseUtilityTest {

	/**
	 * The first word from an actual run of the test. If the PassPhraseUtility always generates the same first word this would be it.
	 * With a chance of 1 in ~10k this test will fail.
	 */
	public static final String BAD_FIRST_WORD = "actress";
	public static final int WORD_LIST_SIZE = 9914;
	public static final String FIRST_WORD = "the";
	public static final String LAST_WORD = "poison"; 
	public static final String WORD_4815 = "mechanisms";
	public static final int WORD_4815_INDEX = 4815;
	
	private PassPhraseUtility utility;
	
	@Before
	public void oneTimeSetUp() {
		utility = new PassPhraseUtility();
		Assert.assertNotEquals("First word should be different but is not", utility.getNextWord(), BAD_FIRST_WORD);
	}
	
	@Test
	public void testWordList() {
		int words = utility.words();
		Assert.assertEquals("Unexpected word list size", WORD_LIST_SIZE, words);
		Assert.assertEquals("Unexpected first word", FIRST_WORD, utility.getWord(0));
		Assert.assertEquals("Unexpected word at index " + WORD_4815_INDEX, WORD_4815, utility.getWord(WORD_4815_INDEX));
		Assert.assertEquals("Unexpected last word", LAST_WORD, utility.getWord(words - 1));
	}
	
	@Test
	public void testSingleWordList() {
		String phrase1 = utility.getPassPhrase(1);
		String phrase2 = utility.getPassPhrase(1);
		
		checkWordList(phrase1, 1);
		checkWordList(phrase2, 1);
		
		Assert.assertNotSame("Pass phrases should not match", phrase1, phrase2);
	}
	
	@Test
	public void test16WordList() {
		int words = 16;
		
		String phrase1 = utility.getPassPhrase(words);
		String phrase2 = utility.getPassPhrase(words);
		
		checkWordList(phrase1, words);
		checkWordList(phrase2, words);
		
		Assert.assertNotSame("Pass phrases should not match", phrase1, phrase2);
	}
	
	private void checkWordList(String phrase, int words) {
		Assert.assertNotNull("Phrase must not be null", phrase);
		Assert.assertTrue("Phrase must not be empty", !phrase.isEmpty());
		
		String [] word = phrase.split(" ");
		
		Assert.assertEquals("Unexpected number of words in phrase", words, word.length);
	}
}
