package com.abb.bye.spider;

import java.util.regex.Pattern;

/**
 * @author cenpeng.lwm
 * @since 2019/3/11
 */
public class DynaPageExpression {
    private static Pattern LIST_PATTERN = Pattern.compile("\\[(\\d+)->(\\d+)\\]");

}
