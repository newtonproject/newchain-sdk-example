NewChain SDK for java
==================================

## Get the Jars

Download address: [HERE](web3j-4.1.1.zip)

## Get Started

### Get a Web3j Instance

Get a `Web3j` instance with a `Web3jService` instance, which needs a URL as the parameter: 

```java
Web3j web3 = Web3j.build(new HttpService(rpcUrl));
```

#### RPC URL

TestNet URL:

```java
private final static String rpcUrl = "https://rpc1.newchain.newtonproject.org/";
```

### Get the chainId (net version)

Get chain ID with the `Web3j` instance:

```java
NetVersion netVersion = web3.netVersion().send();
String chainIDStr = netVersion.getNetVersion();
```

### Generate Keystore

Create a standard keystore:   

```java
String fileName = WalletUtils.generateNewWalletFile(
                "123qwe",
                new File("C:\\Files\\wallet"));
```

#### Parameters

* password (`String`): The password for the keystore.
* destinationDirectory (`File`): The destination directory for the keystore.

#### Return Values

Return a `String` value which is the name of the keystore. It`s in the following format:  

> UTC--2019-03-09T03-20-42.743000000Z--02d9bec4c13aecd197362adf92ed23b00a95d8ab.json  

### Load Keystore 

Load an existing keystore:

```java
Credentials credentials = WalletUtils.loadCredentials(
                "123qwe",
                "C:\\Files\\wallet\\UTC--2019-03-09T03-20-42.743000000Z--02d9bec4c13aecd197362adf92ed23b00a95d8ab.json");
```

#### Parameters

* password(`String`): The password for the keystore.  
* source(`String`): The path of the keystore.

#### Return Values

Return an `Credentials` instance with keystore information.

### Get original Address and Convert it into NEW Address

Get the address of the keystore with the `Credentials` instance:

```java
String fromAddress = credentials.getAddress();
```

Convert the original format address into NEW format:

```java
String demo = AddressUtil.originalAddress2NewAddress(fromAddress, chainIDStr);
```

#### Parameters

* originalAddress(`String`): The address in original format.
* chainID(`String`): The chainID you get above.

#### Return Values

Return a `String` value which is the NEW format address.

#### See Also

* [Convert from New format address to original format address.](#convert-new-address-into-original-address)

### Get Balance

Get balance of the keystore with the `Web3j` instance:

```java
EthGetBalance balance = web3.ethGetBalance(fromAddress, DefaultBlockParameterName.LATEST).send();
BigInteger b = balance.getBalance();
```

#### Parameters

* s(`String`): The address.
* defaultBlockParameter(`DefaultBlockParameter`): Integer block number, or the string "latest", "earliest" or "pending". You should put into `DefaultBlockParameterName.LATEST`("latest").

#### Return Values

Returns the balance of the account of given address.

#### See Also

* [The default block parameter.](https://github.com/ethereum/wiki/wiki/JSON-RPC#the-default-block-parameter)

### Get nonce

Get the transaction count(nonce) of the account:

```java
EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
BigInteger nonce = ethGetTransactionCount.getTransactionCount();
```

#### Parameters

* s(`String`): The address.
* defaultBlockParameter(`DefaultBlockParameter`): Integer block number, or the string "latest", "earliest" or "pending". You should put into `DefaultBlockParameterName.LATEST`("latest").

#### Return Values 

Return the nonce of the account of given address.

### Convert NEW Address into original Address

```java
String newAddress = "NEW17zJoq3eHwv3x7cJNgdmG73Limvv7TwQurB4";
String toAddress = AddressUtil.newAddress2originalAddress(newAddress);

//getChainID() return the chain ID in hex string
String addressCahinID = AddressUtil.getChainID(newAddress);

Integer inputChainID = Integer.parseInt(addressCahinID,16);
System.out.println("input ID : " + inputChainID);
Integer chainID = Integer.parseInt(chainIDStr);
System.out.println("chain ID : " + chainID);

if(!inputChainID.equals(chainID)){
    System.out.println("Wrong address. Please check the address.");
    return;
}else{
    System.out.println("Right address.");
}
```

#### Parameters

* newAdderss(`String`): The address in NEW format.

#### Return Values

Return the original address.

#### See Also

* [NewChain Address Identifier (NAI)](https://github.com/newtonproject/newchain-sdk-example/blob/master/address_standards.md)

### Get gasPrice

```java
EthGasPrice ethGasPrice = web3.ethGasPrice().send();
BigInteger gasPrice = ethGasPrice.getGasPrice();
```

#### Parameters

None

#### Return Values

Return the gasPrice.

### Get GasLimit

Get gas limit with `Web3j.ethEstimateGas()` function.This won\`t sent a transaction on block chain.

```java
Transaction tx = Transaction.createEtherTransaction(
            fromAddress, nonce, gasPrice, null, toAddress, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());
EthEstimateGas ethEstimateGas = web3.ethEstimateGas(tx).send();
BigInteger gasLimit = ethEstimateGas.getAmountUsed();
```

#### Parameters

The `Transaction.createEtherTransaction()` function has 6 parameters:

* from(`String`): From address.
* nonce(`BigInteger`): The nonce you get from chain above.
* gasPrice(`BigInteger`): The gasPrice you get from chain above.
* gasLimit(`BigInteger`): The value you ask for, put null in this position.
* to(`String`): To address.
* value(`BigInteger`): The value you want to transfer.

#### Return Values

Return the gasLimit.

#### Note

The value passed into `Transaction.createEtherTransaction()` is in format of `WEI`(`ISSAC`).  
  
You can convert `ETHER` into `WEI`:  

```java
Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER);  // 10 ETHER / NEW
```

Also you can convert `WEI` into `ETHER`:

```java
Convert.fromWei(BigDecimal.valueOf(10), Convert.Unit.ETHER);  // 10 WEI / ISSAC
```

#### See Also

* [JSON_RPC: eth_estimategas](https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_estimategas)

### Create Transaction

```java
RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, toAddress, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());
```

#### Parameters

* nonce(`BigInteger`): The nonce.
* gasPrice(`BigInteger`): The gasPrice.
* gasLimit(`BigInteger`): The gasLimit.
* to(`String`): To address.
* value(`BigInteger`): The value.

#### Return Values

Return the `RawTransaction` instance.

### Sign and Send Transaction

```java
byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Integer.parseInt(chainIDStr), credentials);
String hexValue = Numeric.toHexString(signedMessage);
EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
```

#### Paremeters

The `TransactionEncoder.signMessage()` function require 3 parameters:

* rawTransaction(`RawTransaction`): The instance of `RawTransaction`.
* chainId(`int`): The chainId.
* credentials(`Credentials`): The instance of `Credentials`.

#### Return Values

Return the signed message in `byte[]`.

### Get Error

```java
Response.Error error = ethSendTransaction.getError();
```

#### Parameters

None

#### Return Values

Return an instance of `Response.Error`. If there are no errors, the instance will be `null`.  

### Get Hash Value

Get transaction hash.

```java
String hash = ethSendTransaction.getTransactionHash();
```

#### Parameters

None

#### Return Values

Return the `String` value of transaction hash.
