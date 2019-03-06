package com.abb.bye.web;

import com.alibaba.boot.velocity.annotation.VelocityLayout;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author cenpeng.lwm
 * @since 2018/9/4
 */
@Controller
public class MainController {
    @GetMapping("/")
    @VelocityLayout("/velocity/layout/index.vm")
    public String root() {
        return "index";
    }
}
