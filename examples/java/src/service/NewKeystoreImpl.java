package service;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author weixuefeng@lubangame.com
 * @version $Rev$
 * @time: 2019/12/12--1:16 PM
 * @description
 * @copyright (c) 2019 Newton Foundation. All rights reserved.
 */
public class NewKeystoreImpl implements AccountKeystoreService {
    private BigInteger M_GAS_PRICE = BigInteger.valueOf(1000000000L);
    private BigInteger M_GAS_LIMIT = BigInteger.valueOf(34056L);

    private static final int PRIVATE_KEY_RADIX = 16;
    /**
     * CPU/Memory cost parameter. Must be larger than 1, a power of 2 and less than 2^(128 * r / 8).
     */
    private static final int N = 1 << 9;
    /**
     * Parallelization parameter. Must be a positive integer less than or equal to Integer.MAX_VALUE / (128 * r * 8).
     */
    private static final int P = 1;

    private final NewtonKeystore keyStore;

    private static String TAG = "NewKeystoreAccountService";

    public NewKeystoreImpl(File keyStoreFile) {
        keyStore = new NewtonKeystore(keyStoreFile.getAbsolutePath());
    }

    public NewKeystoreImpl(NewtonKeystore keyStore) {
        this.keyStore = keyStore;
    }

    @Override
    public Single<Wallet> createAccount(String password) {
        return null;
    }

    @Override
    public Single<ArrayList<String>> createAccountMnemonic() {
        return Single.fromCallable(() -> {
            byte init[] = new byte[16];
            SecureRandomUtils.secureRandom().nextBytes(init);
            String mnemonic = MnemonicForNewton.generateMnemonic(init);
            return StringUtil.getMnemonicFromString(mnemonic);
        });
    }

    @Override
    public Single<Wallet> importKeystore(String store, String password) {
        return Single.fromCallable(() -> keyStore.importKeyStore(store, password))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Wallet> importPrivateKey(String privateKey, String password) {
        return Single.fromCallable(() -> keyStore.importPrivateKey(privateKey, password)).
                subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Wallet> importMnemonic(List<String> mnemonics, String newPassword) {
        return Single.fromCallable(() -> keyStore.importMnemonic(StringUtil.getMnemonicFromList(mnemonics), "m/44'/1642'/0'/0/0", newPassword));
    }

    @Override
    public Single<String> exportAccount(Wallet wallet, String password) {
        return  Single
                .fromCallable(() -> keyStore.exportKeyStore(wallet.address, password))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<String> exportPrivateKey(Wallet wallet, String password) {
        return Single
                .fromCallable(() -> keyStore.exportPrivateKey(wallet.address, password))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<ArrayList<String>> exportMnemonic(Wallet wallet, String password) {
        return Single
                .fromCallable(() -> StringUtil.getMnemonicFromString(keyStore.exportMnemonic(wallet.address, password)))
                .subscribeOn(Schedulers.io());
    }


    @Override
    public Completable deleteAccount(String address, String password) {
        return Completable.fromAction(
                () -> keyStore.deleteAccount(address, password))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable updateAccount(String address, String password, String newPassword) {
        return Completable.fromAction(
                () -> keyStore.updateAccount(address, password, newPassword))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<byte[]> signTransaction(Wallet signer, String signerPassword, String toAddress, BigInteger amount,
                                          BigInteger gasPrice, BigInteger gasLimit, long nonce, byte[] data, long chainId) {
        return Single.fromCallable(() -> {
            String transactionData = data == null ? "0x" : Numeric.toHexString(data);
            RawTransaction transaction = RawTransaction.createTransaction(BigInteger.valueOf(nonce), gasPrice, gasLimit, NewAddressUtils.newAddress2ethAddress(toAddress), amount, transactionData);
            String s = keyStore.exportPrivateKey(signer.address, signerPassword);
            Credentials credentials = Credentials.create(Numeric.prependHexPrefix(s));
            return TransactionEncoder.signMessage(transaction, (int) chainId, credentials);
        })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<byte[]> signTransaction(Wallet signer, String signerPassword, String toAddress, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit, long nonce, byte[] data, long chainId, byte[] salt, byte[] iv, byte[] ciphertext) {
        return Single.fromCallable(() -> {
            String transactionData = data == null ? "0x" : Numeric.toHexString(data);
            RawTransaction transaction = RawTransaction.createTransaction(BigInteger.valueOf(nonce), gasPrice, gasLimit, toAddress, amount, transactionData);
            byte[] privateKey = keyStore.getPrivateKeyByCipherText(signer.address, signerPassword, salt, iv, ciphertext);
            Credentials credentials = Credentials.create(Numeric.toHexString(privateKey));
            return TransactionEncoder.signMessage(transaction, (int) chainId, credentials);
        })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Sign.SignatureData> signMessage(Wallet signer, String password, String message) {
        return Single.fromCallable(()-> {
            String s = keyStore.exportPrivateKey(signer.address, password);
            return Sign.signMessage(message.getBytes(), ECKeyPair.create(Numeric.toBigInt(s)));
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Sign.SignatureData> signMessage(String accessKey, String message) {
        return Single.fromCallable(()-> Sign.signMessage(message.getBytes(), ECKeyPair.create(Numeric.toBigInt(accessKey))));
    }


    @Override
    public boolean hasAccount(String address) {
        return keyStore.hasWallet(address);
    }

    @Override
    public Single<Wallet[]> fetchAccounts() {
        return Single.fromCallable(keyStore::getWallets)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<String> getWalletType(String address) {
        return Single.fromCallable(() -> keyStore.getWalletType(address))
                .subscribeOn(Schedulers.io());
    }

    private Wallet findAccount(String address) throws IOException {
        return keyStore.getWallet(address);
    }

    @Override
    public Completable checkKeystore(String keystore, String password) {
        return Completable.fromAction(
                () -> keyStore.checkKeyStore(keystore, password))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Wallet> importKeystore(String keystore, String password, String newPassword) {
        return Single.fromCallable(() -> keyStore.importKeyStore(keystore, password, newPassword))
                .subscribeOn(Schedulers.io());
    }
}
