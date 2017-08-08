package org.matthiaszimmermann.ethereum.pwg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApplicationTest {

	public static final String WALLET_JSON_PASS_PHRASE = "good pass phrase";
	public static final String WALLET_JSON_OK = "{\"address\":\"f51663d9e8f853af0a255fc0b97ce5826a18b4be\",\"id\":\"7990fdfc-0dc7-47cd-86ee-98f396e9f3e2\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"72147bf6e633eda3cad6df2d4b73d7be7c6c5ed9451fde452ce34b7628825982\",\"cipherparams\":{\"iv\":\"df6cbfb95bed1456c1cde987324da22c\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"ef24709bb7d4ab5a01c323e3adb68fd360f2a8d331876d2de760f97b35904bb9\"},\"mac\":\"522ab7b12f491f3ca005380f7fe31883f7c9c21b3cf7a4d47b74cd6c22b8a4a6\"}}";
	public static final String WALLET_JSON_CORRUPT_1 = "{\"address\":\"f51663d9e8f853af0a255fc0b97ce5826a18b4be\",\"id\":\"7990fdfc-0dc7-47cd-86ee-98f396e9f3e2\",\"version\":4,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"72147bf6e633eda3cad6df2d4b73d7be7c6c5ed9451fde452ce34b7628825982\",\"cipherparams\":{\"iv\":\"df6cbfb95bed1456c1cde987324da22c\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"ef24709bb7d4ab5a01c323e3adb68fd360f2a8d331876d2de760f97b35904bb9\"},\"mac\":\"522ab7b12f491f3ca005380f7fe31883f7c9c21b3cf7a4d47b74cd6c22b8a4a6\"}}";
	public static final String WALLET_JSON_CORRUPT_2 = "{\"address\":\"f51663d9e8f853af0a255fc0b97ce5826a18b4be\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"72147bf6e633eda3cad6df2d4b73d7be7c6c5ed9451fde452ce34b7628825982\",\"cipherparams\":{\"iv\":\"df6cbfb95bed1456c1cde987324da22c\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"salt\":\"ef24709bb7d4ab5a01c323e3adb68fd360f2a8d331876d2de760f97b35904bb9\"},\"mac\":\"522ab7b12f491f3ca005380f7fe31883f7c9c21b3cf7a4d47b74cd6c22b8a4a6\"}}";

	private static List<File> tmpFile = new ArrayList<>();
	private static String tmpFilePath;
	private static boolean setupFailed = false;

	@BeforeClass
	public static void setUp() {
		try {
			File f = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
			String p = f.getAbsolutePath();
			tmpFilePath = p.substring(0, p.lastIndexOf(File.separator));
			tmpFile.add(f);

		} 
		catch (IOException e) {
			setupFailed = true;
		}
	}

	@AfterClass
	public static void tearDown() {
		for(File f: tmpFile) {
			System.out.print("deleting temp file " + f.getAbsolutePath() + " ... ");
			if(f != null && f.exists()) {
				f.delete();
				System.out.println(" done");
			}
			else {
				System.out.println(" no such file found");
			}
		}
	}

	@Test
	public void verifySetup() {
		Assert.assertTrue("failed to get path to temp directory", !setupFailed);
	}

	@Test
	public void createWalletHappyCase() {
		if(setupFailed) {
			return;
		}

		String passPhrase = "test pass phrase";
		String [] args = new String [] { Application.SWITCH_DIRECTORY, tmpFilePath, Application.SWITCH_PASS_PHRASE, passPhrase};
		Application app = new Application();
		String message = app.run(args);
		boolean isOkMessage = message.startsWith(Application.CREATE_OK);

		Assert.assertTrue(String.format("failed to write paper wallet to directory %s: expected message '%s ...', actual message: '%s'", tmpFilePath, Application.CREATE_OK, message), isOkMessage);

		if(isOkMessage) {
			File jsonFile = new File(okMessageToJsonFileName(message));
			File htmlFile = deriveFile(jsonFile, Application.EXT_HTML);
			File pngFile = deriveFile(jsonFile, Application.EXT_PNG);

			Assert.assertTrue("failed to create json file " + jsonFile.getAbsolutePath(), jsonFile.exists());
			Assert.assertTrue("failed to create html file " + htmlFile.getAbsolutePath(), htmlFile.exists());
			Assert.assertTrue("failed to create png file " + pngFile.getAbsolutePath(), pngFile.exists());

			tmpFile.add(jsonFile);
			tmpFile.add(htmlFile);
			tmpFile.add(pngFile);
		}
	}

	@Test
	public void verifyWalletHappyCase() {
		if(setupFailed) {
			return;
		}
		
		String jsonFile = String.format("%s%s%s", tmpFilePath, File.separator, "wallet_verify_ok.json");
		FileUtility.saveToFile(WALLET_JSON_OK, jsonFile);
		
		String [] args = new String [] { Application.SWITCH_DIRECTORY, tmpFilePath, Application.SWITCH_PASS_PHRASE, WALLET_JSON_PASS_PHRASE, Application.SWITCH_WALLET, jsonFile, Application.SWITCH_VERIFY};
		Application app = new Application();
		String message = app.run(args);
		boolean isOkMessage = message.startsWith(Application.VERIFY_OK);

		Assert.assertTrue(String.format("failed to verify paper wallet %s: expected message '%s ...', actual message: '%s'", jsonFile, Application.VERIFY_OK, message), isOkMessage);
		tmpFile.add(new File(jsonFile));
	}

	@Test
	public void createAndVerifyWalletHappyCase() {
		if(setupFailed) {
			return;
		}

		// create wallet file
		String passPhrase = "hi";
		String [] args = new String [] { Application.SWITCH_DIRECTORY, tmpFilePath, Application.SWITCH_PASS_PHRASE, passPhrase};
		Application app = new Application();
		String message = app.run(args);
		boolean isOkMessage = message.startsWith(Application.CREATE_OK);

		Assert.assertTrue(String.format("failed to write paper wallet to directory %s: expected message '%s ...', actual message: '%s'", tmpFilePath, Application.CREATE_OK, message), isOkMessage);
		updateTempFiles(message);

		// verify wallet file
		String jsonFile = okMessageToJsonFileName(message);
		args = new String [] { Application.SWITCH_DIRECTORY, tmpFilePath, Application.SWITCH_PASS_PHRASE, passPhrase, Application.SWITCH_WALLET, jsonFile, Application.SWITCH_VERIFY};
		app = new Application();
		message = app.run(args);
		isOkMessage = message.startsWith(Application.VERIFY_OK);

		Assert.assertTrue(String.format("failed to verify paper wallet %s: expected message '%s ...', actual message: '%s'", jsonFile, Application.VERIFY_OK, message), isOkMessage);
	}

	@Test
	public void verifyWalletFileMissing() {
		if(setupFailed) {
			return;
		}

		String jsonFile = String.format("%s%s%s", tmpFilePath, File.separator, "wallet_verify_missing_file.json");
		String badPassPhrase = WALLET_JSON_PASS_PHRASE;
		String [] args = new String [] { Application.SWITCH_PASS_PHRASE, badPassPhrase, Application.SWITCH_WALLET, jsonFile, Application.SWITCH_VERIFY};
		Application app = new Application();
		String message = app.run(args);
		boolean isErrorMessage = message.startsWith(Application.VERIFY_ERROR);
		
		Assert.assertTrue(String.format("failed to force verification error for inexistent file. expected message '%s ...', actual message: '%s'", Application.VERIFY_ERROR, message), isErrorMessage);
	}

	@Test
	public void verifyWalletBadPassPhrase() {
		if(setupFailed) {
			return;
		}
		
		String jsonFile = String.format("%s%s%s", tmpFilePath, File.separator, "wallet_verify_bad_pass_phrase.json");
		FileUtility.saveToFile(WALLET_JSON_OK, jsonFile);
		
		String badPassPhrase = WALLET_JSON_PASS_PHRASE + " bad";
		String [] args = new String [] { Application.SWITCH_PASS_PHRASE, badPassPhrase, Application.SWITCH_WALLET, jsonFile, Application.SWITCH_VERIFY};
		Application app = new Application();
		String message = app.run(args);
		boolean isErrorMessage = message.startsWith(Application.VERIFY_ERROR);
		
		Assert.assertTrue(String.format("failed to force pass phrase verification error. expected message '%s ...', actual message: '%s'", Application.VERIFY_ERROR, message), isErrorMessage);
		tmpFile.add(new File(jsonFile));
	}

	@Test
	public void verifyWalletEmptyFile() {
		if(setupFailed) {
			return;
		}
		
		String jsonFile = String.format("%s%s%s", tmpFilePath, File.separator, "wallet_verify_empty.json");
		FileUtility.saveToFile("", jsonFile);
		
		String badPassPhrase = WALLET_JSON_PASS_PHRASE;
		String [] args = new String [] { Application.SWITCH_PASS_PHRASE, badPassPhrase, Application.SWITCH_WALLET, jsonFile, Application.SWITCH_VERIFY};
		Application app = new Application();
		String message = app.run(args);
		boolean isErrorMessage = message.startsWith(Application.VERIFY_ERROR);
		
		Assert.assertTrue(String.format("failed to force empty file verification error. expected message '%s ...', actual message: '%s'", Application.VERIFY_ERROR, message), isErrorMessage);
		tmpFile.add(new File(jsonFile));
	}

	@Test
	public void verifyWalletCorruptFileTruncated() {
		if(setupFailed) {
			return;
		}
		
		String jsonFile = String.format("%s%s%s", tmpFilePath, File.separator, "wallet_truncated.json");
		FileUtility.saveToFile(WALLET_JSON_OK.substring(0, WALLET_JSON_OK.length() - 23), jsonFile);
		
		String badPassPhrase = WALLET_JSON_PASS_PHRASE;
		String [] args = new String [] { Application.SWITCH_PASS_PHRASE, badPassPhrase, Application.SWITCH_WALLET, jsonFile, Application.SWITCH_VERIFY};
		Application app = new Application();
		String message = app.run(args);
		boolean isErrorMessage = message.startsWith(Application.VERIFY_ERROR);
		
		Assert.assertTrue(String.format("failed to force truncated file verification error. expected message '%s ...', actual message: '%s'", Application.VERIFY_ERROR, message), isErrorMessage);
		tmpFile.add(new File(jsonFile));
	}

	@Test
	public void verifyWalletBadVersion() {
		if(setupFailed) {
			return;
		}
		
		String jsonFile = String.format("%s%s%s", tmpFilePath, File.separator, "wallet_bad_version.json");
		FileUtility.saveToFile(WALLET_JSON_CORRUPT_1, jsonFile);
		
		String badPassPhrase = WALLET_JSON_PASS_PHRASE;
		String [] args = new String [] { Application.SWITCH_PASS_PHRASE, badPassPhrase, Application.SWITCH_WALLET, jsonFile, Application.SWITCH_VERIFY};
		Application app = new Application();
		String message = app.run(args);
		boolean isErrorMessage = message.startsWith(Application.VERIFY_ERROR);
		
		Assert.assertTrue(String.format("failed to force bad version verification error. expected message '%s ...', actual message: '%s'", Application.VERIFY_ERROR, message), isErrorMessage);
		tmpFile.add(new File(jsonFile));
	}

	@Test
	public void verifyWalletMissingElement() {
		if(setupFailed) {
			return;
		}
		
		String jsonFile = String.format("%s%s%s", tmpFilePath, File.separator, "wallet_missing_elements.json");
		FileUtility.saveToFile(WALLET_JSON_CORRUPT_2, jsonFile);
		
		String badPassPhrase = WALLET_JSON_PASS_PHRASE;
		String [] args = new String [] { Application.SWITCH_PASS_PHRASE, badPassPhrase, Application.SWITCH_WALLET, jsonFile, Application.SWITCH_VERIFY};
		Application app = new Application();
		String message = app.run(args);
		boolean isErrorMessage = message.startsWith(Application.VERIFY_ERROR);
		
		Assert.assertTrue(String.format("failed to force missing elements verification error. expected message '%s ...', actual message: '%s'", Application.VERIFY_ERROR, message), isErrorMessage);
		tmpFile.add(new File(jsonFile));
	}

	private void updateTempFiles(String message) {
		if(message == null || !message.startsWith(Application.CREATE_OK)) {
			return;
		}

		File jsonFile = new File(okMessageToJsonFileName(message));
		File htmlFile = deriveFile(jsonFile, Application.EXT_HTML);
		File pngFile = deriveFile(jsonFile, Application.EXT_PNG);

		tmpFile.add(jsonFile);
		tmpFile.add(htmlFile);
		tmpFile.add(pngFile);
	}

	private String okMessageToJsonFileName(String message) {
		return message.substring(Application.CREATE_OK.length() + 1);
	}

	private File deriveFile(File file, String newExtension) {
		String baseName = getBaseName(file.getAbsolutePath());
		return new File(String.format("%s.%s", baseName, newExtension));
	}

	private String getBaseName(String jsonName) {
		return jsonName.substring(0, jsonName.lastIndexOf("."));
	}
}
