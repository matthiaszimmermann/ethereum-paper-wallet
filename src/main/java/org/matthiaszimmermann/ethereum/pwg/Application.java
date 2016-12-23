package org.matthiaszimmermann.ethereum.pwg;

public class Application {

	public static void main(String[] args) throws Exception  {
		
		// TODO add optional parameter (pass phrase)
		// TODO add optional parameter (target directory)
		
		PaperWallet pw = new PaperWallet(null, null);
		String html = WalletPageUtility.createHtml(pw);
		FileUtility.saveToFile(html, "testli.html");
		
		// TODO create github repo
		// TODO test + write more tests
		// TODO add docu
		// TODO add tweet
		
	}
}
