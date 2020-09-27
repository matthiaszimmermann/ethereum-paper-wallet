package org.matthiaszimmermann.ethereum.pwg;

public class WalletPageUtility extends HtmlUtility {
	
	// TODO verify version with the one in the pom.xml
	public static final String VERSION = "0.3.0-SNAPSHOT";
	public static final String REPOSITORY = "https://github.com/matthiaszimmermann/ethereum-paper-wallet";

	public static final String TITLE = "Ethereum Paper Wallet";
	public static final String ETHEREUM_LOGO = "/ethereum_logo.png"; 
	
	public static final String CSS_CLEARFIX = "clearfix";
	public static final String CSS_ADDRESS_ROW = "address-row";
	public static final String CSS_COLUMN = "column";
	public static final String CSS_FILL = "fill-right";
	public static final String CSS_NOTES = "notes";
	public static final String CSS_CONTENT = "content";
	public static final String CSS_CAPTION = "caption";
	public static final String CSS_FOOTER = "footer-content";
	public static final String CSS_IMG_ADDRESS = "img-address";
	public static final String CSS_IMG_WALLET = "img-wallet";
	
	public static final String [] CSS_STYLES = {
			"html * { font-family:Verdana, sans-serif; }",
			String.format(".%s::after { content: \"\"; clear:both; display:table; }", CSS_CLEARFIX),
			String.format(".%s { background-color:#eef; }", CSS_ADDRESS_ROW),
			"@media screen {",
			String.format(".%s { float:left; padding: 15px; }", CSS_COLUMN), 
			String.format(".%s { overflow:auto; padding:15px; }", CSS_FILL),
			String.format(".%s { height:256px; background-color:#fff; }", CSS_NOTES),
			String.format(".%s { padding:15px; background-color:#efefef; font-family:monospace; }", CSS_CONTENT),
			String.format(".%s { margin-top:6px; font-size:smaller;}", CSS_CAPTION),
			String.format(".%s { font-size:small; }", CSS_FOOTER),
			String.format(".%s { display:block; height:256px; }", CSS_IMG_ADDRESS),
			String.format(".%s { display:block; height:400px }", CSS_IMG_WALLET),
			"}",
			"@media print {",
			String.format(".%s { float:left; padding:8pt; }", CSS_COLUMN), 
			String.format(".%s { overflow:auto; padding:8pt; }", CSS_FILL),
			String.format(".%s { height:100pt; border-style:solid; border-width:1pt; }", CSS_NOTES),
			String.format(".%s { background-color:#efefef; font-family:monospace; font-size:6pt}", CSS_CONTENT),
			String.format(".%s { margin-top:2pt; font-size:smaller;}", CSS_CAPTION),
			String.format(".%s { font-size:6pt; }", CSS_FOOTER),
			String.format(".%s { display:block; height:100pt; }", CSS_IMG_ADDRESS),
			String.format(".%s { display:block; height:180pt }", CSS_IMG_WALLET),
			"}",
	};

