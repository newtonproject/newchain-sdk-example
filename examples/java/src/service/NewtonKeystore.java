package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.web3j.crypto.CipherException;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author weixuefeng@lubangame.com
 * @version $Rev$
 * @time: 2019/12/12--1:17 PM
 * @description
 * @copyright (c) 2019 Newton Foundation. All rights reserved.
 */
public class NewtonKeystore {

    private static final String TAG = NewtonKeystore.class.getSimpleName();
    private String keyStorePath;

    public NewtonKeystore(String keyStorePath) {
        if(!keyStorePath.endsWith("/")){
            keyStorePath = keyStorePath + "/";
        }
        this.keyStorePath = keyStorePath;
        File file = new File(keyStorePath);
        if(!file.exists() || !file.isDirectory()){
            file.mkdirs();
        }
    }

    /**
     * Import Wallet by privateKey.
     * @param privateKey
     * @param password
     * @throws MnemonicException
     * @throws UnreadableWalletException
     * @throws CipherException
     * @throws IOException
     */
    public Wallet importPrivateKey(String privateKey, String password) throws MnemonicException, UnreadableWalletException, CipherException, IOException {
        NewtonWalletFile walletFile = NewtonWallet.createStandard(password, privateKey, null, NewtonWalletFile.FILETYPE_PRIVATE);
        boolean b = checkIsExistKeystore(walletFile.getAddress());
        if(!b) {
            String fileName = getNewtonWalletFileName(walletFile);
            File file = new File(keyStorePath + fileName);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, walletFile);
            return new Wallet(walletFile.getAddress(), walletFile.getType());
        }
        return null;
    }

    /**
     * Import Wallet by mnemonic with derivePath
     * @param mnemonic
     * @param derivePath
     * @param password
     * @throws MnemonicException
     * @throws UnreadableWalletException
     * @throws CipherException
     * @throws IOException
     */
    public Wallet importMnemonic(String mnemonic, String derivePath, String password) throws MnemonicException, UnreadableWalletException, CipherException, IOException {
        NewtonWalletFile walletFile = NewtonWallet.createStandard(password, mnemonic, derivePath, NewtonWalletFile.FILETYPE_MNEMONIC);
        boolean b = checkIsExistKeystore(walletFile.getAddress());
        if(!b) {
            String fileName = getNewtonWalletFileName(walletFile);
            File file = new File(keyStorePath + fileName);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, walletFile);
            return new Wallet(walletFile.getAddress(), walletFile.getType());
        }
        return null;
    }

    /**
     * Import Wallet with KeyStore content with checkPassword.
     * @param keyStore
     * @param password
     * @throws CipherException
     * @throws IOException
     */
    public Wallet importKeyStore(String keyStore, String password) throws CipherException, IOException, UnreadableWalletException, MnemonicException {
        ObjectMapper mapper = new ObjectMapper();
        NewtonWalletFile walletFile = mapper.readValue(keyStore, NewtonWalletFile.class);
        String type = walletFile.getType();
        walletFile.setAddress(convertHexAddress(walletFile.getAddress().toLowerCase()));
        boolean b = checkIsExistKeystore(walletFile.getAddress());
        if(!b) {
            String decrypt = NewtonWallet.decrypt(password, walletFile);
            if(type !=null && type.equals(NewtonWalletFile.FILETYPE_MNEMONIC)) {
                return importMnemonic(decrypt, walletFile.getDerivationPath(), password);
            }else{
                return importPrivateKey(decrypt, password);
            }
        }
        return null;
    }

    /**
     * check keystore with checkPassword.
     * @param keystore
     * @param password
     * @throws IOException
     * @throws CipherException
     */
    public void checkKeyStore(String keystore, String password) throws IOException, CipherException {
        ObjectMapper mapper = new ObjectMapper();
        NewtonWalletFile walletFile = mapper.readValue(keystore, NewtonWalletFile.class);
        String type = walletFile.getType();
        walletFile.setAddress(convertHexAddress(walletFile.getAddress().toLowerCase()));
        boolean b = checkIsExistKeystore(walletFile.getAddress());
        if(!b) {
            String decrypt = NewtonWallet.decrypt(password, walletFile);
        }
    }

    /**
     * import keystore with new checkPassword.
     * @param keyStore
     * @param password
     * @param newPassword
     * @return Wallet.
     * @throws CipherException
     * @throws IOException
     * @throws UnreadableWalletException
     * @throws MnemonicException
     */
    public Wallet importKeyStore(String keyStore, String password, String newPassword) throws CipherException, IOException, UnreadableWalletException, MnemonicException {
        ObjectMapper mapper = new ObjectMapper();
        NewtonWalletFile walletFile = mapper.readValue(keyStore, NewtonWalletFile.class);
        String type = walletFile.getType();
        walletFile.setAddress(convertHexAddress(walletFile.getAddress().toLowerCase()));
        boolean b = checkIsExistKeystore(walletFile.getAddress());
        if(!b) {
            String decrypt = NewtonWallet.decrypt(password, walletFile);
            if(type != null && type.equals(NewtonWalletFile.FILETYPE_MNEMONIC)) {
                return importMnemonic(decrypt, walletFile.getDerivationPath(), newPassword);
            }else{
                return importPrivateKey(decrypt, newPassword);
            }
        }
        return null;
    }

    /**
     * Export KeyStore
     * @param password
     * @param address
     * @return KeyStore content
     * @throws IOException
     * @throws CipherException
     * @throws UnreadableWalletException
     * @throws MnemonicException
     */
    public String exportKeyStore(String address, String password) throws IOException, CipherException, UnreadableWalletException, MnemonicException {
        return export(address, password, null);
    }

    /**
     * Export Mnemonic with checkPassword.
     * @param password
     * @param address
     * @return {String} Mnemonic Content
     * @throws IOException
     * @throws CipherException
     * @throws UnreadableWalletException
     * @throws MnemonicException
     */
    public String exportMnemonic(String address, String password) throws IOException, CipherException, UnreadableWalletException, MnemonicException {
        return export(address, password, NewtonWalletFile.FILETYPE_MNEMONIC);
    }

    /**
     * Export Private key with checkPassword
     * @param password
     * @param address
     * @return private key string.
     * @throws IOException
     * @throws CipherException
     * @throws UnreadableWalletException
     * @throws MnemonicException
     */
    public String exportPrivateKey(String address, String password) throws IOException, CipherException, UnreadableWalletException, MnemonicException {
        return export(address, password, NewtonWalletFile.FILETYPE_PRIVATE);
    }

    /**
     * Delete Account by checkPassword
     * @param password
     * @param address
     * @return Boolean delete successed.
     * @throws CipherException
     * @throws IOException
     */
    public boolean deleteAccount(String address, String password) throws CipherException, IOException {
        String keyStorePath = getKeyStorePath(address);
        if(keyStorePath != null) {
            ObjectMapper mapper = new ObjectMapper();
            File keyStore = new File(keyStorePath);
            NewtonWalletFile walletFile = mapper.readValue(keyStore, NewtonWalletFile.class);
            String decrypt = NewtonWallet.decrypt(password, walletFile);
            return keyStore.delete();
        }
        return false;
    }

    /**
     * Update Account by checkPassword.
     * @param address
     * @param password
     * @param newPassword
     * @return Boolean update successed.
     * @throws IOException
     * @throws CipherException
     * @throws UnreadableWalletException
     * @throws MnemonicException
     */
    public boolean updateAccount(String address, String password, String newPassword) throws IOException, CipherException, UnreadableWalletException, MnemonicException {
        String keyStorePath = getKeyStorePath(address);
        if(keyStorePath != null) {
            ObjectMapper mapper = new ObjectMapper();
            File keyStore = new File(keyStorePath);
            NewtonWalletFile walletFile = mapper.readValue(keyStore, NewtonWalletFile.class);
            String decrypt = NewtonWallet.decrypt(password, walletFile);
            NewtonWalletFile newWalletFile = NewtonWallet.createStandard(newPassword, decrypt, walletFile.getDerivationPath(), walletFile.getType());
            mapper.writeValue(keyStore, newWalletFile);
            return true;
        }
        return false;
    }

    /**
     * Get Wallets
     * @return Wallet[]
     * @throws IOException
     */
    public Wallet[] getWallets() throws IOException {
        File file = new File(keyStorePath);
        ArrayList<Wallet> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        if(file.exists() && file.isDirectory()) {
            // loop keystore directory.
            File[] files = file.listFiles();
            for(File targetFile : files) {
                NewtonWalletFile walletFile = mapper.readValue(targetFile, NewtonWalletFile.class);
                list.add(new Wallet(convertHexAddress(walletFile.getAddress()), walletFile.getType()));
            }
        }
        if(list.size() > 0) {
            Wallet[] wallets = new Wallet[list.size()];
            for(int i = 0; i < list.size(); i++) {
                wallets[i] = list.get(i);
            }
            return wallets;
        } else {
            return new Wallet[0];
        }
    }

    /**
     * Get Wallet
     * @param address
     * @return Wallet or null.
     * @throws IOException
     */
    public Wallet getWallet(String address) throws IOException {
        String keyStorePath = getKeyStorePath(address);
        if(keyStorePath != null) {
            ObjectMapper mapper = new ObjectMapper();
            NewtonWalletFile walletFile = mapper.readValue(new File(keyStorePath), NewtonWalletFile.class);
            return new Wallet(convertHexAddress(walletFile.getAddress()), walletFile.getType());
        }
        return null;
    }

    /**
     * has Wallet
     * @param address
     * @return Wallet
     */
    public boolean hasWallet(String address) {
        return checkIsExistKeystore(address);
    }


    public String getWalletType(String address) throws IOException {
        String keyStorePath = getKeyStorePath(address);
        if(keyStorePath != null) {
            ObjectMapper mapper = new ObjectMapper();
            NewtonWalletFile walletFile = mapper.readValue(new File(keyStorePath), NewtonWalletFile.class);
            return walletFile.getType();
        }
        return null;
    }


    private String convertHexAddress(String address) {
        return address.startsWith("0x") ? address.toLowerCase() : String.format("0x%s", address.toLowerCase());
    }


    /**
     * Get private key for sign transaction message.
     * @param password
     * @param address
     * @return private key string.
     * @throws UnreadableWalletException
     * @throws MnemonicException
     * @throws CipherException
     * @throws IOException
     */
    private String getPrivateKey(String address, String password) throws UnreadableWalletException, MnemonicException, CipherException, IOException {
        return exportPrivateKey(address, password);
    }

    /**
     * Get keystore path by address
     * @param address
     * @return keystore path
     */
    private String getKeyStorePath(String address) {
        File file = new File(keyStorePath);
        address = NewAddressUtils.newAddress2ethAddress(address);
        if(file.exists() && file.isDirectory()) {
            // loop keystore directory.
            String[] list = file.list();
            for(String fileName : list) {
                if(fileName.contains(address)){
                    return keyStorePath + fileName;
                }
            }
        }
        return null;
    }

    /**
     * Check has wallet by address.
     * @param address
     * @return
     */
    private boolean checkIsExistKeystore(String address) {
        String keyStorePath = getKeyStorePath(address);
        return keyStorePath != null;
    }

    /**
     * Export Wallet implement.
     * @param password
     * @param address
     * @param exportType
     * @return decrypt content.
     * @throws IOException
     * @throws CipherException
     * @throws MnemonicException
     * @throws UnreadableWalletException
     */
    private String export(String address, String password, String exportType) throws IOException, CipherException, MnemonicException, UnreadableWalletException {
        String keyStorePath = getKeyStorePath(address);
        if(keyStorePath != null) {
            File targetFile = new File(keyStorePath);
            ObjectMapper mapper = new ObjectMapper();
            NewtonWalletFile walletFile = mapper.readValue(targetFile, NewtonWalletFile.class);
            String decrypt = NewtonWallet.decrypt(password, walletFile);
            if(exportType == null) {
                return mapper.writeValueAsString(walletFile);
            }
            if(exportType.equals(NewtonWalletFile.FILETYPE_PRIVATE)) {
                if(walletFile.getType().equals(NewtonWalletFile.FILETYPE_PRIVATE)) {
                    return checkPrivLength(decrypt);
                }else {
                    ArrayList<String> mnemonicFromString = StringUtil.getMnemonicFromString(decrypt);
                    MnemonicCode.INSTANCE.check(mnemonicFromString);
                    byte[] seed = MnemonicCode.toSeed(mnemonicFromString, "");
                    // 生成确定性钱包的seed
                    DeterministicSeed deterministicSeed = new DeterministicSeed("", seed, "", System.currentTimeMillis());
                    DeterministicKeyChain keyChain = DeterministicKeyChain.builder().seed(deterministicSeed).build();
                    BigInteger privkeyeth = keyChain.getKeyByPath(HDUtils.parseSupportPath(walletFile.getDerivationPath()), true).getPrivKey();
                    return checkPrivLength(privkeyeth.toString(16));
                }
            }
            if(exportType.equals(NewtonWalletFile.FILETYPE_MNEMONIC)) {
                if(walletFile.getType().equals(NewtonWalletFile.FILETYPE_MNEMONIC)) {
                    return decrypt;
                }
            }
        }
        return null;
    }

    private String checkPrivLength(String s) {
        if(s.length() < 64) {
            int index = 64 - s.length();
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < index; i++) {
                builder.append("0");
            }
            return builder.append(s).toString();
        }
        return Numeric.prependHexPrefix(s);
    }

    /**
     *
     * @param address from address
     * @param signerPassword from checkPassword byte
     * @param salt from salt
     * @param iv from iv
     * @param ciphertext from ciphertext
     * @return {byte[]} privatekey
     * @throws CipherException
     */
    public byte[] getPrivateKeyByCipherText(String address, String signerPassword, byte[] salt, byte[] iv, byte[] ciphertext) throws CipherException {
        return NewtonWallet.decrypt(address, signerPassword, salt, iv, ciphertext);
    }


    private String getNewtonWalletFileName(NewtonWalletFile NewtonWalletFile) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.SSS'--'");
        return dateFormat.format(new Date()) + NewtonWalletFile.getAddress() + ".json";
    }
}
