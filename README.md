# Paper Wallet Generator for Ethereum

## Application Description

Command line tool to create (offline) Ethereum paper wallets.

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
java -jar target/epwg-0.3.0-SNAPSHOT.jar -d C:\Users\mzi\AppData\Local\Temp -p 'test pass phrase'
```

This will lead to some information on the console

```
creating wallet ...
wallet file successfully created
wallet pass phrase: 'test pass phrase'
wallet file location: C:\Users\mzi\AppData\Local\Temp\UTC--2017-01-14T11-34-23.830000000Z--b86bab51c139f9662ccea6547a5e34e13d144bb0.json
writing additional output files ...
html wallet: C:\Users\mzi\AppData\Local\Temp\UTC--2017-01-14T11-34-23.830000000Z--b86bab51c139f9662ccea6547a5e34e13d144bb0.html
address qr code: C:\Users\mzi\AppData\Local\Temp\UTC--2017-01-14T11-34-23.830000000Z--b86bab51c139f9662ccea6547a5e34e13d144bb0.png
```

Three file are created by the tool as indicated in the output above
* The actual wallet file (UTC--2017-01-14T11-34-23.83... .json)
* The HTML file for printing (UTC--2017-01-14T11-34-23.83... .html)
* The image file with the QR code for the paper wallet address (UTC--2017-01-14T11-34-23.83... .png)

### Verifying a (Paper) Wallet

The tool also allows to verify a provided wallet file against a provided pass phrase.

```
java -jar target/epwg-0.3.0-SNAPSHOT.jar -p 'test pass phrase' -w  "C:\Users\mzi\AppData\Local\Temp\UTC--2017-01-14T11-34-23.830000000Z--b86bab51c139f9662ccea6547a5e34e13d144bb0.json" -v
```

This will lead to some information on the console

```
veriying wallet file ...
wallet file successfully verified
wallet file: C:\Users\mzi\AppData\Local\Temp\UTC--2017-01-14T11-34-23.830000000Z--b86bab51c139f9662ccea6547a5e34e13d144bb0.json
pass phrase: test pass phrase
```

### Creating an offline Transaction

The tool further allows to create an offline transaction for provided wallet details

```
java -jar target/epwg-0.3.0-SNAPSHOT.jar -p 'test pass phrase' -w  "C:\Users\mzi\AppData\Local\Temp\UTC--2017-01-14T11-34-23.830000000Z--b86bab51c139f9662ccea6547a5e34e13d144bb0.json" -t -t 0x025403ff4c543c660423543a9c5a3cc2a02e2f1f -a 0.0123
```

leading to the following output.

```
Target address: 0x025403ff4c543c660423543a9c5a3cc2a02e2f1f
Amount [Ether]: 0.0123
Nonce: 0
Gas price [Wei]: 20000000000
Gas limit [Wei]: 21000
curl -X POST --data '{"jsonrpc":"2.0","method":"eth_sendRawTransaction","params":["0xf86b808504a817c80082520894025403ff4c543c660423543a9c5a3cc2a02e2f1f872bb2c8eabcc000801ba08c5b25a10edb8e72518f4e6f51527df718d090f80cefcf024669340fe29cf78aa0124a95546dc897b6987c2b05efd2be7ed976318174a6bf9300d6f11c1d5d2da1"],"id":1}' -H "Content-Type: application/json" https://mainnet.infura.io/<your-infura-token>
```

The last line may be used to send the transaction to the Etherem network (using your infura token). 

## Dependencies

The project is maintained with the Eclipse IDE using Java 8. Building the project is done with Maven. 
For Ethereum the [web3j](https://web3j.github.io/web3j/) library is used, 
to create QR codes the [ZXing](https://github.com/zxing/zxing) library and
for command line parsing the [JCommander](https://github.com/cbeust/jcommander) library.

