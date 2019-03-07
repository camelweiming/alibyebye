package com.abb.bye.web;

import com.abb.bye.client.service.SpiderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2018/10/2
 */
@Controller
public class CommandController {
    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);
    @Resource
    private SpiderService spiderService;

    @RequestMapping(value = "command.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    String cmd(Model model,
               @RequestParam String cmd,
               @RequestParam(required = false) Integer site
    ) {
        if ("restartSpider".equals(cmd) && site != null) {
            //spiderService.stop(site);
            //spiderService.start(site);
        }
        return "success";
    }
}