	public static String createHtml(PaperWallet wallet) {
		StringBuffer html = new StringBuffer();
		// header
		HtmlUtility.addOpenElements(html, HtmlUtility.HTML, HtmlUtility.HEAD);
		HtmlUtility.addTitle(html, TITLE);
		HtmlUtility.addStyles(html, CSS_STYLES);
		HtmlUtility.addCloseElements(html, HtmlUtility.HEAD);

		// body
		HtmlUtility.addOpenElements(html, HtmlUtility.BODY);
		HtmlUtility.addHeader2(html, TITLE);
		
		// add 1st row
		HtmlUtility.addOpenDiv(html, CSS_CLEARFIX, CSS_ADDRESS_ROW);

		// ethereum logo
		HtmlUtility.addOpenDiv(html, CSS_COLUMN);
		byte [] logo = FileUtility.getResourceAsBytes(ETHEREUM_LOGO);
		HtmlUtility.addEncodedImage(html, logo, 256, CSS_IMG_ADDRESS);
		HtmlUtility.addCloseDiv(html);
		
		// account address
		HtmlUtility.addOpenDiv(html, CSS_COLUMN);
		byte [] addressQrCode = QrCodeUtility.contentToPngBytes(wallet.getAddress(), 256);
		HtmlUtility.addEncodedImage(html, addressQrCode, 256, CSS_IMG_ADDRESS);
		HtmlUtility.addParagraph(html, "QR Code Address", CSS_CAPTION);
		HtmlUtility.addCloseDiv(html);
		
		// notes
		HtmlUtility.addOpenDiv(html, CSS_FILL);
		HtmlUtility.addOpenDiv(html, CSS_NOTES);
		HtmlUtility.addCloseDiv(html);
		HtmlUtility.addParagraph(html, "Notes", CSS_CAPTION);
		HtmlUtility.addCloseDiv(html);

		HtmlUtility.addCloseDiv(html);
		
		// add 2nd row
		HtmlUtility.addOpenDiv(html, CSS_CLEARFIX);

		// qr code for wallet file
		String walletFileContent = getWalletFileContent(wallet);
		HtmlUtility.addOpenDiv(html, CSS_COLUMN);
		byte [] walletQrCode = QrCodeUtility.contentToPngBytes(walletFileContent, 400);
		HtmlUtility.addEncodedImage(html, walletQrCode, 400, CSS_IMG_WALLET);
		HtmlUtility.addParagraph(html, "QR Code Wallet File", CSS_CAPTION);
		HtmlUtility.addCloseDiv(html);
		
		// address, pass phrase, wallet file, file name
		HtmlUtility.addOpenDiv(html, CSS_FILL);
		HtmlUtility.addOpenDiv(html, CSS_CONTENT);
		HtmlUtility.addContent(html, wallet.getAddress());
		HtmlUtility.addCloseDiv(html);
		HtmlUtility.addParagraph(html, "Address", CSS_CAPTION);
		
		HtmlUtility.addOpenDiv(html, CSS_CONTENT);
		HtmlUtility.addContent(html, wallet.getPassPhrase());
		HtmlUtility.addCloseDiv(html);
		HtmlUtility.addParagraph(html, "Pass Phrase", CSS_CAPTION);
		
		HtmlUtility.addOpenDiv(html, CSS_CONTENT);
		HtmlUtility.addContent(html, walletFileContent.replace(",\"", ", \""));
		HtmlUtility.addCloseDiv(html);
		HtmlUtility.addParagraph(html, "File Content", CSS_CAPTION);
		
		HtmlUtility.addOpenDiv(html, CSS_CONTENT);
		HtmlUtility.addContent(html, wallet.getFileName());
		HtmlUtility.addCloseDiv(html);
		HtmlUtility.addParagraph(html, "File Name", CSS_CAPTION);
		
		HtmlUtility.addCloseDiv(html);		
		HtmlUtility.addCloseDiv(html);		
		
		// add footer content
		String footer = String.format("Page created with EPW Generator [%s] V %s", REPOSITORY, VERSION);
		HtmlUtility.addOpenFooter(html, CSS_FOOTER);
		HtmlUtility.addContent(html, footer);
		HtmlUtility.addCloseFooter(html);
				
		HtmlUtility.addCloseElements(html, HtmlUtility.BODY, HtmlUtility.HTML);

		return html.toString();
	}

	private static String getWalletFileContent(PaperWallet wallet) {
		try {
			return wallet.getFileContent();
		} 
		catch (Exception e) {
			throw new RuntimeException("Failed to load content from wallet file", e);
		}
	}

	public static String instertMnemonic(PaperWalletBIP44 pw, String html) {
		return insertAfterWord(html, createMnemonic(pw), "File Name</p>");
	}

	public static StringBuffer createMnemonic(PaperWalletBIP44 pw) {
		StringBuffer mnemonicBuffer = new StringBuffer();
		HtmlUtility.addOpenDiv(mnemonicBuffer, CSS_CONTENT);
		HtmlUtility.addContent(mnemonicBuffer, pw.getMnemonic());
		HtmlUtility.addCloseDiv(mnemonicBuffer);
		HtmlUtility.addParagraph(mnemonicBuffer, "Mnemonic", CSS_CAPTION);
		return mnemonicBuffer;
	}

	public static String insertAfterWord(String from, StringBuffer toInsert, String afterWord) {
		return new StringBuffer(from).insert(from.lastIndexOf(afterWord) + afterWord.length(), toInsert).toString();
	}

}
