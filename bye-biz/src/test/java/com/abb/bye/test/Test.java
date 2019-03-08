package com.abb.bye.test;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author camelweiming@163.com
 * @since 2018/6/7
 */
public class Test {
    static Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        DateTime dateTime = DateTime.now().plusSeconds(-3600);
        System.out.println(dateTime.toString("yyyy-MM-dd hh:mm:ss"));

    }
}
