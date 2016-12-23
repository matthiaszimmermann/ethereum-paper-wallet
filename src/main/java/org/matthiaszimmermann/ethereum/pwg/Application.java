package org.matthiaszimmermann.ethereum.pwg;

public class Application {

	public static void main(String[] args) throws Exception  {
		
		// TODO add optional parameter (pass phrase)
		// TODO add optional parameter (target directory)
		// TODO test + write more tests
		// TODO add more docu
		
		PaperWallet pw = new PaperWallet(null, null);
		String html = WalletPageUtility.createHtml(pw);
		FileUtility.saveToFile(html, "testli.html");
	}
}
