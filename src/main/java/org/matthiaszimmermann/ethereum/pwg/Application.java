package org.matthiaszimmermann.ethereum.pwg;

import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;

import org.web3j.utils.Convert;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Application {

	public static final String COMMAND_NAME = "java -jar epwg.jar";
	public static final String SWITCH_DIRECTORY = "-d";
	public static final String SWITCH_PASS_PHRASE = "-p";
	public static final String SWITCH_WALLET = "-w";
	public static final String SWITCH_ADDRESS = "-t";
	public static final String SWITCH_AMOUNT = "-a";
	public static final String SWITCH_NONCE = "-n";
	public static final String SWITCH_VERIFY = "-v";
	public static final String SWITCH_SILENT = "-s";
	public static final String SWITCH_MNEMONICS = "-m";
	public static final String SWITCH_HELP = "-h";
	
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

	@Parameter(names = {SWITCH_WALLET, "--wallet-file"}, description = "wallet file location")
	private String walletFile = null;

	@Parameter(names = {SWITCH_ADDRESS, "--target-address"}, description = "target address for offline transaction(need to specify wallet file)")
	private String targetAddress = null;

	@Parameter(names = {SWITCH_NONCE, "--nonce"}, description = "nonce value for offline transaction")
	private Integer nonce = new Integer(0);

	@Parameter(names = {SWITCH_AMOUNT, "--amount"}, description = "amount [ethers] for offline transaction")
	private Double amount = new Double(0.01);

	@Parameter(names = {SWITCH_MNEMONICS, "--mnemonics"}, description = "use specified mnemonic word sequence")
	private String mnemonics = null;

	@Parameter(names = {SWITCH_VERIFY, "--verify"}, description = "verify the specified wallet file")
	private boolean verify = false;
	
	@Parameter(names = {SWITCH_SILENT, "--silent"}, description = "silent mode, suppress command line output")
	private boolean silent = false;

	@Parameter(names = {SWITCH_HELP, "--help"}, description = "show this help page", help = true)
	private boolean help;

	public static void main(String[] args) throws Exception {
		Application app = new Application();
		app.run(args);
	}

	public String run(String [] args) {
		parseCommandLine(args);

		if(walletFile != null) {
			if(targetAddress != null || verify) {
				if(verify) {
					return verifyWalletFile();
				}
				else {
					return createOfflineTx();
				}
			}
			else {
				System.err.println("command line error: for a specified wallet you need to specify -v or -t");
			}
		}
		
		return createWalletFile();
	}

	private String createOfflineTx() {
		readPassPhrase();
		
		try {
			PaperWallet pw = new PaperWallet(passPhrase, new File(walletFile));
			BigInteger gasPrice = PaperWallet.GAS_PRICE_DEFAULT;
			BigInteger gasLimit = PaperWallet.GAS_LIMIT_DEFAULT;
			BigInteger amountWei = Convert.toWei(String.valueOf(amount), Convert.Unit.ETHER).toBigInteger();

			log("Target address: " + targetAddress);
			log("Amount [Ether]: " + amount);
			log("Nonce: " + nonce);
			log("Gas price [Wei]: " + gasPrice);
			log("Gas limit [Wei]: " + gasLimit);
			
			String txData = pw.createOfflineTx(targetAddress, gasPrice, gasLimit, amountWei, BigInteger.valueOf(nonce));
			String curlCmd = String.format("curl -X POST --data '{\"jsonrpc\":\"2.0\",\"method\":\"eth_sendRawTransaction\",\"params\":[\"%s\"],\"id\":1}' -H \"Content-Type: application/json\" https://mainnet.infura.io/<infura-token>", txData);
			
			log(curlCmd);
			
			return curlCmd;
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public String verifyWalletFile() {
		readPassPhrase();

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

	private void readPassPhrase() {
		if(passPhrase == null) {
			Scanner scanner = new Scanner(System.in);

			//  prompt for the user's name
			System.out.print("wallet pass phrase: ");

			// get their input as a String
			passPhrase = scanner.next();
			scanner.close();
		}
	}

	public String createWalletFile() {
		PaperWallet pw = null;
		
		log("creating wallet ...");
		
		try {
			pw = this.createPaperWallet();
		} catch (Exception e) {
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

	private PaperWallet createPaperWallet() throws Exception {
		return new PaperWallet(passPhrase, targetDirectory, mnemonics);
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
