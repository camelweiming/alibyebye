package com.abb.bye;

/**
 * @author cenpeng.lwm
 * @since 2019/3/8
 */
public class Switcher {
    private static boolean debug = true;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Switcher.debug = debug;
    }
}
