package org.matthiaszimmermann.ethereum.pwg;

public class Bip44PathValues {

	// default path according to 
	// https://iancoleman.io/bip39/
	// https://github.com/MyCryptoHQ/MyCrypto/blob/master/src/services/WalletService/wallets/__snapshots__/MnemonicPhrase.test.ts.snap
	// https://docs.trezor.io/trezor-firmware/misc/coins-bip44-paths.html
	public static final String ETH_DEFAULT = "m/44'/60'/0'/0/0";
	
	// https://github.com/meherett/eth-wallet/blob/master/tests/test_from_mnemonic.py
	public static final String ETH_MEHERETT = "m/44'/60'/0'/0/0'";
	
	// https://github.com/MyCryptoHQ/MyCrypto/blob/master/src/config/dpaths.ts
	public static final String ETH_MY_CRYPTO = ETH_DEFAULT;
	
	// https://www.myetherwallet.com
	public static final String ETH_MEW = ETH_DEFAULT;

	// https://docs.trezor.io/trezor-firmware/misc/coins-bip44-paths.html
	public static final String ETH_TREZOR = ETH_DEFAULT;
	public static final String ETH_TREZOR_FORMAT = "m/44'/60'/0'/0/%d";
	
	// https://github.com/web3j/web3j/blob/35c6d46332089bd9921c710889fc175410110d50/core/src/main/java/org/web3j/crypto/Bip44WalletUtils.java#L67
	public static final String ETH_WEB3J_PATH = "m/44'/60'/0'/0";
	
	public static String ethDefault() {
		return ETH_DEFAULT;
	}
	
	public static String ethMeherett() {
		return ETH_MEHERETT;
	}
	
	public static String ethMyEtherWallet() {
		return ETH_MEW;
	}
	
	public static String ethMyCrypto() {
		return ETH_MY_CRYPTO;
	}
	
	public static String ethTrezor() {
		return ETH_TREZOR;
	}
	
	public static String ethTrezor(int accountNumber) {
		return String.format(ETH_TREZOR_FORMAT, accountNumber);
	}
	
	public static String ethWeb3j() {
		return ETH_WEB3J_PATH;
	}
}
