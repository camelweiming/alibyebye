package com.abb.bye.test.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserAuthorityDTO;
import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.domain.UserOptions;
import com.abb.bye.client.service.UserAuthorityService;
import com.abb.bye.client.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/5/25
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(locations = {"classpath:application-context-users.xml"})
public class UserServiceTest {
    @Resource
    private UserAuthorityService userAuthorityService;
    @Resource
    private UserService userService;

    @Test
    public void testGet() {
        ResultDTO<UserDTO> res = userService.getById(6001, new UserOptions());
        System.out.println(res);
    }

    @Test
    public void testInsert() {
        UserAuthorityDTO u = new UserAuthorityDTO();
        u.setName("camel8");
        u.setPassword("ssss");
        ResultDTO<Long> res = userAuthorityService.register(u);
        System.out.println(res);
    }

    @Test
    public void testValidate() {
        UserAuthorityDTO u = new UserAuthorityDTO();
        u.setName("camel6");
        u.setPassword("ssss3");
        ResultDTO<Long> res = userAuthorityService.verify(u);
        System.out.println(res);
    }
}
