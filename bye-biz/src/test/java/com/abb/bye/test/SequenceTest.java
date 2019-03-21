package com.abb.bye.test;

import com.abb.bye.client.service.SequenceService;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/3/21
 */
public class SequenceTest extends BaseDAOTest {
    @Resource
    private SequenceService sequenceService;

    @Test
    public void test() {
        long current = 0;
        for (int i = 0; i < 2000; i++) {
            long value = sequenceService.next("task_queue");
            if (current != 0) {
                Assert.assertEquals(value, current + 1);
            }
            current = value;
        }
    }
}
