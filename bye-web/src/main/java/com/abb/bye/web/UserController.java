package com.abb.bye.web;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserAuthorityDTO;
import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.service.UserAuthorityService;
import com.abb.bye.client.service.UserService;
import com.abb.bye.utils.LoginUtil;
import com.alibaba.boot.velocity.annotation.VelocityLayout;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserAuthorityService userAuthorityService;
    @Resource
    private UserService userService;

    @RequestMapping(value = "sign_in.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/layout_login.vm")
    String signIn(Model model, @RequestParam(required = false) String name, @RequestParam(required = false) String password, HttpServletRequest request, HttpServletResponse response) {
        String vm = "sign_in";
        try {
            model.addAttribute("name", name);
            model.addAttribute("password", password);
            if (StringUtils.isBlank(name)) {
                return vm;
            }
            if (StringUtils.isBlank(password)) {
                model.addAttribute("errorMsg", "密码不能为空");
            }
            UserAuthorityDTO userAuthorityDTO = new UserAuthorityDTO();
            userAuthorityDTO.setName(name);
            userAuthorityDTO.setPassword(password);
            ResultDTO<Long> res = userAuthorityService.register(userAuthorityDTO);
            logger.info("register:" + userAuthorityDTO.getName() + " res:" + res);
            if (!res.isSuccess()) {
                model.addAttribute("errorMsg", res.getErrMsg());
                return vm;
            }
            UserDTO user = userService.getById(res.getData()).getData();
            LoginUtil.setLoginCookie(null, user.getUserName(), 3600, request, response);
            return "redirect:/";
        } catch (Throwable e) {
            logger.error("Error signIn", e);
            model.addAttribute("errorMsg", "system error");
            return vm;
        }
    }

    @RequestMapping(value = "login.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/layout_login.vm")
    String login(Model model, @RequestParam(required = false) String name, @RequestParam(required = false) String password, HttpServletRequest request, HttpServletResponse response) {
        String vm = "login";
        try {
            model.addAttribute("name", name);
            model.addAttribute("password", password);
            if (StringUtils.isBlank(name)) {
                return vm;
            }
            if (StringUtils.isBlank(password)) {
                model.addAttribute("errorMsg", "密码不能为空");
            }
            UserAuthorityDTO userAuthorityDTO = new UserAuthorityDTO();
            userAuthorityDTO.setName(name);
            userAuthorityDTO.setPassword(password);
            ResultDTO<Long> res = userAuthorityService.verify(userAuthorityDTO);
            logger.info("register:" + userAuthorityDTO.getName() + " res:" + res);
            if (!res.isSuccess()) {
                model.addAttribute("errorMsg", res.getErrMsg());
                return vm;
            }
            UserDTO user = userService.getById(res.getData()).getData();
            LoginUtil.setLoginCookie(null, user.getUserName(), 3600, request, response);
            return "redirect:/";
        } catch (Throwable e) {
            logger.error("Error signIn", e);
            model.addAttribute("errorMsg", "system error");
            return vm;
        }
    }

    @RequestMapping(value = "logout.htm", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/layout_login.vm")
    ModelAndView logout(Model model, @RequestParam(required = false) String name, @RequestParam(required = false) String password, HttpServletRequest request, HttpServletResponse response) {
        LoginUtil.removeCookie(null, request, response);
        return new ModelAndView("redirect:/");
    }
}
