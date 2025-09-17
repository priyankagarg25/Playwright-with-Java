package utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

public abstract class AESCrypto {
    private static final String ALGO = "AES/ECB/PKCS5Padding";
    private static final String KEY = "1234567890123456";

    public static String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }


}