package com.abb.bye.web;

import com.abb.bye.client.domain.*;
import com.abb.bye.client.domain.enums.UserRelationType;
import com.abb.bye.client.service.UserAuthorityService;
import com.abb.bye.client.service.UserRelationService;
import com.abb.bye.client.service.UserService;
import com.abb.bye.utils.LoginUtil;
import com.alibaba.boot.velocity.annotation.VelocityLayout;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
    @Resource
    private UserRelationService userRelationService;

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
    String logout(Model model, @RequestParam(required = false) String name, @RequestParam(required = false) String password, HttpServletRequest request, HttpServletResponse response) {
        LoginUtil.removeCookie(null, request, response);
        return "redirect:/";
    }

    @RequestMapping(value = "users.htm", method = {RequestMethod.POST, RequestMethod.GET})
    String users(Model model, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize, HttpServletRequest request, HttpServletResponse response) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        ResultDTO<List<UserDTO>> resultDTO = userService.list((page - 1) * pageSize, pageSize, true);
        Map<Long, UserVO> userMapping = new LinkedHashMap<>(32);
        List<UserVO> userVOS = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        resultDTO.getData().forEach(u -> {
            UserVO uv = new UserVO(u);
            userMapping.put(u.getUserId(), uv);
            userIds.add(u.getUserId());
            userVOS.add(uv);
        });

        List<UserRelationDO> relations = userRelationService.mGetByUserIds(UserRelationType.PARENT, userIds).getData();
        Set<Long> refIds = new HashSet<>();
        relations.forEach(r -> {
            UserVO userVO = userMapping.get(r.getUserId());
            userVO.addParentId(r.getRefId());
            refIds.add(r.getRefId());
        });
        List<UserDTO> parents = userService.mGet(new ArrayList<>(refIds)).getData();
        Map<Long, UserDTO> parentMapping = new HashMap<>(32);
        parents.forEach(p -> parentMapping.put(p.getUserId(), p));

        userMapping.forEach((userId, userVO) -> {
            if (userVO.getParentIds() != null) {
                userVO.getParentIds().forEach(pid -> {
                    UserDTO parent = parentMapping.get(pid);
                    if (parent != null) {
                        userVO.addParent(parent);
                    }
                });
            }
        });

        Paging<UserVO> paging = new Paging().setCurrentPage(page).setPageSize(pageSize).setTotalData(resultDTO.getTotal()).setData(userVOS).build();
        model.addAttribute("paging", paging);
        return "admin/users";
    }

    public static class UserVO {
        private UserDTO user;
        private List<Long> parentIds;
        private List<UserDTO> parents;

        public List<Long> getParentIds() {
            return parentIds;
        }

        public void setParentIds(List<Long> parentIds) {
            this.parentIds = parentIds;
        }

        public UserVO(UserDTO user) {
            this.user = user;
        }

        public UserDTO getUser() {
            return user;
        }

        public List<UserDTO> getParents() {
            return parents;
        }

        public void setParents(List<UserDTO> parents) {
            this.parents = parents;
        }

        public void addParentId(long parentId) {
            if (parentIds == null) {
                parentIds = new ArrayList<>();
            }
            parentIds.add(parentId);
        }

        public void addParent(UserDTO userDTO) {
            if (parents == null) {
                parents = new ArrayList<>();
            }
            parents.add(userDTO);
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
