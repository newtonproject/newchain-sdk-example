NewChain SDK for java
==================================

# How to Get the Jars
Download address：https://gitlab.newtonproject.org/alexcg/master/blob/patch-5-modify/examples/java/web3j-4.1.1.zip
Latest version：4.1.1  
# How to Start
## Get a Web3j Instance
Get a `Web3j` instance with a `Web3jService` instance, which needs an url as the parameter: 

```
Web3j web3 = Web3j.build(new HttpService(rpcUrl));
```

### RPC URL

```
private final static String rpcUrl = "https://rpc1.newchain.newtonproject.org/";
```

## Get the chainId(net version)

Get chain id with the `Web3j` instance:

```
NetVersion netVersion = web3.netVersion().send();
String clientVersion = netVersion.getNetVersion();
```

## Generate Keystore

Create a standard keystore:   

```
String fileName = WalletUtils.generateNewWalletFile(
                "123qwe",
                new File("C:\\Files\\wallet"));
```

Create a light keystore:

```
String fileName = WalletUtils.generateNewWalletFile(
                "123qwe",
                new File("C:\\Files\\wallet")
                false);
```

### Parameters

* password(`String`) : The password for the keystore.
* destinationDirectory(`File`) : The destination directory for the keystore.
* useFullScrypt(`boolean`) : If you want to generate a standard keystore. Default value is `true`.  

### Return Values

Return a `String` value which is the name of the keystore. It`s in the format like this:  

> UTC--2019-03-09T03-20-42.743000000Z--02d9bec4c13aecd197362adf92ed23b00a95d8ab.json  

## Load Keystore 

Load an existed keystore:

```
Credentials credentials = WalletUtils.loadCredentials(
                "123qwe",
                "C:\\Files\\wallet\\UTC--2019-03-09T03-20-42.743000000Z--02d9bec4c13aecd197362adf92ed23b00a95d8ab.json");
```

### Parameters

* password(`String`) : The password for the keystore.  
* source(`String`) : The path of the keystore.
* source(`File`) : The keystore instance.

### Return Values

Return an `Credentials` instance with keystore information.

## Get eth Address and Transfer to NEW Address

Get the address of the keystore with the `Credentials` instance:

```
String fromAddress = credentials.getAddress();
```

Transfer the eth format address into NEW format:

```
String demo = AddressUtil.ethAddress2NewAddress(fromAddress, clientVersion);
```

### Parameters

* ethAddress(`String`) : The eth address you want to transfer.
* chainId(`String`) : The chainId (net version) you get above.

### Return Values

Return a `String` value which is the NEW format address.

### See Also

* [Transfer from New format address to eth format address.](https://gitlab.newtonproject.org/lixuan/web3j-example#transfer-new-address-to-eth-address)

## Get Balance

Get balance of the keystore with the `Web3j` instance:

```
EthGetBalance balance = web3.ethGetBalance(fromAddress, DefaultBlockParameterName.LATEST).send();
BigInteger b = balance.getBalance();
```

### Parameters

* s(`String`) : The address.
* defaultBlockParameter(`DefaultBlockParameter`) : Integer block number, or the string "latest", "earliest" or "pending". You should put into `DefaultBlockParameterName.LATEST`("latest").

### Return Values

Returns the balance of the account of given address.

### See Also

* [The default block parameter.](https://github.com/ethereum/wiki/wiki/JSON-RPC#the-default-block-parameter)

## Get nonce

Get the transaction count(nonce) of the account:

```
EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
BigInteger nonce = ethGetTransactionCount.getTransactionCount();
```

### Parameters

* s(`String`) : The address.
* defaultBlockParameter(`DefaultBlockParameter`) : Integer block number, or the string "latest", "earliest" or "pending". You should put into `DefaultBlockParameterName.LATEST`("latest").

### Return Values 

Return the nonce of the account of given address.

## Transfer NEW Address to eth Address

```
String newAddress = "NEW17zJoq3eHwv3x7cJNgdmG73Limvv7TwQurB4";
String toAddress = AddressUtil.newAddress2ethAddress(newAddress);
```

### Parameters

* newAdderss(`String`) : The NEW address you want to transfer.

### Return Values

Return the eth address.

### See Also

* [Transfer from eth format address to NEW format address.](https://gitlab.newtonproject.org/lixuan/web3j-example#get-eth-address-and-transfer-to-new-address)

## Get gasPrice

```
EthGasPrice ethGasPrice = web3.ethGasPrice().send();
BigInteger gasPrice = ethGasPrice.getGasPrice();
```

### Parameters

No parameters.

### Return Values

Return the gasPrice.

## Get GasLimit

Get gas limit with `Web3J.ethEstimateGas()` function.This won\`t sent a transaction on block chain.

```
Transaction tx = Transaction.createEtherTransaction(
            fromAddress, nonce, gasPrice, null, toAddress, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());
EthEstimateGas ethEstimateGas = web3.ethEstimateGas(tx).send();
BigInteger gasLimit = ethEstimateGas.getAmountUsed();
```

### Parameters

The `Transaction.createEtherTransaction()` function has 6 parameters:

* from(`String`) : From address.
* nonce(`BigInteger`) : The nonce you get from chain above.
* gasPrice(`BigInteger`) : The gasPrice you get from chain above.
* gasLimit(`BigInteger`) : The value you ask for, put null in this position.
* to(`String`) : To address.
* value(`BigInteger`) : The value you want to transfer.

### Return Values

Return the gasLimit.

### Note

The value passed into `Transaction.createEtherTransaction()` is in format of `WEI`(`ISSAC`).  
  
You can transfer `ETHER` into `WEI`:  

```
Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER);  // 10 ETHER / NEW
```

Also you can transfer `WEI` into `ETHER`:

```
Convert.fromWei(BigDecimal.valueOf(10), Convert.Unit.ETHER);  // 10 WEI / ISSAC
```

### See Also

* [JSON_RPC : eth_estimategas](https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_estimategas)

## Create Transaction

```
RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, toAddress, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());
```

### Parameters

* nonce(`BigInteger`) : The nonce.
* gasPrice(`BigInteger`) : The gasPrice.
* gasLimit(`BigInteger`) : The gasLimit.
* to(`String`) : To address.
* value(`BigInteger`) : The value.

### Return Values

Return the `RawTransaction` instance.

## Sign and Send Transaction

```
byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Integer.parseInt(clientVersion), credentials);
String hexValue = Numeric.toHexString(signedMessage);
EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
```

### Paremeters

The `TransactionEncoder.signMessage()` function require 3 parameters:

* rawTransaction(`RawTransaction`) : The instance of `RawTransaction`.
* chainId(`int`) : The chainId.
* credentials(`Credentials`) : The instance of `Credentials`.

### Return Values

Return the signed message in `byte[]`.

## Get Error

```
Response.Error error = ethSendTransaction.getError();
```

### Parameters

No parameters.

### Return Values

Return an instance of `Response.Error`. If there is no errors, the instance will be `null`.  

## Get Hash Value

Get transaction hash.

```
String hash = ethSendTransaction.getTransactionHash();
```

### Parameters

No parameters.

### Return Values

Return the `String` value of transaction hash.

