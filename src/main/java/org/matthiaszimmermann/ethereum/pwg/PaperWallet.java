package org.matthiaszimmermann.ethereum.pwg;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PaperWallet {

	public static final int PHRASE_SIZE_DEFAULT = 8;
	public static final String WALLET_OK = "OK";
	public static final String WALLET_ERROR = "ERROR";

	private static PassPhraseUtility passPhraseUtility = new PassPhraseUtility();

	private Credentials credentials = null;
	private String fileName;
	private String pathToFile;
	private String passPhrase;

	public PaperWallet(String passPhrase, String pathToFile) throws Exception {
		this.passPhrase = setPassPhrase(passPhrase);
		this.pathToFile = setPathToFile(pathToFile);

		try {
			fileName = WalletUtils.generateNewWalletFile(this.passPhrase, new File(this.pathToFile));
			credentials = getCredentials(this.passPhrase);
		}
		catch (Exception e) {
			throw new Exception("Failed to create account", e);
		}
	}

	public static String getPathToFileDefault() {
		return WalletUtils.getDefaultKeyDirectory();
	}
	
	public static String checkWalletFileStatus(File sourceFile, String passPhrase) {
		// check if provided file exists
		if(!sourceFile.exists() || sourceFile.isDirectory()) { 
			return String.format("%s file does not exist or is a directory", WALLET_ERROR);
		}
		
		WalletFile walletFile = null;

		// try to create wallet file object
		try {
	        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
	        walletFile = objectMapper.readValue(sourceFile, WalletFile.class);
		} 
		catch (Exception e) {
			String message = e.getLocalizedMessage();
			
			if(message == null) {
				message = "general wallet file format error";
			}
			
			return String.format("%s %s", WALLET_ERROR, message);
		}
		
		// try to decrypt wallet file object
		ECKeyPair keyPair = null;
		
		try {
	        keyPair = Wallet.decrypt(passPhrase, walletFile);
		} 
		catch (Exception e) {
			String message = e.getLocalizedMessage();
			
			if(message == null) {
				message = "general wallet file decryption error";
			}
			
			return String.format("%s %s", WALLET_ERROR, message);
		}
		
		if(keyPair != null) {
			String privateK = encode(keyPair.getPrivateKey());
			String publicK = encode(keyPair.getPublicKey());
			return String.format("%s\npublic key: %s\nprivate key: %s", WALLET_OK, publicK, privateK);
		}
		else {
			return WALLET_OK;
		}
	}

	public Credentials getCredentials(String passPhrase) throws Exception {
		if (credentials != null) {
			return credentials;
		}

		try {
			String fileWithPath = getFile().getAbsolutePath();
			credentials = WalletUtils.loadCredentials(passPhrase, fileWithPath);

			return credentials;
		}
		catch (Exception e) {
			throw new Exception ("Failed to access credentials in file '" + getFile().getAbsolutePath() + "'", e);
		}
	}

	public String getAddress() {
		return credentials.getAddress();
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public String getPathToFile() {
		return pathToFile;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileContent() throws Exception {
		try {
			return String.join("", Files.readAllLines(getFile().toPath()));
		} 
		catch (IOException e) {
			throw new Exception ("Failed to read content from file '" + getFile().getAbsolutePath() + "'", e);
		}
	}

	public File getFile() {
		return new File(pathToFile, fileName);
	}

	private String setPassPhrase(String passPhrase) {
		if(passPhrase == null || passPhrase.isEmpty()) {
			return passPhraseUtility.getPassPhrase(PHRASE_SIZE_DEFAULT);
		}

		return passPhrase;
	}

	private String setPathToFile(String pathToFile) {
		if(pathToFile == null || pathToFile.isEmpty()) {
			return getPathToFileDefault();
		}

		return pathToFile;
	}

	public String getBaseName() {
		if(fileName == null) {
			return null;
		}
		
		int pos = fileName.lastIndexOf(".");
		
		if(pos >= 0) {
			return fileName.substring(0, pos);
		}
		
		return fileName;
	}
	
	private static String encode(BigInteger number) {
		// TODO check that this is the desired encoding...
		return Keys.getAddress(number);
	}

}
