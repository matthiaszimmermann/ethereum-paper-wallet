package org.matthiaszimmermann.ethereum.pwg;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

public class PaperWallet {
	
	public static final int PHRASE_SIZE_DEFAULT = 8;
	
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

	private String setPassPhrase(String passPhrase) {
		if(passPhrase == null || passPhrase.isEmpty()) {
			return passPhraseUtility.getPassPhrase(PHRASE_SIZE_DEFAULT);
		}
		
		return passPhrase;
	}

	private String setPathToFile(String pathToFile) {
		if(pathToFile == null || pathToFile.isEmpty()) {
			return WalletUtils.getDefaultKeyDirectory();
		}
		
		return pathToFile;
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

}
