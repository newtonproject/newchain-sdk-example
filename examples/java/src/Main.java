/**
  * Java example for NewChain SDK
  * @version 1
  * @author lixuan@diynova.com
  * @copyright (c) 2016 Beijing ShenJiangHuDong Technology Co., Ltd. All rights reserved.
  */

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.NetVersion;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import org.web3j.protocol.http.HttpService;
import org.web3j.utils.AddressUtil;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Main {

    private final static String rpcUrl = "https://rpc1.newchain.newtonproject.org/";

    public static void main(String[] args) throws CipherException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Web3j web3 = Web3j.build(new HttpService(rpcUrl));

        NetVersion netVersion;
        try {
            netVersion = web3.netVersion().send();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String clientVersion = netVersion.getNetVersion();
        System.out.println("Client version : " + clientVersion);

        // Comment out if you want to create keystore
        /*String fileName = WalletUtils.generateNewWalletFile(
                "123qwe",
                new File("C:\\Files\\wallet"));
        System.out.println("File name : " + fileName);*/

        Credentials credentials = WalletUtils.loadCredentials(
                "123qwe",
                "C:\\Files\\wallet\\UTC--2019-03-09T03-20-42.743000000Z--02d9bec4c13aecd197362adf92ed23b00a95d8ab.json");

        String fromAddress = credentials.getAddress();
        System.out.println("Address(eth) : " + fromAddress);

        String demo = AddressUtil.ethAddress2NewAddress(fromAddress, clientVersion);
        System.out.println("Address(NEW) : " + demo);

        EthGetBalance balance = web3.ethGetBalance(fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger b = balance.getBalance();
        System.out.println("Balance : " + b);

        // Get the next available nonce
        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        System.out.println("Nonce : " + nonce);

        // Main net address
        // String newAddress = "NEW182KVqyBnPTxGVhU57krvhTy5i5SQBYecxZh";
        // String toAddress = AddressUtil.newAddress2ethAddress(newAddress);
        // System.out.println("To address : " + toAddress);

        // Test net address
        String newAddress = "NEW17zJoq3eHwv3x7cJNgdmG73Limvv7TwQurB4";
        String toAddress = AddressUtil.newAddress2ethAddress(newAddress);
        System.out.println("To address : " + toAddress);


        EthGasPrice ethGasPrice = web3.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice();
        System.out.println("GasPrice : " + gasPrice);

        Transaction tx = Transaction.createEtherTransaction(fromAddress, nonce, gasPrice, null, toAddress, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());
        EthEstimateGas ethEstimateGas = web3.ethEstimateGas(tx).send();
        BigInteger gasLimit = ethEstimateGas.getAmountUsed();
        System.out.println("GasLimit : " + gasLimit);

        // Create transaction
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, toAddress, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());

        // Sign and send transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Integer.parseInt(clientVersion), credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        System.out.println("Tx value : " + hexValue);
        
        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
        Response.Error error = ethSendTransaction.getError();
        if (error != null) {
            System.out.println("Error : " + error.getMessage());
        }
        
        String hash = ethSendTransaction.getTransactionHash();
        System.out.println("Hash : " + hash);
    }
}
