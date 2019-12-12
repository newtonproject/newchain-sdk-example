package service;

import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.KeyParameter;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.utils.Numeric;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author weixuefeng@lubangame.com
 * @version $Rev$
 * @time: 2019/12/12--1:17 PM
 * @description
 * @copyright (c) 2019 Newton Foundation. All rights reserved.
 */
public class NewtonWallet {
    private static final int N_LIGHT = 1 << 12;
    private static final int P_LIGHT = 6;

    private static final int N_STANDARD = 1 << 18;
    private static final int P_STANDARD = 1;

    private static final int R = 8;
    private static final int DKLEN = 32;

    private static final int CURRENT_VERSION = 3;

    private static final String CIPHER = "aes-128-ctr";
    static final String AES_128_CTR = "pbkdf2";
    static final String SCRYPT = "scrypt";
    private static final String TAG = NewtonWallet.class.getSimpleName();

    private static NewtonWalletFile create(String password, String chiperText, String derivePath, String type, int n, int p)
            throws CipherException, MnemonicException, UnreadableWalletException {
        byte[] salt = generateRandomBytes(32);
        //byte[] derivedKey = generateDerivedScryptKey(checkPassword.getBytes(UTF_8), salt, n, R, p, DKLEN);
        byte[] derivedKey = com.lambdaworks.crypto.SCrypt.scryptN(password.getBytes(Charset.forName("UTF-8")), salt, n, R, p, DKLEN);
        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
        byte[] iv = generateRandomBytes(16);
        byte[] privateKeyBytes = null;
        ECKeyPair keyPair = null;
        if(type != null && type.equals(NewtonWalletFile.FILETYPE_MNEMONIC)) {
            // mnemonic type
            privateKeyBytes = chiperText.getBytes();
            ArrayList<String> mnemonicFromString = StringUtil.getMnemonicFromString(chiperText);
            MnemonicCode.INSTANCE.check(mnemonicFromString);
            byte[] seed = MnemonicCode.toSeed(mnemonicFromString, "");
            DeterministicSeed deterministicSeed = new DeterministicSeed("", seed, "", System.currentTimeMillis());
            DeterministicKeyChain keyChain = DeterministicKeyChain.builder().seed(deterministicSeed).build();

            BigInteger privkeyeth = keyChain.getKeyByPath(HDUtils.parseSupportPath(derivePath), true).getPrivKey();
            keyPair = ECKeyPair.create(privkeyeth);
        } else if(type != null && type.equals(NewtonWalletFile.FILETYPE_PRIVATE)) {
            // private type
            privateKeyBytes = Numeric.hexStringToByteArray(chiperText);
            keyPair = ECKeyPair.create(privateKeyBytes);
        }
        String address = Keys.getAddress(keyPair);
        byte[] cipherText = performCipherOperation(
                Cipher.ENCRYPT_MODE, iv, encryptKey, privateKeyBytes);

        byte[] mac = generateMac(derivedKey, cipherText);

        return createNewtonWalletFile(address, cipherText, iv, salt, mac, n, p, derivePath, type);
    }

    public static NewtonWalletFile createStandard(String password, String chiperText, String derivePath, String type)
            throws CipherException, UnreadableWalletException, MnemonicException {
        return create(password, chiperText, derivePath, type, N_STANDARD, P_STANDARD);
    }

    public static NewtonWalletFile createLight(String password, String chiperText, String derivePath, String type)
            throws CipherException, UnreadableWalletException, MnemonicException {
        return create(password, chiperText, derivePath, type, N_LIGHT, P_LIGHT);
    }

    private static NewtonWalletFile createNewtonWalletFile(
            String address, byte[] cipherText, byte[] iv, byte[] salt, byte[] mac,
            int n, int p, String derivePath, String type) {
        NewtonWalletFile NewtonWalletFile = new NewtonWalletFile();
        NewtonWalletFile.setAddress(address.startsWith("0x") ? address : String.format("0x%s", address) );
        NewtonWalletFile.setType(type);
        NewtonWalletFile.setDerivationPath(derivePath);
        NewtonWalletFile.Crypto crypto = new NewtonWalletFile.Crypto();
        crypto.setCipher(CIPHER);
        crypto.setCiphertext(Numeric.toHexStringNoPrefix(cipherText));

        NewtonWalletFile.CipherParams cipherParams = new NewtonWalletFile.CipherParams();
        cipherParams.setIv(Numeric.toHexStringNoPrefix(iv));
        crypto.setCipherparams(cipherParams);

        crypto.setKdf(SCRYPT);
        NewtonWalletFile.ScryptKdfParams kdfParams = new NewtonWalletFile.ScryptKdfParams();
        kdfParams.setDklen(DKLEN);
        kdfParams.setN(n);
        kdfParams.setP(p);
        kdfParams.setR(R);
        kdfParams.setSalt(Numeric.toHexStringNoPrefix(salt));
        crypto.setKdfparams(kdfParams);

        crypto.setMac(Numeric.toHexStringNoPrefix(mac));
        NewtonWalletFile.setCrypto(crypto);
        NewtonWalletFile.setId(UUID.randomUUID().toString());
        NewtonWalletFile.setVersion(CURRENT_VERSION);
        return NewtonWalletFile;
    }

    private static byte[] generateDerivedScryptKey(
            byte[] password, byte[] salt, int n, int r, int p, int dkLen) throws CipherException {
        return SCrypt.generate(password, salt, n, r, p, dkLen);
    }

