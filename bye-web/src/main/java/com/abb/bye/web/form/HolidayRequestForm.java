package com.abb.bye.web.form;

import com.abb.bye.client.domain.*;
import com.abb.bye.client.flow.FlowForm;
import com.abb.bye.client.flow.FormObject;
import com.abb.bye.client.flow.component.TextComponent;
import com.abb.bye.client.service.FlowService;
import com.abb.bye.client.service.UserService;
import com.abb.bye.service.SpringCtx;
import com.abb.bye.utils.CommonUtils;
import com.abb.bye.utils.LoginUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/11
 */
@Component("HolidayRequestForm")
public class HolidayRequestForm implements FlowForm {
    private static final Logger logger = LoggerFactory.getLogger(HolidayRequestForm.class);
    private static final String PROCESS_DEFINITION_KEY = "holidayRequest";

    @Override
    public ResultDTO<FormObject> render(HttpServletRequest request) {
        FormObject formObject = new FormObject();
        formObject.addComponent(new TextComponent().setName("days").setLabel("请假天数").setRequired(true));
        formObject.addComponent(new TextComponent().setName("description").setLabel("请假理由").setRequired(true));
        return ResultDTO.buildSuccess(formObject);
    }

    @Override
    public ResultDTO<FormObject> render(Map<String, Object> variables) {
        Integer days = (Integer)variables.get("days");
        String description = (String)variables.get("description");
        FormObject formObject = new FormObject();
        formObject.addComponent(new TextComponent().setValue("" + days).setName("days").setLabel("请假天数"));
        formObject.addComponent(new TextComponent().setValue(description).setName("description").setLabel("请假理由").setRequired(true));
        return ResultDTO.buildSuccess(formObject);
    }

    @Override
    public ResultDTO<Object> post(HttpServletRequest request) {
        Integer days = CommonUtils.toInteger(request.getParameter("days"));
        String description = request.getParameter("description");
        if (days == null || days <= 0) {
            return ResultDTO.buildError("天数不能小于0");
        }
        try {
            UserService userService = SpringCtx.getBean(UserService.class);
            FlowService flowService = SpringCtx.getBean(FlowService.class);
            UserDTO userDTO = userService.getById(LoginUtil.getLoginUserSilent(request), new UserOptions().setWithBoss(true)).getData();
            FlowSubmitDTO flowSubmitDTO = new FlowSubmitDTO();
            flowSubmitDTO.setUserId(userDTO.getUserId());
            flowSubmitDTO.setUserName(userDTO.getUserName());
            flowSubmitDTO.setTitle(String.format("%s申请休假%s天", userDTO.getUserName(), days));
            flowSubmitDTO.addVariable("description", description);
            flowSubmitDTO.addVariable("days", days);
            /**
             * 没有上级则跳过其余审批节点
             */
            UserDTO leader = null;
            if (CollectionUtils.isNotEmpty(userDTO.getBosses())) {
                leader = userDTO.getBosses().get(0);
            }
            if (leader == null) {
                flowSubmitDTO.setSkip(true);
                flowSubmitDTO.setPass(true);
            } else {
                flowSubmitDTO.setAssignee(String.valueOf(leader.getUserId()));
                flowSubmitDTO.setAssigneeName(leader.getUserName());
            }
            ResultDTO<ProcessInstanceDTO> result = flowService.submitProcessor(PROCESS_DEFINITION_KEY, flowSubmitDTO);
            if (!result.isSuccess()) {
                return ResultDTO.buildError(result.getErrCode(), result.getErrMsg());
            }
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            logger.error("Error post", e);
            return ResultDTO.buildError("系统错误");
        }
    }
}