package com.abb.bye.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
public class EncodeUtil {
    private static final Logger log = LoggerFactory.getLogger(EncodeUtil.class);
    private static final String SHA1PRNG = "SHA1PRNG";
    private static final String AES = "AES";
    private static String serverKey = "fff3dfxx56`?";
    private static String clientKey = "D@#dsgfh145";
    private static String cookieKey = "6fdTE5$^Ygf#";
    private static Cipher serverEncryptCipher;
    private static Cipher serverDecryptCipher;
    private static Cipher clientEncryptCipher;
    private static Cipher clientDecryptCipher;
    private static Cipher cookieEncryptCipher;
    private static Cipher cookieDecryptCipher;
    private static SecretKeySpec serverKeySpec;
    private static SecretKeySpec clientKeySpec;
    private static SecretKeySpec cookieKeySpec;

    static {
        KeyGenerator serverkgen;
        KeyGenerator clientkgen;
        KeyGenerator cookiekgen;
        try {
            serverkgen = KeyGenerator.getInstance(AES);
            SecureRandom serverSecureRandom = SecureRandom.getInstance(SHA1PRNG);
            serverSecureRandom.setSeed(serverKey.getBytes());
            serverkgen.init(128, serverSecureRandom);
            SecretKey serverSecretKey = serverkgen.generateKey();
            byte[] serverEnCodeFormat = serverSecretKey.getEncoded();
            serverKeySpec = new SecretKeySpec(serverEnCodeFormat, AES);
            serverDecryptCipher = Cipher.getInstance(AES);
            serverDecryptCipher.init(Cipher.DECRYPT_MODE, serverKeySpec);
            serverEncryptCipher = Cipher.getInstance(AES);
            serverEncryptCipher.init(Cipher.ENCRYPT_MODE, serverKeySpec);

            clientkgen = KeyGenerator.getInstance(AES);
            SecureRandom clientSecureRandom = SecureRandom.getInstance(SHA1PRNG);
            clientSecureRandom.setSeed(clientKey.getBytes());
            clientkgen.init(128, clientSecureRandom);

            SecretKey clientSecretKey = clientkgen.generateKey();
            byte[] clientEnCodeFormat = clientSecretKey.getEncoded();
            clientKeySpec = new SecretKeySpec(clientEnCodeFormat, AES);
            clientDecryptCipher = Cipher.getInstance(AES);
            clientDecryptCipher.init(Cipher.DECRYPT_MODE, clientKeySpec);
            clientEncryptCipher = Cipher.getInstance(AES);
            clientEncryptCipher.init(Cipher.ENCRYPT_MODE, clientKeySpec);

            cookiekgen = KeyGenerator.getInstance(AES);
            SecureRandom cookieSecureRandom = SecureRandom.getInstance(SHA1PRNG);
            cookieSecureRandom.setSeed(cookieKey.getBytes());
            cookiekgen.init(128, cookieSecureRandom);
            SecretKey cookieSecretKey = cookiekgen.generateKey();
            byte[] cookieEnCodeFormat = cookieSecretKey.getEncoded();
            cookieKeySpec = new SecretKeySpec(cookieEnCodeFormat, AES);
            cookieDecryptCipher = Cipher.getInstance(AES);
            cookieDecryptCipher.init(Cipher.DECRYPT_MODE, cookieKeySpec);
            cookieEncryptCipher = Cipher.getInstance(AES);
            cookieEncryptCipher.init(Cipher.ENCRYPT_MODE, cookieKeySpec);
        } catch (Exception e) {
            log.error("happens error when initialize EncodeUtil", e);
        }
    }

