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
    public static String TASK_SKIP_ENABLE = "_FLOWABLE_SKIP_EXPRESSION_ENABLED";
    public static String TASK_ASSIGNEE = "assignee";
    public static String TASK_USER_ID = "user_id";
    public static String TASK_USER_NAME = "user_name";
    public static String TASK_SKIP = "skip";
    public static String TASK_PASS = "pass";
    public static String TASK_TITLE = "title";
    public static String TASK_DESCRIPTION = "description";

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
