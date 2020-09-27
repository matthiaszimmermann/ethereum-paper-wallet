package org.matthiaszimmermann.ethereum.pwg;

import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.LinuxSecureRandom;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletUtils;

public class Bip44WalletUtilsFixed extends WalletUtils {

    /**
     * Generates a BIP-44 compatible Ethereum wallet on top of BIP-39 generated seed.
     *
     * @param password Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param destinationDirectory The directory containing the wallet
     * @return A BIP-39 compatible Ethereum wallet
     * @throws CipherException if the underlying cipher is not available
     * @throws IOException if the destination cannot be written to
     */
    public static Bip39Wallet generateBip44Wallet(String password, File destinationDirectory)
            throws CipherException, IOException {
        return generateBip44Wallet(password, destinationDirectory, false);
    }

    /**
     * Generates a BIP-44 compatible Ethereum wallet on top of BIP-39 generated seed.
     *
     * @param password Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param destinationDirectory The directory containing the wallet
     * @param testNet should use the testNet derive path
     * @return A BIP-39 compatible Ethereum wallet
     * @throws CipherException if the underlying cipher is not available
     * @throws IOException if the destination cannot be written to
     */
    public static Bip39Wallet generateBip44Wallet(
            String password, File destinationDirectory, boolean testNet)
            throws CipherException, IOException {
        byte[] initialEntropy = new byte[16];
        SecureRandomUtils.secureRandom().nextBytes(initialEntropy);

        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);

        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, testNet);

        String walletFile = generateWalletFile(password, bip44Keypair, destinationDirectory, false);

        return new Bip39Wallet(walletFile, mnemonic);
    }
    
    public static Bip39Wallet generateBip44Wallet(
            String password, File destinationDirectory, String mnemonic, boolean testNet)
            throws CipherException, IOException {
   
    	if(!MnemonicUtils.validateMnemonic(mnemonic)) {
    		throw new RuntimeException("Invalid mnemonic!");
    	}
    	
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);

        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, testNet);

        String walletFile = generateWalletFile(password, bip44Keypair, destinationDirectory, false);

        return new Bip39Wallet(walletFile, mnemonic);
    }

    public static Bip32ECKeyPair generateBip44KeyPair(Bip32ECKeyPair master) {
        return generateBip44KeyPair(master, false);
    }

    public static Bip32ECKeyPair generateBip44KeyPair(Bip32ECKeyPair master, boolean testNet) {
        if (testNet) {
            // /m/44'/0'/0
            final int[] path = {44 | HARDENED_BIT, 0 | HARDENED_BIT, 0 | HARDENED_BIT, 0,0};
            return Bip32ECKeyPair.deriveKeyPair(master, path);
        } else {
            // m/44'/60'/0'/0
            //FIX DONE HERE: final int[] path = {44 | HARDENED_BIT, 60 | HARDENED_BIT, 0 | HARDENED_BIT, 0};
			final int[] path = { 44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0,0};
            return Bip32ECKeyPair.deriveKeyPair(master, path);
        }
    }

    public static Credentials loadBip44Credentials(String password, String mnemonic) {
        return loadBip44Credentials(password, mnemonic, false);
    }

    public static Credentials loadBip44Credentials(
            String password, String mnemonic, boolean testNet) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        Bip32ECKeyPair bip44Keypair = generateBip44KeyPair(masterKeypair, testNet);
        return Credentials.create(bip44Keypair);
    }
        
}

final class SecureRandomUtils {

    private static final SecureRandom SECURE_RANDOM;

    static {
        if (isAndroidRuntime()) {
            new LinuxSecureRandom();
        }
        SECURE_RANDOM = new SecureRandom();
    }

    static SecureRandom secureRandom() {
        return SECURE_RANDOM;
    }

    // Taken from BitcoinJ implementation
    // https://github.com/bitcoinj/bitcoinj/blob/3cb1f6c6c589f84fe6e1fb56bf26d94cccc85429/core/src/main/java/org/bitcoinj/core/Utils.java#L573
    private static int isAndroid = -1;

    static boolean isAndroidRuntime() {
        if (isAndroid == -1) {
            final String runtime = System.getProperty("java.runtime.name");
            isAndroid = (runtime != null && runtime.equals("Android Runtime")) ? 1 : 0;
        }
        return isAndroid == 1;
    }

    private SecureRandomUtils() {}
}

