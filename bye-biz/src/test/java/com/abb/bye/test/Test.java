package com.abb.bye.test;

import com.abb.bye.utils.Tracer;
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
        logger.info("xxxxxxxxx");
        Tracer tracer = new Tracer("xxxxx");
        tracer.trace("xxdddg");
    }
}
