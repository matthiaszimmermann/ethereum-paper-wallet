package org.matthiaszimmermann.ethereum.pwg;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Numeric;

public class PaperWalletBIP44Test {

	private File tempDir;

	@Before
	public void setUp() throws Exception {
		tempDir = createTempDir();

	}

	@After
	public void tearDown() throws Exception {
		for (File file : tempDir.listFiles()) {
			file.delete();
		}
		tempDir.delete();
	}

	@Test
	public void paperWalletBIP44CreationAndLoading() throws Exception {
		String password = "password";

		// generation
		PaperWalletBIP44 pw = new PaperWalletBIP44(password, tempDir.getAbsolutePath());
		Credentials expectedCredentials = pw.getCredentials(password);

		// loading form file
		PaperWallet pw2 = new PaperWallet(password, new File(tempDir.getAbsolutePath() + "/" + pw.fileName));
		Credentials actualCredentials = pw2.getCredentials(password);

		assertEquals(expectedCredentials.getEcKeyPair().getPrivateKey(), actualCredentials.getEcKeyPair().getPrivateKey());
		assertEquals(expectedCredentials.getEcKeyPair().getPublicKey(), actualCredentials.getEcKeyPair().getPublicKey());

	}

	@Test
	public void paperWalletBIP44CreationAndLoadingFromMnemonic() throws Exception {
		String password = "password";

		// generation
		PaperWalletBIP44 pw = new PaperWalletBIP44(password, tempDir.getAbsolutePath());
		String mnemonic = pw.getMnemonic();
		Credentials expectedCredentials = pw.getCredentials(password);

		// generating from mnemonic
		PaperWallet pw2 = new PaperWalletBIP44(password, tempDir.getAbsolutePath(), mnemonic);
		Credentials actualCredentials = pw2.getCredentials(password);

		assertEquals(expectedCredentials.getEcKeyPair().getPrivateKey(), actualCredentials.getEcKeyPair().getPrivateKey());
		assertEquals(expectedCredentials.getEcKeyPair().getPublicKey(), actualCredentials.getEcKeyPair().getPublicKey());

	}

	@Test
	public void credentialsValidationsBip44AndBip39() throws Exception {
		// From https://iancoleman.io/bip39/
		// BIP39 Mnemonic = kite scan embark dismiss text syrup salon butter cross rude
		// hammer course
		// BIP39 Passphrase (optional) = password
		// pk = 0x99f739623389516f8f56479dcaba7aef36fb1a5687140c3ac141ba112e6dd6c5
		// address = 0x03C4b5778a3DEd3957890CFC9251AA1D1a521916

		String password = "password";
		String mnemonic = "kite scan embark dismiss text syrup salon butter cross rude hammer course";
		String expectedPk = "0x99f739623389516f8f56479dcaba7aef36fb1a5687140c3ac141ba112e6dd6c5";
		String exepectedAddress = "0x03C4b5778a3DEd3957890CFC9251AA1D1a521916";

		// generating from mnemonic
		PaperWallet pw = new PaperWalletBIP44(password, tempDir.getAbsolutePath(), mnemonic);
		Credentials actualCredentials = pw.getCredentials(password);
		String actualPk = Numeric.toHexStringWithPrefix(actualCredentials.getEcKeyPair().getPrivateKey());

		assertEquals(expectedPk, actualPk);
		assertEquals(exepectedAddress.toLowerCase(), actualCredentials.getAddress());
	}

	static File createTempDir() throws Exception {
		return Files.createTempDirectory(PaperWalletBIP44Test.class.getSimpleName() + "-testkeys").toFile();
	}
}