    private static byte[] generateAes128CtrDerivedKey(
            byte[] password, byte[] salt, int c, String prf) throws CipherException {

        if (!prf.equals("hmac-sha256")) {
            throw new CipherException("Unsupported prf:" + prf);
        }

        // Java 8 supports this, but you have to convert the checkPassword to a character array, see
        // http://stackoverflow.com/a/27928435/3211687

        PKCS5S2ParametersGenerator gen;
        gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(password, salt, c);
        return ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
    }

    private static byte[] performCipherOperation(
            int mode, byte[] iv, byte[] encryptKey, byte[] text) throws CipherException {

        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
            cipher.init(mode, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(text);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException e) {
            throw new CipherException("Error performing cipher operation", e);
        }
    }

    private static byte[] generateMac(byte[] derivedKey, byte[] cipherText) {
        byte[] result = new byte[16 + cipherText.length];

        System.arraycopy(derivedKey, 16, result, 0, 16);
        System.arraycopy(cipherText, 0, result, 16, cipherText.length);

        return Hash.sha3(result);
    }

    public static String decrypt(String password, NewtonWalletFile NewtonWalletFile)
            throws CipherException {

        validate(NewtonWalletFile);
        NewtonWalletFile.Crypto crypto = NewtonWalletFile.getCrypto();
        String type = NewtonWalletFile.getType();
        type = type == null ? NewtonWalletFile.FILETYPE_PRIVATE : type;
        byte[] mac = Numeric.hexStringToByteArray(crypto.getMac());
        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
        byte[] cipherText = Numeric.hexStringToByteArray(crypto.getCiphertext());

        byte[] derivedKey;

        NewtonWalletFile.KdfParams kdfParams = crypto.getKdfparams();
        if (kdfParams instanceof NewtonWalletFile.ScryptKdfParams) {
            NewtonWalletFile.ScryptKdfParams scryptKdfParams =
                    (NewtonWalletFile.ScryptKdfParams) crypto.getKdfparams();
            int dklen = scryptKdfParams.getDklen();
            int n = scryptKdfParams.getN();
            int p = scryptKdfParams.getP();
            int r = scryptKdfParams.getR();
            byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());
            //derivedKey = generateDerivedScryptKey(checkPassword.getBytes(Charset.forName("UTF-8")), salt, n, r, p, dklen);
            derivedKey = com.lambdaworks.crypto.SCrypt.scryptN(password.getBytes(Charset.forName("UTF-8")), salt, n, r, p, dklen);
        } else if (kdfParams instanceof NewtonWalletFile.Aes128CtrKdfParams) {
            NewtonWalletFile.Aes128CtrKdfParams aes128CtrKdfParams =
                    (NewtonWalletFile.Aes128CtrKdfParams) crypto.getKdfparams();
            int c = aes128CtrKdfParams.getC();
            String prf = aes128CtrKdfParams.getPrf();
            byte[] salt = Numeric.hexStringToByteArray(aes128CtrKdfParams.getSalt());

            derivedKey = generateAes128CtrDerivedKey(password.getBytes(Charset.forName("UTF-8")), salt, c, prf);
        } else {
            throw new CipherException("Unable to deserialize params: " + crypto.getKdf());
        }

        byte[] derivedMac = generateMac(derivedKey, cipherText);

        if (!Arrays.equals(derivedMac, mac)) {
            throw new CipherException("password error");
        }
        String res = null;
        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
        byte[] privateKey = performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKey, cipherText);
        if(type.equals(NewtonWalletFile.FILETYPE_MNEMONIC)){
            res = new String(privateKey);
        }else if(type.equals(NewtonWalletFile.FILETYPE_PRIVATE)){
            res = Numeric.toHexString(privateKey);
        }
        return res;
    }

    // decrypt checkPassword which from nfc. ps: for NewCash
    static byte[] decrypt(String address, String password, byte[] salt, byte[] iv, byte[] ciphertext) throws CipherException {
        byte[] derivedKey = com.lambdaworks.crypto.SCrypt.scryptN(password.getBytes(Charset.forName("UTF-8")), salt, N_LIGHT, R, P_LIGHT, DKLEN);
        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
        byte[] privateKey = performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKey, ciphertext);
        if(checkDecrypt(address, privateKey)) {
            return privateKey;
        }
        // try again...
        byte[] derivedKeyS = com.lambdaworks.crypto.SCrypt.scryptN(password.getBytes(Charset.forName("UTF-8")), salt, N_STANDARD, R, P_STANDARD, DKLEN);
        byte[] encryptKeyS = Arrays.copyOfRange(derivedKeyS, 0, 16);
        byte[] privateKeyS = performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKeyS, ciphertext);
        if(checkDecrypt(address, privateKeyS)) {
            return privateKeyS;
        }
        return null;
    }

    private static boolean checkDecrypt(String address, byte[] ecKeyPair) {
        return Numeric.cleanHexPrefix(Keys.getAddress(ECKeyPair.create(ecKeyPair))).equals(Numeric.cleanHexPrefix(address));
    }

    static void validate(NewtonWalletFile NewtonWalletFile) throws CipherException {
        NewtonWalletFile.Crypto crypto = NewtonWalletFile.getCrypto();

        if (NewtonWalletFile.getVersion() != CURRENT_VERSION) {
            throw new CipherException("Wallet version is not supported");
        }

        if (!crypto.getCipher().equals(CIPHER)) {
            throw new CipherException("Wallet cipher is not supported");
        }

        if (!crypto.getKdf().equals(AES_128_CTR) && !crypto.getKdf().equals(SCRYPT)) {
            throw new CipherException("KDF type is not supported");
        }
    }

    static byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        SecureRandomUtils.SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }

}
