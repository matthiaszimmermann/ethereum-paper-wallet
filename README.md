# Paper Wallet Generator for Ethereum

## Application Description

Command line tool to create (offline) Ethereum paper wallets. 
For the mnemonic sentences the BIP39 specification is used. 
For the wallet address path relies on the BIP44 specification.

## Demo Output

The output of the tool is a HTML page that can be viewed in any browser. 
An example output is provided below.
![HTML Page](/screenshots/paper_wallet_html.png)

As we want to create paper wallets, the CSS is prepared make the HTML printable.

![Printed Wallet](/screenshots/paper_wallet_printed.png)

## Run the Application

After cloning this repo build the command line tool using Maven.

```
mvn clean package
```

The result of the Maven build is an executable JAR file.

### Creating a Paper Wallet
 
Use the following command to create a paper wallet.

```
java -jar target/epwg-0.4.0-SNAPSHOT.jar -d /Users/matthiaszimmermann/tmp -p 'password'
```

This will lead to some information on the console

```
creating wallet ...
wallet file successfully created
wallet pass phrase: 'password'
wallet file location: /Users/matthiaszimmermann/tmp/UTC--2020-10-08T07-42-39.594331000Z--9a1d5dd27b3b9ded07a25e4dbf0a9c539a5edd27.json
writing additional output files ...
html wallet: /Users/matthiaszimmermann/tmp/UTC--2020-10-08T07-42-39.594331000Z--9a1d5dd27b3b9ded07a25e4dbf0a9c539a5edd27.html
address qr code: /Users/matthiaszimmermann/tmp/UTC--2020-10-08T07-42-39.594331000Z--9a1d5dd27b3b9ded07a25e4dbf0a9c539a5edd27.png
```

Three file are created by the tool as indicated in the output above
* The actual wallet file (UTC--2020-10-08T07-42-39... .json)
* The HTML file for printing (UTC--2020-10-08T07-42-39... .html)
* The image file with the QR code for the paper wallet address (UTC--2020-10-08T07-42-39... .png)

### Verifying a (Paper) Wallet

The tool also allows to verify a provided wallet file against a provided pass phrase.

```
java -jar target/epwg-0.4.0-SNAPSHOT.jar -p 'password' -w /Users/matthiaszimmermann/tmp/UTC--2020-10-08T07-42-39.594331000Z--9a1d5dd27b3b9ded07a25e4dbf0a9c539a5edd27.json -v
```

This will lead to some information on the console

```
veriying wallet file ...
wallet file successfully verified
wallet file: /Users/matthiaszimmermann/tmp/UTC--2020-10-08T07-42-39.594331000Z--9a1d5dd27b3b9ded07a25e4dbf0a9c539a5edd27.json
pass phrase: password
```

### Creating a Paper Wallet with a given Mnemonic
 
Use the following command to create a paper wallet for a specified menmonic "kite scan embark dismiss text syrup salon butter cross rude hammer course".

```
java -jar target/epwg-0.4.0-SNAPSHOT.jar -d /Users/matthiaszimmermann/tmp -p 'password' -m 'kite scan embark dismiss text syrup salon butter cross rude hammer course'
```

This will lead to some information on the console

```
creating wallet ...
wallet file successfully created
wallet pass phrase: 'password'
wallet file location: /Users/matthiaszimmermann/tmp/UTC--2020-10-09T10-07-55.955089000Z--03c4b5778a3ded3957890cfc9251aa1d1a521916.json
writing additional output files ...
html wallet: /Users/matthiaszimmermann/tmp/UTC--2020-10-09T10-07-55.955089000Z--03c4b5778a3ded3957890cfc9251aa1d1a521916.html
address qr code: /Users/matthiaszimmermann/tmp/UTC--2020-10-09T10-07-55.955089000Z--03c4b5778a3ded3957890cfc9251aa1d1a521916.png
```


### Creating an offline Transaction

The tool further allows to create an offline transaction for provided wallet details

```
java -jar target/epwg-0.4.0-SNAPSHOT.jar -p 'password' -w /Users/matthiaszimmermann/tmp/UTC--2020-10-08T07-42-39.594331000Z--9a1d5dd27b3b9ded07a25e4dbf0a9c539a5edd27.json -t 0x025403ff4c543c660423543a9c5a3cc2a02e2f1f -a 0.0123
```

leading to the following output.

```
Target address: 0x025403ff4c543c660423543a9c5a3cc2a02e2f1f
Amount [Ether]: 0.0123
Nonce: 0
Gas price [Wei]: 20000000000
Gas limit [Wei]: 21000
curl -X POST --data '{"jsonrpc":"2.0","method":"eth_sendRawTransaction","params":["0xf86b808504a817c80082520894025403ff4c543c660423543a9c5a3cc2a02e2f1f872bb2c8eabcc000801ba0374026a8b52870008143f09beae2648da1b891ebf71260f1c0d450854ce087a2a07929efec1ea48ef78afb731af5a50273a8a94821c59638d67b9d1dad6c27038e"],"id":1}' -H "Content-Type: application/json" https://mainnet.infura.io/<infura-token>
```

The last line may be used to send the transaction to the Etherem network (using your infura token). 

## Dependencies

The project is maintained with the Eclipse IDE using Java 8. Building the project is done with Maven. 
For Ethereum the [web3j](https://web3j.github.io/web3j/) library is used, 
to create QR codes the [ZXing](https://github.com/zxing/zxing) library and
for command line parsing the [JCommander](https://github.com/cbeust/jcommander) library.

