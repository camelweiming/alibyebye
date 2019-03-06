package com.abb.bye.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author cenpeng.lwm
 * @since 2018/9/21
 */
public class Tracer {
    private static final Logger logger = LoggerFactory.getLogger("trace");
    private static final Logger errorLog = LoggerFactory.getLogger("T");
    public static String SERVER_IP;
    /**
     * 关键属性,可按此选项过滤
     */
    private String nameSpace;
    /**
     * 关键属性,orderId或者其他ID,可按此选项过滤
     */
    private Object entityId;

    static {
        try {
            InetAddress address = InetAddress.getLocalHost();
            SERVER_IP = address.getHostAddress();
        } catch (UnknownHostException e) {
            SERVER_IP = "0.0.0.0";
        }
    }

    public Tracer(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public Tracer setEntityId(Object entityId) {
        this.entityId = entityId;
        return this;
    }

    public final void trace(String msg) {
        trace(msg, false, null);
    }

    public final void trace(String msg, boolean alarm) {
        trace(msg, alarm, null);
    }

    public final void trace(String msg, Throwable e) {
        trace(msg, false, e);
    }

    public final void trace(String msg, boolean alarm, Throwable e) {
        StringBuilder sb = new StringBuilder(alarm ? "|ALARM" : "|RECORD");
        sb.append("|").append(nameSpace);
        sb.append("|").append(entityId == null ? 0 : entityId);
        sb.append("|").append(SERVER_IP);
        sb.append("|").append(format(msg));
        if (e != null) {
            sb.append("|").append(toString(e, 8));
        }
        logger.warn(sb.toString());
        if (alarm) {
            errorLog.error(msg, e);
        }
    }

    public static String format(String e) {
        if (e == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = e.toCharArray();
        for (char c : chars) {
            if (c == '\n' || c == '\r' || c == '\t') {
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String toString(Throwable e, int depth) {
        StackTraceElement[] elements = e.getStackTrace();
        StringBuilder sb = new StringBuilder(1024);
        sb.append(Tracer.format(e.toString()));
        if (elements == null) {
            return sb.toString();
        }
        int i = 0;
        for (StackTraceElement ele : elements) {
            if (i++ > depth) {
                break;
            }
            sb.append(" ").append(Tracer.format(ele.toString()));
        }
        return sb.toString();
    }

    public static String toString(Throwable e) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(os, true));
            String str = IOUtils.toString(os.toByteArray(), "UTF-8");
            str = format(str);
            return str;
        } catch (Throwable e2) {
            return "EEE:" + e2.getMessage();
        }
    }

    public static void main(String[] args) {
        try {
            String str = null;
            str.length();

        } catch (Throwable e) {
            new Tracer("").trace("sss", e);
        }
    }
}
