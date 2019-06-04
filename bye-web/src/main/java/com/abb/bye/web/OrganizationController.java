package com.abb.bye.web;

import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.domain.UserRelationDO;
import com.abb.bye.client.domain.enums.UserRelationType;
import com.abb.bye.client.service.UserRelationService;
import com.abb.bye.client.service.UserService;
import com.google.common.base.Function;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
@Controller
public class OrganizationController {
    @Resource
    private UserService userService;
    @Resource
    private UserRelationService userRelationService;

    @RequestMapping(value = "edit_parent.htm", method = {RequestMethod.POST, RequestMethod.GET})
    String parent(Model model, HttpServletRequest request, @RequestParam Long userId, @RequestParam(required = false) String parentName, @RequestParam(required = false) List<Long> delIds) {
        String vm = "admin/edit_parent";
        UserDTO userDTO = userService.getById(userId).getData();
        model.addAttribute("user", userDTO);
        if (StringUtils.isNotBlank(parentName)) {
            UserDTO parent = userService.getByName(parentName).getData();
            boolean validate = true;
            if (parent == null) {
                model.addAttribute("errorMsg", "用户不存在");
                validate = false;
            }
            if (userId.equals(parent.getUserId())) {
                model.addAttribute("errorMsg", "不能添加同一个用户");
                validate = false;
            }
            if (validate) {
                UserRelationDO userRelationDO = new UserRelationDO();
                userRelationDO.setRefType(UserRelationType.PARENT.getType());
                userRelationDO.setRefId(parent.getUserId());
                userRelationDO.setUserId(userId);
                userRelationDO.setStatus(UserRelationDO.STATUS_ENABLE);
                userRelationService.insertOrUpdate(userRelationDO);
            }
        }
        if (CollectionUtils.isNotEmpty(delIds)) {
            for (Long refId : delIds) {
                userRelationService.remove(UserRelationType.PARENT, userId, refId);
            }
        }
        List<UserRelationDO> relations = userRelationService.getByUserId(UserRelationType.PARENT, userId).getData();
        List<Long> userIds = relations.stream().map((Function<UserRelationDO, Long>)ur -> ur.getRefId()).collect(Collectors.toList());
        List<UserDTO> userDTOS = userService.mGet(userIds).getData();
        model.addAttribute("parents", userDTOS);
        return vm;
    }
}
