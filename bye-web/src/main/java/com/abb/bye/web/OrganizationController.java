package com.abb.bye.web;

import com.abb.bye.client.service.UserService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
@Controller
public class OrganizationController {
    @Resource
    private UserService userService;


}
