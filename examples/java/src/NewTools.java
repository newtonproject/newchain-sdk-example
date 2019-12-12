import com.fasterxml.jackson.databind.ObjectMapper;
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

public class NewTools {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Web3j mWeb3j;
    private String chainId;

    /**
     * NewTools 构造函数
     * @param rpcUrl 链接的 rpc 接口，
     *           测试网: https://rpc1.newchain.newtonproject.org/
     *           主网: https://global.rpc.mainnet.newtonproject.org
     *
     */
    public NewTools(String rpcUrl) {
        mWeb3j = Web3j.build(new HttpService(rpcUrl));
        NetVersion netVersion;
        try {
            netVersion = mWeb3j.netVersion().send();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        chainId = netVersion.getNetVersion();
    }

    /**
     * 生成钱包文件
     * @param path 生成的钱包文件夹路径
     * @param password 密码
     * @return 返回钱包文件的路径
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     * @throws CipherException
     * @throws IOException
     */
    public String generateWallet(String path, String password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        return WalletUtils.generateNewWalletFile(password, new File(path));
    }

    public String getPublicKeyByPrivateKey(String priv) {
        ECKeyPair keyPair = ECKeyPair.create(Numeric.toBigInt(priv));
        return Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
    }

    /**
     * 获取私钥
     * @param path 文件路径
     * @param password 密码
     * @return 十六进制私钥
     * @throws IOException
     * @throws CipherException
     */
    public String loadWallet(String path, String password) throws IOException, CipherException {
        Credentials credentials = WalletUtils.loadCredentials(password, path);
        return Numeric.toHexStringWithPrefix(credentials.getEcKeyPair().getPrivateKey());
    }

    /**
     * 获取地址
     * @param path 钱包文件路径
     * @param password 密码
     * @return NEW 地址
     * @throws IOException
     * @throws CipherException
     */
    public String getAddress(String path, String password) throws IOException, CipherException {
        Credentials credentials = WalletUtils.loadCredentials(password, path);
        return AddressUtil.originalAddress2NewAddress(credentials.getAddress(), chainId);
    }

    /**
     * 更新钱包密码
     * @param path 钱包文件路径
     * @param password 钱包文件密码
     * @param newPassword 新密码
     * @return 钱吧文件路径
     * @throws IOException
     * @throws CipherException
     */
    public String updatePassword(String path, String password, String newPassword) throws IOException, CipherException {
        Credentials credentials = WalletUtils.loadCredentials(password, path);
        WalletFile walletFile = Wallet.createStandard(newPassword, credentials.getEcKeyPair());
        File destination = new File(path);
        objectMapper.writeValue(destination, walletFile);
        return path;
    }

    /**
     * 获取钱包余额，同步方法
     * @param address new 的地址
     * @throws IOException
     */
    public String getBalance(String address) throws IOException {
        address = AddressUtil.newAddress2originalAddress(address);
        EthGetBalance balance = mWeb3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        BigInteger b = balance.getBalance();
        return b + "";
    }

    /**
     * 发送交易
     * @param path 钱包文件路径
     * @param to 接受 token 方的 NEW 的地址
     * @param password 钱包文件密码
     * @param value 发送 token 的数量
     * @return 交易的hash
     * @throws IOException
     * @throws CipherException
     * @throws SendTransactionError
     */
    public String sendTransaction(String path, String to, String password, String value) throws IOException, CipherException, SendTransactionError {
        to = AddressUtil.newAddress2originalAddress(to);
        Credentials credentials = WalletUtils.loadCredentials(password, path);
        String fromAddress = credentials.getAddress();
        EthGetTransactionCount ethGetTransactionCount = mWeb3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        EthGasPrice ethGasPrice = mWeb3j.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice();
        Transaction tx = Transaction.createEtherTransaction(fromAddress, nonce, gasPrice, null, to, Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.ETHER).toBigInteger());
        EthEstimateGas ethEstimateGas = mWeb3j.ethEstimateGas(tx).send();
        BigInteger gasLimit = ethEstimateGas.getAmountUsed();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, to, Convert.toWei(BigDecimal.valueOf(Long.valueOf(value)), Convert.Unit.ETHER).toBigInteger());
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Integer.parseInt(chainId), credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = mWeb3j.ethSendRawTransaction(hexValue).send();
        Response.Error error = ethSendTransaction.getError();
        if (error != null) {
            throw new SendTransactionError(error.getMessage());
        }
        return ethSendTransaction.getTransactionHash();
    }

    private class SendTransactionError extends Exception {
        SendTransactionError(String message){
            super(message);
        }
    }

    public static String signMessage(String message, Credentials credentials) {
        ECKeyPair ecKeyPair = credentials.getEcKeyPair();
        Sign.SignatureData signatureData = Sign.signMessage(message.getBytes(), ecKeyPair);
        String signR = Numeric.toHexString(signatureData.getR());
        String signS = Numeric.toHexStringNoPrefix(signatureData.getS());
        return signR + signS;
    }

    public static void main(String[] arsg) throws Exception {
        String dir = "/Users/erhu/temp/keystore";
        String file1 = "UTC--2019-10-29T02-41-01.754000000Z--3ea0aec0ef248dce87dc21fcc69f7df285ef62c3.json";
        String file2 = "UTC--2019-10-29T02-41-06.745000000Z--032a0d321d5633e0f0dfc1191be9b7dd5817b5de.json";
        Credentials credentials = WalletUtils.loadCredentials("123456", new File(dir + "/" + file1));
        ECKeyPair keyPair = credentials.getEcKeyPair();
        System.out.println(Numeric.toHexStringWithPrefix(keyPair.getPrivateKey()));

        Credentials credentials2 = WalletUtils.loadCredentials("123456", new File(dir + "/" + file2));
        ECKeyPair keyPair2 = credentials2.getEcKeyPair();
        System.out.println(Numeric.toHexStringWithPrefix(keyPair2.getPrivateKey()));
//        NewTools tools = new NewTools("https://rpc1.newchain.newtonproject.org/");
//        String s = tools.generateWallet(dir, "123456");
//        System.out.println(s);
        String signMessage = signMessage("hi", credentials);
        System.out.println(signMessage);

    }

    /**
     * @author weixuefeng@lubangame.com
     * @version $Rev$
     * @time: 2019/12/12--1:12 PM
     * @description
     * @copyright (c) 2019 Newton Foundation. All rights reserved.
     */
    public static class WalletTools {

    }
}
