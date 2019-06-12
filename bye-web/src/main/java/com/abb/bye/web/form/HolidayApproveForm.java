package com.abb.bye.web.form;

import com.abb.bye.client.domain.FlowCompleteDTO;
import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.domain.UserOptions;
import com.abb.bye.client.flow.Field;
import com.abb.bye.client.flow.Form;
import com.abb.bye.client.flow.FormFieldOption;
import com.abb.bye.client.service.FlowService;
import com.abb.bye.client.service.UserService;
import com.abb.bye.service.SpringCtx;
import com.abb.bye.utils.CommonUtils;
import com.abb.bye.utils.LoginUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/12
 */
public class HolidayApproveForm implements Form {
    @Field(name = "approve", label = "审批", type = "radio", multiValue = true, required = true, persistence = true)
    private List<FormFieldOption> approve;
    @Field(name = "description", label = "理由", persistence = true)
    private String description;
    @Field(name = "confirmUser", label = "加签", type = "radio", multiValue = true)
    private List<FormFieldOption> leaders;

    @Override
    public ResultDTO<Object> render(HttpServletRequest request) {
        approve = new ArrayList<>();
        approve.add(new FormFieldOption("请选择", "-1"));
        approve.add(new FormFieldOption("通过", "1"));
        approve.add(new FormFieldOption("驳回", "2"));
        Long loginUserId = LoginUtil.getLoginUserSilent(request);
        UserService userService = SpringCtx.getBean(UserService.class);
        /**
         * 加签用户列表
         */
        UserDTO userDTO = userService.getById(loginUserId, new UserOptions().setWithBoss(true)).getData();
        if (userDTO.getBosses() != null) {
            leaders = new ArrayList<>();
            leaders.add(new FormFieldOption("请选择", "-1"));
            userDTO.getBosses().forEach(boss -> leaders.add(new FormFieldOption(boss.getUserName(), "" + boss.getUserId())));
        }
        return ResultDTO.buildSuccess(null);
    }

    @Override
    public ResultDTO<Object> render(Map<String, Object> variables) {
        return null;
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
            submitDTO.addVariable("description", description);
        }
        if (confirmUser != null) {
            submitDTO.addVariable("confirmUser", confirmUser);
        }
        ResultDTO<Void> result = flowService.complete(taskId, submitDTO);
        if (!result.isSuccess()) {
            return ResultDTO.buildError(result.getErrCode(), result.getErrMsg());
        }
        return ResultDTO.buildSuccess(true);
    }

    public List<FormFieldOption> getApprove() {
        return approve;
    }

    public void setApprove(List<FormFieldOption> approve) {
        this.approve = approve;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FormFieldOption> getLeaders() {
        return leaders;
    }

    public void setLeaders(List<FormFieldOption> leaders) {
        this.leaders = leaders;
    }
}
