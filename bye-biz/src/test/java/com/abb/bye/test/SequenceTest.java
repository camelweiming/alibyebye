package com.abb.bye.test;

import com.abb.bye.service.Sequence;
import com.abb.bye.test.dao.BaseDAOTest;
import com.google.common.collect.Sets;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author cenpeng.lwm
 * @since 2019/3/21
 */
public class SequenceTest extends BaseDAOTest {
    @Resource
    private Sequence sequence;

    @Test
    public void test() throws InterruptedException {
        Set<Long> set = Sets.newConcurrentHashSet();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    long value = sequence.next("task_queue");
                    System.out.println(value);
                    if (!set.add(value)) {
                        throw new IllegalArgumentException("dup:" + value);
                    }
                }
            }).start();
        }
        Thread.sleep(3000);
        //long current = 0;
        //for (int i = 0; i < 2000; i++) {
        //
        //    if (current != 0) {
        //        Assert.assertEquals(value, current + 1);
        //    }
        //    current = value;
        //}
    }
}