    public static Cipher getCipher(String seed, boolean isEncrypt) {
        Cipher cipher = null;
        try {
            KeyGenerator clientkgen = KeyGenerator.getInstance(AES);
            SecureRandom clientSecureRandom = SecureRandom.getInstance(SHA1PRNG);
            clientSecureRandom.setSeed(seed.getBytes());
            clientkgen.init(128, clientSecureRandom);
            SecretKey clientSecretKey = clientkgen.generateKey();
            byte[] clientEnCodeFormat = clientSecretKey.getEncoded();
            SecretKeySpec clientKeySpec = new SecretKeySpec(clientEnCodeFormat, AES);
            cipher = Cipher.getInstance(AES);
            if (isEncrypt) {
                cipher.init(Cipher.ENCRYPT_MODE, clientKeySpec);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, clientKeySpec);
            }
        } catch (Exception e) {
            log.error("happens error when getCipher", e);
        }
        return cipher;
    }

    public static String decodeServerText(String cipherText) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return decodeText(serverDecryptCipher, cipherText);
    }

    public static String decodeServerText(String cipherText, String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return decodeText(serverDecryptCipher, cipherText, charset);
    }

    public static String encodeServerText(String text) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return encodeText(serverEncryptCipher, text);
    }

    public static String encodeServerText(String text, String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return encodeText(serverEncryptCipher, text, charset);
    }

    public static String decodeClientText(String cipherText) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return decodeText(clientDecryptCipher, cipherText);
    }

    public static String decodeClientText(String cipherText, String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return decodeText(clientDecryptCipher, cipherText, charset);
    }

    public static String encodeClientText(String text) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return encodeText(clientEncryptCipher, text);
    }

    public static String decodeClientTextWithSeed(String cipherText, String clientKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = getCipher(clientKey, false);
        return decodeText(cipher, cipherText);
    }

    public static String encodeClientTextWithSeed(String text, String clientKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = getCipher(clientKey, true);
        return encodeText(cipher, text);
    }

    public static String encodeClientText(String text, String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return encodeText(clientEncryptCipher, text, charset);
    }

    public static String decodeCookieText(String cipherText) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return decodeText(cookieDecryptCipher, cipherText);
    }

    public static String decodeCookieText(String cipherText, String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return decodeText(cookieDecryptCipher, cipherText, charset);
    }

    public static String encodeCookieText(String text) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return encodeText(cookieEncryptCipher, text);
    }

    public static String encodeCookieText(String text, String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return encodeText(cookieEncryptCipher, text, charset);
    }

    public static String decodeText(Cipher cipher, String cipherText) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return decodeText(cipher, cipherText, null);
    }

    public static String decodeText(Cipher cipher, String cipherText, String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        if (cipherText == null) {
            return null;
        }
        byte[] resultBytes = null;
        byte[] byteContent = hex2Bytes(cipherText);
        resultBytes = cipher.doFinal(byteContent);
        if (StringUtils.isBlank(charset)) {
            return new String(resultBytes);
        } else {
            return new String(resultBytes, charset);
        }

    }

    public static String encodeText(Cipher cipher, String text) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        return encodeText(cipher, text, null);
    }

    public static String encodeText(Cipher cipher, String text, String charset) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        if (text == null) {
            return null;
        }
        byte[] resultBytes = null;
        String result = null;
        byte[] byteContent = null;
        if (StringUtils.isBlank(charset)) {
            byteContent = text.getBytes();
        } else {
            byteContent = text.getBytes(charset);
        }
        resultBytes = cipher.doFinal(byteContent);
        result = bytes2Hex(resultBytes);
        return result;
    }

    /**
     * 字符转换成16进制
     *
     * @param bytes
     * @return
     */
    public static String bytes2Hex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        String tmp = "";
        for (int n = 0; n < bytes.length; n++) {
            tmp = (java.lang.Integer.toHexString(bytes[n] & 0XFF));
            if (tmp.length() == 1) {
                result.append("0").append(tmp);
            } else {
                result.append(tmp);
            }
        }
        return result.toString().toUpperCase();
    }

    /**
     * 16进制转换成字符
     *
     * @param hexStr
     * @return
     */
    public static byte[] hex2Bytes(String hexStr) {
        byte[] b = hexStr.getBytes();
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("length need to be even");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte)Integer.parseInt(item, 16);
        }
        return b2;
    }
}
