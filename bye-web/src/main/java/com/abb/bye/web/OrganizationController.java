package com.abb.bye.web;

import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.domain.UserRelationDO;
import com.abb.bye.client.domain.enums.UserRelationType;
import com.abb.bye.client.service.UserRelationService;
import com.abb.bye.client.service.UserService;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
    String parent(Model model, HttpServletRequest request, @RequestParam Long userId, @RequestParam String parentIds) {
        String vm = "admin/edit_parent";
        UserDTO userDTO = userService.getById(userId).getData();
        if (StringUtils.isNotBlank(parentIds)) {
            
        }
        List<UserRelationDO> relations = userRelationService.getByUserId(UserRelationType.PARENT, userId).getData();
        List<Long> userIds = new ArrayList<>(relations.size());
        relations.forEach(r -> userIds.add(r.getRefId()));
        List<UserDTO> userDTOS = userService.mGet(userIds).getData();
        model.addAttribute("user", userDTO);
        model.addAttribute("parents", userDTOS);
        model.addAttribute("parentIds", Joiner.on(",").join(userIds));
        return vm;
    }

}
