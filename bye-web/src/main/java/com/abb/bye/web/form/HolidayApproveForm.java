package com.abb.bye.web.form;

import com.abb.bye.client.domain.FlowCompleteDTO;
import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.domain.UserOptions;
import com.abb.bye.client.flow.FlowForm;
import com.abb.bye.client.flow.FormObject;
import com.abb.bye.client.flow.component.ComponentOption;
import com.abb.bye.client.flow.component.RadioComponent;
import com.abb.bye.client.flow.component.TextComponent;
import com.abb.bye.client.service.FlowService;
import com.abb.bye.client.service.UserService;
import com.abb.bye.service.SpringCtx;
import com.abb.bye.utils.CommonUtils;
import com.abb.bye.utils.LoginUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/12
 */
@Component("HolidayApproveForm")
public class HolidayApproveForm implements FlowForm {

    @Override
    public ResultDTO<FormObject> render(HttpServletRequest request) {
        FormObject formObject = new FormObject();
        RadioComponent approve = (RadioComponent)new RadioComponent().setName("approve").setLabel("审批").setRequired(true);
        approve.addOption(new ComponentOption("请选择", "-1"));
        approve.addOption(new ComponentOption("通过", "1"));
        approve.addOption(new ComponentOption("驳回", "2"));
        formObject.addComponent(approve);
        formObject.addComponent(new TextComponent().setName("description").setLabel("理由"));
        /**
         * 加签用户列表
         */
        Long loginUserId = LoginUtil.getLoginUserSilent(request);
        UserService userService = SpringCtx.getBean(UserService.class);
        UserDTO userDTO = userService.getById(loginUserId, new UserOptions().setWithBoss(true)).getData();
        if (userDTO.getBosses() != null) {
            RadioComponent leaders = (RadioComponent)new RadioComponent().setName("confirmUser").setLabel("加签");
            userDTO.getBosses().forEach(boss -> leaders.addOption(new ComponentOption(boss.getUserName(), "" + boss.getUserId())));
            formObject.addComponent(leaders);
        }
        return ResultDTO.buildSuccess(formObject);
    }

    @Override
    public ResultDTO<FormObject> render(Map<String, Object> variables) {
        FormObject formObject = new FormObject();
        String confirmUserName = (String)variables.get("confirmUserName");
        String description = (String)variables.get("description");
        if (confirmUserName != null) {
            formObject.addComponent(new TextComponent().setValue(confirmUserName).setLabel("加签"));
        }
        if (description != null) {
            formObject.addComponent(new TextComponent().setValue(description).setLabel("理由"));
        }
        return ResultDTO.buildSuccess(formObject);
    }

    @Override
    public ResultDTO<Object> post(HttpServletRequest request) {
        Integer approve = CommonUtils.toInteger(request.getParameter("approve"));
        Long confirmUser = CommonUtils.toLong(request.getParameter("confirmUser"));
        String description = request.getParameter("description");
        if (confirmUser != null && confirmUser < 0) {
            confirmUser = null;
        }
        String taskId = request.getParameter("taskId");
        if (approve == null || taskId == null) {
            return ResultDTO.buildError("参数错误");
        }
        FlowCompleteDTO submitDTO = new FlowCompleteDTO();
        UserService userService = SpringCtx.getBean(UserService.class);
        FlowService flowService = SpringCtx.getBean(FlowService.class);
        Long loginUserId = LoginUtil.getLoginUserSilent(request);
        UserDTO userDTO = userService.getById(loginUserId, new UserOptions()).getData();
        submitDTO.setUserId(userDTO.getUserId());
        submitDTO.setUserName(userDTO.getUserName());
        boolean pass = approve == 1;
        /**
         * 如果驳回或没有加签，则跳过其余审批节点
         */
        if (!pass || confirmUser == null) {
            submitDTO.setSkip(true);
        } else {
            submitDTO.setAssignee(String.valueOf(confirmUser));
            submitDTO.setAssigneeName(userService.getById(confirmUser, new UserOptions()).getData().getUserName());
        }
        submitDTO.setPass(true);
        if (description != null) {
            submitDTO.addTaskVariables("description", description);
        }
        if (confirmUser != null) {
            UserDTO confirmUserDTO = userService.getById(confirmUser, new UserOptions()).getData();
            submitDTO.addTaskVariables("confirmUser", confirmUser);
            submitDTO.addTaskVariables("confirmUserName", confirmUserDTO.getUserName());
        }
        ResultDTO<Void> result = flowService.complete(taskId, submitDTO);
        if (!result.isSuccess()) {
            return ResultDTO.buildError(result.getErrCode(), result.getErrMsg());
        }
        return ResultDTO.buildSuccess(true);
    }
}
