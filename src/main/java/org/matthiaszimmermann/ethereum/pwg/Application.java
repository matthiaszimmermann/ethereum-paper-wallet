package org.matthiaszimmermann.ethereum.pwg;

import java.io.File;
import java.util.Scanner;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Application {

	public static final String COMMAND_NAME = "java -jar epgw.jar";
	public static final String SWITCH_DIRECTORY = "-d";
	public static final String SWITCH_PASS_PHRASE = "-p";
	public static final String SWITCH_VERIFY = "-v";
	
	public static final String CREATE_OK = "WALLET CREATION OK";
	public static final String CRATE_ERROR = "WALLET CREATION ERROR";
	
	public static final String VERIFY_OK = "WALLET VERIFICATION OK";
	public static final String VERIFY_ERROR = "WALLET VERIFICATION ERROR";
	
	public static final String EXT_HTML = "html";
	public static final String EXT_PNG = "png";

	@Parameter(names = {SWITCH_DIRECTORY, "--target-directory"}, description = "target directory for wallet file etc.")
	private String targetDirectory = PaperWallet.getPathToFileDefault();

	@Parameter(names = {SWITCH_PASS_PHRASE, "--pass-phrase"}, description = "pass phrase for the wallet file")
	private String passPhrase;

	@Parameter(names = {SWITCH_VERIFY, "--verify-wallet-file"}, description = "verify the specified wallet file")
	private String walletFile = null;

	@Parameter(names = {"-s", "--silent"}, description = "silent mode, suppress command line output")
	private boolean silent = false;

	@Parameter(names = {"-h", "--help"}, help = true)
	private boolean help;

	public static void main(String[] args) throws Exception {
		Application app = new Application();
		app.run(args);
	}

	public String run(String [] args) {
		parseCommandLine(args);

		if(walletFile != null) {
			return verifyWalletFile();
		}
		else {
			return createWalletFile();
		}
	}

	public String verifyWalletFile() {
		if(passPhrase == null) {
			Scanner scanner = new Scanner(System.in);

			//  prompt for the user's name
			System.out.print("wallet pass phrase: ");

			// get their input as a String
			passPhrase = scanner.next();
			scanner.close();
		}

		log("veriying wallet file ...");
		String statusMessage = PaperWallet.checkWalletFileStatus(new File(walletFile), passPhrase);

		if(statusMessage.startsWith(PaperWallet.WALLET_OK)) {
			log("wallet file successfully verified");
			log("wallet file: " + walletFile);
			log("pass phrase: " + passPhrase);
			
			return VERIFY_OK;
		}
		else {
			log("verification failed: " + statusMessage);
			log("wallet file: " + walletFile);
			log("pass phrase: " + passPhrase);
			
			return String.format("%s %s", VERIFY_ERROR, statusMessage);
		}
	}

	public String createWalletFile() {
		PaperWallet pw = null;
		
		log("creating wallet ...");
		
		try {
			pw = new PaperWallet(passPhrase, targetDirectory);
		}
		catch(Exception e) {
			return String.format("%s %s", CRATE_ERROR, e.getLocalizedMessage());
		}
		
		log("wallet file successfully created");
		log(String.format("wallet pass phrase: '%s'", pw.getPassPhrase()));
		log(String.format("wallet file location: %s", pw.getFile().getAbsolutePath()));

		String html = WalletPageUtility.createHtml(pw);
		byte [] qrCode = QrCodeUtility.contentToPngBytes(pw.getAddress(), 256);

		String path = pw.getPathToFile();
		String baseName = pw.getBaseName();
		String htmlFile = String.format("%s%s%s.%s", path, File.separator, baseName, EXT_HTML);
		String pngFile = String.format("%s%s%s.%s", path, File.separator, baseName, EXT_PNG);

		log("writing additional output files ...");
		FileUtility.saveToFile(html, htmlFile);
		FileUtility.saveToFile(qrCode, pngFile);
		log(String.format("html wallet: %s", htmlFile));
		log(String.format("address qr code: %s", pngFile));
		
		return String.format("%s %s", CREATE_OK, pw.getFile().getAbsolutePath());
	}

	private void parseCommandLine(String [] args) {
		JCommander cmd = new JCommander(this, args);
		cmd.setProgramName(COMMAND_NAME);

		if(help) {
			cmd.usage();
			System.exit(0);
		}
	}

	private void log(String message) {
		if(!silent) {
			System.out.println(message);
		}
	}
}
