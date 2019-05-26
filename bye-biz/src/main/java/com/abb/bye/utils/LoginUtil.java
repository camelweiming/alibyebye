package com.abb.bye.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
public class LoginUtil {
    private static String clientKey = "test";
    public static final String ENCODING_UTF8 = "UTF-8";
    private static Cipher clientCookieEncryptCipher;
    private static Cipher clientCookieDecryptCipher;

    static {
        clientCookieEncryptCipher = EncodeUtil.getCipher(clientKey, true);
        clientCookieDecryptCipher = EncodeUtil.getCipher(clientKey, false);
    }

    public static String encodeCookie(String value) throws InvalidKeyException, IllegalBlockSizeException,
        BadPaddingException, UnsupportedEncodingException {
        return EncodeUtil.encodeText(clientCookieEncryptCipher, value, ENCODING_UTF8);
    }

    public static String decodeCookie(String value) throws InvalidKeyException, IllegalBlockSizeException,
        BadPaddingException, UnsupportedEncodingException {
        return EncodeUtil.decodeText(clientCookieDecryptCipher, value, ENCODING_UTF8);
    }

    public static void main(String[] args) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        String c = encodeCookie("xxxxx");
        System.out.println(c);
        System.out.println(decodeCookie(c));
    }
}
