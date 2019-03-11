package com.abb.bye.spider;

import com.abb.bye.client.spider.SpiderProcessor;
import org.junit.Test;

/**
 * @author cenpeng.lwm
 * @since 2019/3/11
 */
public class DouBanTest extends BaseSpiderTest {
    private SpiderProcessor processor = new DouBanProcessor();

    @Override
    SpiderProcessor getProcessor() {
        return processor;
    }

    @Test
    public void test() {
        run("https://movie.douban.com/subject/26928226/");
    }
}
