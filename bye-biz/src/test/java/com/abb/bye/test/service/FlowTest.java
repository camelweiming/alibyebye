package com.abb.bye.test.service;

import com.abb.flowable.domain.TaskQuery;
import com.abb.flowable.service.FlowService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/6/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(locations = {"classpath:/beans/beans-flowable-support.xml", "classpath:/beans/beans-persistence.xml"})
public class FlowTest {
    @Resource
    private FlowService flowService;

    @Test
    public void test() {
        //flowService.query(new TaskQuery().setUserId())
    }
}
