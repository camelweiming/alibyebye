package com.abb.bye.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author cenpeng.lwm
 * @since 2018/9/12
 */
public class Md5 {
    private static char[] digits = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static Map<Character, Integer> rDigits = new HashMap(16);
    private static Md5 me;
    private MessageDigest mHasher;
    private ReentrantLock opLock = new ReentrantLock();

    private Md5() {
        try {
            this.mHasher = MessageDigest.getInstance("md5");
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    public static Md5 getInstance() {
        return me;
    }

    public String getMD5String(String content) {
        return this.bytes2string(this.hash(content));
    }

    public String getMD5String(byte[] content) {
        return this.bytes2string(this.hash(content));
    }

    public byte[] getMD5Bytes(byte[] content) {
        return this.hash(content);
    }

    public byte[] hash(String str) {
        this.opLock.lock();

        byte[] var3;
        try {
            byte[] bt = this.mHasher.digest(str.getBytes("GBK"));
            if (null == bt || bt.length != 16) {
                throw new IllegalArgumentException("md5 need");
            }

            var3 = bt;
        } catch (UnsupportedEncodingException var7) {
            throw new RuntimeException("unsupported utf-8 encoding", var7);
        } finally {
            this.opLock.unlock();
        }

        return var3;
    }

    public byte[] hash(byte[] data) {
        this.opLock.lock();

        byte[] var3;
        try {
            byte[] bt = this.mHasher.digest(data);
            if (null == bt || bt.length != 16) {
                throw new IllegalArgumentException("md5 need");
            }

            var3 = bt;
        } finally {
            this.opLock.unlock();
        }

        return var3;
    }

    public String bytes2string(byte[] bt) {
        int l = bt.length;
        char[] out = new char[l << 1];
        int i = 0;

        for (int var5 = 0; i < l; ++i) {
            out[var5++] = digits[(240 & bt[i]) >>> 4];
            out[var5++] = digits[15 & bt[i]];
        }

        return new String(out);
    }

    public byte[] string2bytes(String str) {
        if (null == str) {
            throw new NullPointerException("");
        } else if (str.length() != 32) {
            throw new IllegalArgumentException("");
        } else {
            byte[] data = new byte[16];
            char[] chs = str.toCharArray();

            for (int i = 0; i < 16; ++i) {
                int h = rDigits.get(chs[i * 2]);
                int l = rDigits.get(chs[i * 2 + 1]);
                data[i] = (byte)((h & 15) << 4 | l & 15);
            }

            return data;
        }
    }

    static {
        for (int i = 0; i < digits.length; ++i) {
            rDigits.put(digits[i], i);
        }

        me = new Md5();
    }

    public static void main(String[] args) {
        System.out.println(Md5.getInstance().getMD5String("1000479526779_加上无线同屏和优酷会员看电影很棒"));
    }
}
