# Paper Wallet Generator for Etehereum

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
Use the following command to run the tool after building it.

```
java -jar target/epwg-0.1.0-SNAPSHOT.jar
```

## Dependencies

The project is maintained with the Eclipse IDE using Java 8. Building the project is done with Maven. 
For Ethereum the [web3j](https://web3j.github.io/web3j/) library is used, and [ZXing](https://github.com/zxing/zxing) to create QR codes.
