package com.abb.bye.utils;

/**
 * @author cenpeng.lwm
 * @since 2019/6/4
 */
public class Switcher {
    private static String userCacheKeyPrefix = "user_id_";
    private static int userCacheExpiredSeconds = 300;
    private static String userNameCacheKeyPrefix = "user_name_";
    private static int userNameCacheExpiredSeconds = 300;

    public static int getUserCacheExpiredSeconds() {
        return userCacheExpiredSeconds;
    }

    public static void setUserCacheExpiredSeconds(int userCacheExpiredSeconds) {
        Switcher.userCacheExpiredSeconds = userCacheExpiredSeconds;
    }

    public static int getUserNameCacheExpiredSeconds() {
        return userNameCacheExpiredSeconds;
    }

    public static void setUserNameCacheExpiredSeconds(int userNameCacheExpiredSeconds) {
        Switcher.userNameCacheExpiredSeconds = userNameCacheExpiredSeconds;
    }

    public static String getUserCacheKeyPrefix() {
        return userCacheKeyPrefix;
    }

    public static void setUserCacheKeyPrefix(String userCacheKeyPrefix) {
        Switcher.userCacheKeyPrefix = userCacheKeyPrefix;
    }

    public static String getUserNameCacheKeyPrefix() {
        return userNameCacheKeyPrefix;
    }

    public static void setUserNameCacheKeyPrefix(String userNameCacheKeyPrefix) {
        Switcher.userNameCacheKeyPrefix = userNameCacheKeyPrefix;
    }
}
