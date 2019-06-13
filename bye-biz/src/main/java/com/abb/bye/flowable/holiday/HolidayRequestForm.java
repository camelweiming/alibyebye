package com.abb.bye.flowable.holiday;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.domain.UserOptions;
import com.abb.bye.client.service.UserService;
import com.abb.bye.service.SpringCtx;
import com.abb.bye.utils.CommonUtils;
import com.abb.flowable.domain.*;
import com.abb.flowable.domain.component.TextComponent;
import com.abb.flowable.service.FlowService;
import com.abb.flowable.service.Form;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/11
 */
@Component("HolidayRequestForm")
public class HolidayRequestForm implements Form {
    private static final Logger logger = LoggerFactory.getLogger(HolidayRequestForm.class);
    private static final String PROCESS_DEFINITION_KEY = "holidayRequest";

    @Override
    public ResultDTO<ComponentForm> render(FormRequest request) {
        ComponentForm componentForm = new ComponentForm();
        componentForm.addComponent(new TextComponent().setName("days").setLabel("请假天数").setRequired(true));
        componentForm.addComponent(new TextComponent().setName("description").setLabel("请假理由").setRequired(true));
        return ResultDTO.buildSuccess(componentForm);
    }

    @Override
    public ResultDTO<ComponentForm> render(Map<String, Object> variables) {
        Integer days = (Integer)variables.get("days");
        String description = (String)variables.get("description");
        ComponentForm componentForm = new ComponentForm();
        componentForm.addComponent(new TextComponent().setValue("" + days).setName("days").setLabel("请假天数"));
        componentForm.addComponent(new TextComponent().setValue(description).setName("description").setLabel("请假理由").setRequired(true));
        return ResultDTO.buildSuccess(componentForm);
    }

    @Override
    public ResultDTO<Object> post(FormRequest request) {
        Integer days = CommonUtils.toInteger(request.getParameter("days"));
        String description = request.getParameter("description");
        if (days == null || days <= 0) {
            return ResultDTO.buildError("天数不能小于0");
        }
        try {
            UserService userService = SpringCtx.getBean(UserService.class);
            FlowService flowService = SpringCtx.getBean(FlowService.class);
            Long loginUserId = (Long)request.getContextValue(Constants.REQUEST_CXT_LOGIN_USER_ID);
            if (loginUserId == null) {
                return ResultDTO.buildError(ResultDTO.ERROR_CODE_USER_NOT_LOGIN, "user not login");
            }
            UserDTO userDTO = userService.getById(loginUserId, new UserOptions().setWithBoss(true)).getData();
            SubmitDTO flowSubmitDTO = new SubmitDTO();
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
