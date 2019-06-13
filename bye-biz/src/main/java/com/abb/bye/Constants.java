package com.abb.bye;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public class Constants {
    public static String SERVER_IP;
    public static String SERVER_NAME;
    public static String REQUEST_CXT_LOGIN_USER_ID = "CXT_LOGIN_USER_ID";

    static {
        try {
            InetAddress address = InetAddress.getLocalHost();
            SERVER_IP = address.getHostAddress();
            SERVER_NAME = address.getHostName();
        } catch (UnknownHostException e) {
            SERVER_IP = "0.0.0.0";
            SERVER_NAME = "UNKNOWN";
        }
    }
}
