package org.matthiaszimmermann.ethereum.pwg;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;

public class Bip44CredentialsUtility {

	public static Credentials createBip44Credentials(String mnemonic) {
		return createBip44Credentials(mnemonic, null);
	}

	public static Credentials createBip44Credentials(String mnemonic, String password) {
		return createBip44Credentials(mnemonic, password, Bip44PathValues.ETH_DEFAULT);
	}

	public static Credentials createBip44Credentials(String mnemonic, String password, String bip44path) {
		byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
		Bip44Path path = new Bip44Path(bip44path);
		Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
		Bip32ECKeyPair bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path.getPathArray());

		return Credentials.create(bip44Keypair);
	}

}
