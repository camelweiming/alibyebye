package com.abb.bye;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public class Constants {
    public static final String SYSTEM_CONFIG_PREFIX = "SYSTEM_";
    public static final String SYSTEM_CONFIG_SPIDER_URLS = SYSTEM_CONFIG_PREFIX + "SPIDER_URLS";
    public static final String SYSTEM_CONFIG_SPIDER_CONFIG = SYSTEM_CONFIG_PREFIX + "SPIDER_CONFIG";
    public static final String SYSTEM_CONFIG_SPIDER_SCHEDULE = SYSTEM_CONFIG_PREFIX + "SPIDER_SCHEDULE";
    public static final String SPIDER_PROGRAMME_FIELD_NAME = "PROGRAMME_SOURCE";
    public static final String SCHEDULE_SPIDER_PREFIX = "site_spider_";
    public static final int MAX_SOURCE_ID = 60;
    public static final String MD5_SOURCE_ID_PREFIX = "@";
    public static String SERVER_IP;
    public static String SERVER_NAME;

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
