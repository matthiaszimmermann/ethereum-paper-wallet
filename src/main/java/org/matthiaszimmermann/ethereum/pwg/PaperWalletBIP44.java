package org.matthiaszimmermann.ethereum.pwg;

import java.io.File;
import java.io.IOException;

import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;

public class PaperWalletBIP44 extends PaperWallet {

	private String mnemonic;

	public PaperWalletBIP44(String passPhrase, String pathToFile) throws Exception {
		this(passPhrase, pathToFile, null);
	}

	public PaperWalletBIP44(String passPhrase, String pathToFile, String mnemonics) throws Exception {
		this.passPhrase = setPassPhrase(passPhrase);
		this.pathToFile = setPathToFile(pathToFile);

		Bip39Wallet wallet;
		if (mnemonics == null) {
			wallet = generateBip44Wallet(this.passPhrase, new File(this.pathToFile));
		} else {
			wallet = Bip44WalletUtilsFixed.generateBip44Wallet(this.passPhrase, new File(this.pathToFile), mnemonics, false);
		}

		this.mnemonic = wallet.getMnemonic();
		fileName = wallet.getFilename();
		credentials = this.getCredentials(this.passPhrase);
	}

	private Bip39Wallet generateBip44Wallet(String password, File destinationDirectory) {
		try {
			return Bip44WalletUtilsFixed.generateBip44Wallet(password, destinationDirectory);
		} catch (CipherException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error on creating Bip44Wallet", e);
		}
	}

	@Override
	public Credentials getCredentials(String password) throws Exception {
		if (credentials != null) {
			return credentials;
		}

		try {
			credentials = Bip44WalletUtilsFixed.loadBip44Credentials(password, mnemonic);
			return credentials;
		} catch (Exception e) {
			throw new Exception("Failed to access credentials in file '" + getFile().getAbsolutePath() + "'", e);
		}
	}

	public String getMnemonic() {
		return mnemonic;
	}

}
