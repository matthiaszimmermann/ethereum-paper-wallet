package org.matthiaszimmermann.ethereum.pwg;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;

public class Bip44WalletTest {

	private File tempDir;
	private List<String> wordsEnglish;

	@Before
	public void setUp() throws Exception {
		tempDir = createTempDir();
		wordsEnglish = MnemonicUtils.getWords();

	}

	@After
	public void tearDown() throws Exception {
		for (File file : tempDir.listFiles()) {
			file.delete();
		}
		tempDir.delete();
	}

	// test case created using https://www.myetherwallet.com
	@Test
	public void addressTestMyEtherWallet() {
		String mnemonic ="giraffe animal normal scrap powder wave divorce pioneer wrist screen layer invest";
		String password = "";

		String addr1 = "0x87Ce6c41842D73F951D64C4FE7bf87caE92B2191";
		String addr2 = "0x8B80E64F354e9a8bC81DDa25ec23Fadc91D72A42";
		String addr3 = "0xfd7882415209720eB2eDab4F2ED498fE00151391";

		assertEquals(addr1.toLowerCase(), mnemonic2address(mnemonic, password, Bip44PathValues.ethMyEtherWallet(), true).toLowerCase());
		assertEquals(addr2.toLowerCase(), mnemonic2address(mnemonic, password, "m/44'/60'/0'/0/1", true).toLowerCase());
		assertEquals(addr3.toLowerCase(), mnemonic2address(mnemonic, password, "m/44'/60'/0'/0/2", true).toLowerCase());
	}

	// https://github.com/meherett/eth-wallet/blob/master/tests/test_from_mnemonic.py
	@Test
	public void addressTestMeherett() {
		String mnemonic ="indicate warm sock mistake code spot acid ribbon sing over taxi toast";
		String password = null;
		String address = mnemonic2address(mnemonic, password, Bip44PathValues.ethMeherett(), true);

		assertEquals("0xAaB4E88BCa0d7C1e40CE540b9642558d6f9a3a05".toLowerCase(), address.toLowerCase());

		System.out.println(address + " " + mnemonic);
	}

	// https://github.com/MyCryptoHQ/MyCrypto/blob/master/src/services/WalletService/wallets/MnemonicPhrase.test.ts
	// https://github.com/MyCryptoHQ/MyCrypto/blob/master/src/services/WalletService/wallets/__snapshots__/MnemonicPhrase.test.ts.snap
	@Test
	public void addressTestMyCrypto() {
		String mnemonic ="measure awake inject because stay profit soup dawn rare wave news cook";
		String password = null;

		assertEquals("0xBa0310bEE9fDd582530cd1cD0C29aCF7f03cC548".toLowerCase(), mnemonic2address(mnemonic, password, Bip44PathValues.ethMyCrypto(), true).toLowerCase());
		assertEquals("0x3B2006f42edfb0ABFDBB848A7fBe028A8E984e65".toLowerCase(), mnemonic2address(mnemonic, password, "m/44'/60'/0'/0/1", true).toLowerCase());

		assertEquals("0x9F8708a7091B34797a6810ab01Eacf2594e1e93d".toLowerCase(), mnemonic2address(mnemonic, password, "m/44'/60'/3'/0/0", true).toLowerCase());
		assertEquals("0x4029471cEA2B1cB785A5045d517523E91AB92569".toLowerCase(), mnemonic2address(mnemonic, password, "m/44'/60'/4'/0/0", true).toLowerCase());

		password = "foobar";

		assertEquals("0x91F81204910ab591d8ab1e7ab5db42D8FB48c646".toLowerCase(), mnemonic2address(mnemonic, password, Bip44PathValues.ethMyCrypto(), true).toLowerCase());
		assertEquals("0x65CF9d2AC5DFe4A27d22A48A9b898d7B01Ee1c4a".toLowerCase(), mnemonic2address(mnemonic, password, "m/44'/60'/0'/0/1", true).toLowerCase());
	}


	// From https://iancoleman.io/bip39/
	// https://github.com/iancoleman/bip39
	@Test
	public void addressTestIancoleman() throws Exception {
		String mnemonic = "kite scan embark dismiss text syrup salon butter cross rude hammer course";
		String password = "password";

		assertEquals("0x03C4b5778a3DEd3957890CFC9251AA1D1a521916".toLowerCase(), mnemonic2address(mnemonic, password, Bip44PathValues.ethDefault(), true).toLowerCase());
		assertEquals("99f739623389516f8f56479dcaba7aef36fb1a5687140c3ac141ba112e6dd6c5", mnemonic2privateKey(mnemonic, password, Bip44PathValues.ethDefault(), true));
		
		assertEquals("0x8FB2804A678F56de243EFE0f75e517dC771F09db".toLowerCase(), mnemonic2address(mnemonic, password, "m/44'/60'/0'/0/1", true).toLowerCase());
	}

	public String mnemonic2privateKey(String mnemonic, String password, String path, boolean sysoutPrint) {
		Credentials credentials = Bip44CredentialsUtility.createBip44Credentials(mnemonic, password, path);
		ECKeyPair keyPair = credentials.getEcKeyPair();
		
		return toHex(keyPair.getPrivateKey());
	}

	public String mnemonic2address(String mnemonic, String password, String path, boolean sysoutPrint) {
		Credentials credentials = Bip44CredentialsUtility.createBip44Credentials(mnemonic, password, path);

		ECKeyPair keyPair = credentials.getEcKeyPair();
		System.out.println("private key " + toHex(keyPair.getPrivateKey()));
		// returns uncompressed public key
		System.out.println("public key " + toHex(keyPair.getPublicKey()));

		String address = credentials.getAddress();

		if(sysoutPrint) {
			System.out.println("mnemonic: " + mnemonic);
			System.out.println("address: " + address);
		}

		return address;
	}

	public String toHex(BigInteger value) {
		BigInteger toHexInt = new BigInteger(value.toString(), 10);

		return toHexInt.toString(16);
	}

	public String createMnemonic(int wordIndex, int numWords) {
		if(wordIndex < 0) {
			wordIndex = wordsEnglish.size() + wordIndex;
			System.out.println(wordIndex);
		}

		if(numWords == 0) {
			numWords = 12;
		}

		String word = wordsEnglish.get(wordIndex);
		StringBuffer buf = new StringBuffer(word);

		for(int i = 1; i < numWords; i++) {
			buf.append(" ");
			buf.append(word);
		}

		return buf.toString();
	}


	static File createTempDir() throws Exception {
		return Files.createTempDirectory(PaperWalletBIP44Test.class.getSimpleName() + "-testkeys").toFile();
	}

}
