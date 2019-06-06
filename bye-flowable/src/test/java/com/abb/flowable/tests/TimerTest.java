package com.abb.flowable.tests;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/5/28
 */
public class TimerTest extends BaseTest {

    @Test
    public void test() throws InterruptedException, IOException {
        Map<String, Object> variables = new HashMap<>(8);
        Date date = DateUtils.addSeconds(new Date(), 60);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String time = simpleDateFormat.format(date);
        System.out.println("time:" + time);
        variables.put("EndDate", time);
        runtimeService.startProcessInstanceByKey("Timer", variables);
        Thread.sleep(100000);
    }
}
