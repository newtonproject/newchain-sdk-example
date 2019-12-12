package service;

import io.reactivex.Completable;
import io.reactivex.Single;
import org.web3j.crypto.Sign;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author weixuefeng@lubangame.com
 * @version $Rev$
 * @time: 2019/12/12--1:14 PM
 * @description
 * @copyright (c) 2019 Newton Foundation. All rights reserved.
 */
public interface AccountKeystoreService {
    /**
     * Create account in keystore
     *
     * @param password account checkPassword
     * @return new {@link Wallet}
     */
    Single<Wallet> createAccount(String password);

    /**
     * Create account for mnemonic
     *
     * @return mnemonic
     */
    Single<ArrayList<String>> createAccountMnemonic();

    /**
     * Include new existing keystore
     *
     * @param store    store to include
     * @param password store checkPassword
     * @return included {@link Wallet} if success
     */
    Single<Wallet> importKeystore(String store, String password);

    Single<Wallet> importPrivateKey(String privateKey, String password);

    Single<Wallet> importMnemonic(List<String> mnemonics, String password);

    /**
     * Export wallet to keystore
     *
     * @param wallet   wallet to export
     * @param password checkPassword from wallet
     * @return store data
     */
    Single<String> exportAccount(Wallet wallet, String password);

    /**
     * Export wallet to privateKey
     *
     * @param wallet   wallet to export
     * @param password checkPassword from wallet
     */
    Single<String> exportPrivateKey(Wallet wallet, String password);

    /**
     * Export wallet to mnemonic
     */
    Single<ArrayList<String>> exportMnemonic(Wallet wallet, String password);

    /**
     * Delete account from keystore
     *
     * @param address  account address
     * @param password account checkPassword
     */
    Completable deleteAccount(String address, String password);

    /**
     * udpate account from keystore
     *
     * @param address  account address
     * @param password account checkPassword
     */
    Completable updateAccount(String address, String password, String newPassword);

    /**
     * Sign transaction
     *
     * @param signer         {@link Wallet}
     * @param signerPassword checkPassword from {@link Wallet}
     * @param toAddress      transaction destination address
     * @param nonce
     * @return sign data
     */
    Single<byte[]> signTransaction(
            Wallet signer,
            String signerPassword,
            String toAddress,
            BigInteger amount,
            BigInteger gasPrice,
            BigInteger gasLimit,
            long nonce,
            byte[] data,
            long chainId);


    /**
     * Sign message
     *
     * @return
     */
    Single<Sign.SignatureData> signMessage(Wallet signer, String password, String message);

    /**
     * Sign message with access Key
     *
     * @return Sign.SignatureData
     */
    Single<Sign.SignatureData> signMessage(String accessKey, String message);


    /**
     * Sign transaction
     *
     * @param signer         {@link Wallet}
     * @param signerPassword checkPassword from {@link Wallet}
     * @param toAddress      transaction destination address
     * @param nonce
     * @return sign data
     */
    Single<byte[]> signTransaction(
            Wallet signer,
            String signerPassword,
            String toAddress,
            BigInteger amount,
            BigInteger gasPrice,
            BigInteger gasLimit,
            long nonce,
            byte[] data,
            long chainId,
            byte[] salt,
            byte[] iv,
            byte[] ciphertext);


    /**
     * Check if there is an address in the keystore
     *
     * @param address {@link Wallet} address
     */
    boolean hasAccount(String address);

    /**
     * Return all {@link Wallet} from keystore
     *
     * @return wallets
     */
    Single<Wallet[]> fetchAccounts();

    /**
     * Check Wallet type.mnemonic or private
     *
     * @return keystore type
     */
    Single<String> getWalletType(String address);


    /**
     * check keystore
     *
     * @param keystore
     * @param password
     * @return
     */
    Completable checkKeystore(String keystore, String password);

    /**
     * import keystore with newpassword.
     *
     * @param keystore
     * @param password
     * @param newPassword
     * @return
     */
    Single<Wallet> importKeystore(String keystore, String password, String newPassword);
}
