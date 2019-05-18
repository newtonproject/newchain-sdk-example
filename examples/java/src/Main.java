import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
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

    //Test Net
    private final static String rpcUrl = "https://rpc1.newchain.newtonproject.org/";
    //Main Net
    //private final static String rpcUrl = "https://global.rpc.mainnet.newtonproject.org";

    public static void main(String[] args) throws CipherException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Web3j web3 = Web3j.build(new HttpService(rpcUrl));

        NetVersion netVersion;
        try {
            netVersion = web3.netVersion().send();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String chainIDStr = netVersion.getNetVersion();
        System.out.println("Client version : " + chainIDStr);

        //comment out if you want to create keystore
        /*String fileName = WalletUtils.generateNewWalletFile(
                "123qwe",
                new File("C:\\Files\\wallet"));
        System.out.println("File name : " + fileName);*/

        Credentials credentials = WalletUtils.loadCredentials(
                "123qwe",
                "C:\\Files\\wallet\\UTC--2019-03-09T03-20-42.743000000Z--02d9bec4c13aecd197362adf92ed23b00a95d8ab.json");

        String fromAddress = credentials.getAddress();
        System.out.println("address(eth) : " + fromAddress);

        String demo = AddressUtil.originalAddress2NewAddress(fromAddress, chainIDStr);
        System.out.println("address(NEW) : " + demo);

        EthGetBalance balance = web3.ethGetBalance(fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger b = balance.getBalance();
        System.out.println("balance : " + b);

        // get the next available nonce
        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        System.out.println("nonce : " + nonce);

        //Main net address
//        String newAddress = "NEW182KVqyBnPTxGVhU57krvhTy5i5SQBYecxZh";
//        String toAddress = AddressUtil.newAddress2ethAddress(newAddress);
//        System.out.println("to address : " + toAddress);

        //Test net address
        String newAddress = "NEW17zJoq3eHwv3x7cJNgdmG73Limvv7TwQurB4";
        String toAddress = AddressUtil.newAddress2originalAddress(newAddress);
        System.out.println("to address : " + toAddress);

        //getChainID() return the chain ID in hex string
        String addressCahinID = AddressUtil.getChainID(newAddress);

        Integer inputChainID = Integer.parseInt(addressCahinID,16);
        System.out.println("input ID : " + inputChainID);
        Integer chainID = Integer.parseInt(chainIDStr);
        System.out.println("chain ID : " + chainID);

        if(!inputChainID.equals(chainID)){
            System.out.println("Wrong input address. Please check the address you input.");
            return;
        }else{
            System.out.println("Right address.");
        }

        EthGasPrice ethGasPrice = web3.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice();
        System.out.println("gasPrice : " + gasPrice);

        Transaction tx = Transaction.createEtherTransaction(fromAddress, nonce, gasPrice, null, toAddress, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());
        EthEstimateGas ethEstimateGas = web3.ethEstimateGas(tx).send();
        BigInteger gasLimit = ethEstimateGas.getAmountUsed();
        System.out.println("gasLimit : " + gasLimit);

        // create our transaction
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, toAddress, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());

        // sign & send our transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Integer.parseInt(chainIDStr), credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        System.out.println("tx value : " + hexValue);
        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
        Response.Error error = ethSendTransaction.getError();
        if (error != null) {
            System.out.println("error : " + error.getMessage());
        }
        String hash = ethSendTransaction.getTransactionHash();
        System.out.println("hash : " + hash);
    }
}
