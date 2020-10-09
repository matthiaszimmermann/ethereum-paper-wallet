package org.matthiaszimmermann.ethereum.pwg;

import java.util.Arrays;

public class Bip44Path {

	// path purpose field has fixed value 44' according to
	// https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki
	public static final int PURPOSE = 44;
	
	// hardened keys have index >= 0x80000000 according to
	// https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki
	public static final int HARDENED = 0x80000000;

	// registered coin types according to
	// https://github.com/satoshilabs/slips/blob/master/slip-0044.md
	public static final int BITCOIN = 0;
	public static final int BITCOIN_TESTNET = 1;
	public static final int ETHER = 60;
	public static final int IOTA = 4218;
	private static final int UNDEFINED = -1;
	
	private static final int [] SUPPORTED_COINS = {BITCOIN, BITCOIN_TESTNET, ETHER, IOTA};
	private static final String PATH_PATTERN = "m/44'/\\d+'/\\d+'/(\\d+)'?(/(\\d+)'?)?";
	
	private int purpose = PURPOSE | HARDENED;
	private int coinType = UNDEFINED;
	private int account = UNDEFINED;
	private int change = UNDEFINED;
	private int addressIndex = UNDEFINED;
	
	public Bip44Path(String path) {
		validatePathPattern(path);
		
		String [] pathToken = path.split("/");
		coinType = getCoinType(pathToken[2]);
		account = getAccount(pathToken[3]);
		change = getValueFromToken(pathToken[4]);
		
		if(pathToken.length == 6) {
			addressIndex = getValueFromToken(pathToken[5]);
		}
	}
	
	public int [] getPathArray() {
		if(addressIndex == UNDEFINED) {
			return new int [] {purpose, coinType, account, change};
		}
		
		return new int [] {purpose, coinType, account, change, addressIndex};
	}
	
	public String getPurpose() {
		return getStringRepresentation(purpose);
	}
	
	public String getCoinType() {
		return getStringRepresentation(coinType);
	}
	
	public String getAccount() {
		return getStringRepresentation(account);
	}
	
	public String getChange() {
		return getStringRepresentation(change);
	}
	
	public String getAddressIndex() {
		return getStringRepresentation(addressIndex);
	}
	
	private String getStringRepresentation(int value) {
		if(value == UNDEFINED) {
			return "";
		}
		
		if((value & HARDENED) == HARDENED) {
			return String.format("%d'", value - HARDENED);
		}
		
		return String.format("%d", value);
	}

	private int getValueFromToken(String pathToken) {
		if(pathToken.endsWith("'")) {
			pathToken = pathToken.substring(0, pathToken.length() - 1);			
			return Integer.parseInt(pathToken) | HARDENED;
		}
				
		return Integer.parseInt(pathToken) ;
	}

	private int getCoinType(String coinToken) {
		if(!coinToken.endsWith("'")) {
			throw new IllegalArgumentException("Coin type needs to be hardened according to BIP44");
		}
		
		int coinValue = Integer.parseInt(coinToken.substring(0, coinToken.length() - 1));
		
		if(Arrays.binarySearch(SUPPORTED_COINS, coinValue) < 0) {
			throw new IllegalArgumentException("Coin type specified is currently not supported");
		}
		
		return coinValue | HARDENED;
	}

	private int getAccount(String accountToken) {
		if(!accountToken.endsWith("'")) {
			throw new IllegalArgumentException("Account needs to be hardened according to BIP44");
		}
		
		int accountValue = Integer.parseInt(accountToken.substring(0, accountToken.length() - 1));
		
		return accountValue | HARDENED;
	}

	private void validatePathPattern(String path) {
		if(path == null || path.trim().length() == 0) {
			throw new IllegalArgumentException("Path is null or empty");
		}
		
		if(!path.matches(PATH_PATTERN)) {
			throw new IllegalArgumentException("Path does not match required pattern " + PATH_PATTERN);
		}
	}
}
