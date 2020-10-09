package org.matthiaszimmermann.ethereum.pwg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class Bip44PathTest {

	// https://github.com/web3j/web3j/blob/35c6d46332089bd9921c710889fc175410110d50/core/src/main/java/org/web3j/crypto/Bip44WalletUtils.java#L67
	public static final String WEB3J_ETH_PATH_2 = "m/44'/60'/1'/0";
	public static final String WEB3J_ETH_PATH_3 = "m/44'/60'/0'/2'";

	// invalid stuff
	public static final String UNSUPPORTET_COIN_PATH = "m/44'/13'/0'/0/0'";

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test
	public void meherettExampleTest() {
		Bip44Path bip44Path = new Bip44Path(Bip44PathValues.ethMeherett());

		assertEquals("44'", bip44Path.getPurpose());
		assertEquals("60'", bip44Path.getCoinType());
		assertEquals("0'", bip44Path.getAccount());
		assertEquals("0", bip44Path.getChange());
		assertEquals("0'", bip44Path.getAddressIndex());
	}

	@Test
	public void myCryptoExampleTest() {
		Bip44Path bip44Path = new Bip44Path(Bip44PathValues.ethMyCrypto());

		assertEquals("44'", bip44Path.getPurpose());
		assertEquals("60'", bip44Path.getCoinType());
		assertEquals("0'", bip44Path.getAccount());
		assertEquals("0", bip44Path.getChange());
		assertEquals("0", bip44Path.getAddressIndex());
	}

	@Test
	public void web3jExampleTest() {
		Bip44Path bip44Path = new Bip44Path(Bip44PathValues.ethWeb3j());

		assertEquals("44'", bip44Path.getPurpose());
		assertEquals("60'", bip44Path.getCoinType());
		assertEquals("0'", bip44Path.getAccount());
		assertEquals("0", bip44Path.getChange());
		assertEquals("", bip44Path.getAddressIndex());
	}

	@Test
	public void web3jExample2Test() {
		Bip44Path bip44Path = new Bip44Path(WEB3J_ETH_PATH_2);

		assertEquals("44'", bip44Path.getPurpose());
		assertEquals("60'", bip44Path.getCoinType());
		assertEquals("1'", bip44Path.getAccount());
		assertEquals("0", bip44Path.getChange());
		assertEquals("", bip44Path.getAddressIndex());
	}

	@Test
	public void web3jExample3Test() {
		Bip44Path bip44Path = new Bip44Path(WEB3J_ETH_PATH_3);

		assertEquals("44'", bip44Path.getPurpose());
		assertEquals("60'", bip44Path.getCoinType());
		assertEquals("0'", bip44Path.getAccount());
		assertEquals("2'", bip44Path.getChange());
		assertEquals("", bip44Path.getAddressIndex());
	}

	@Test
	public void tezorExample1Test() {
		Bip44Path bip44Path = new Bip44Path(Bip44PathValues.ethTrezor());

		assertEquals("44'", bip44Path.getPurpose());
		assertEquals("60'", bip44Path.getCoinType());
		assertEquals("0'", bip44Path.getAccount());
		assertEquals("0", bip44Path.getChange());
		assertEquals("0", bip44Path.getAddressIndex());
	}

	@Test
	public void tezorExample2Test() {
		Bip44Path bip44Path = new Bip44Path(Bip44PathValues.ethTrezor(13));

		assertEquals("44'", bip44Path.getPurpose());
		assertEquals("60'", bip44Path.getCoinType());
		assertEquals("0'", bip44Path.getAccount());
		assertEquals("0", bip44Path.getChange());
		assertEquals("13", bip44Path.getAddressIndex());
	}

	@Test
	public void invalidPathHelloWorld() {
		try {
			new Bip44Path("hello world");
		}
		catch (Throwable t) {
			System.out.println(t);
			assertTrue(t instanceof IllegalArgumentException);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidPathCapitalM() {
		new Bip44Path(Bip44PathValues.ethMeherett().toUpperCase());
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidPathTruncated() {
		new Bip44Path(Bip44PathValues.ethMeherett().substring(0,11));
	}

	@Test(expected = IllegalArgumentException.class)
	public void unsupportedCoinTruncated() {
		new Bip44Path(UNSUPPORTET_COIN_PATH);
	}
}
