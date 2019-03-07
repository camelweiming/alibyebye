package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author cenpeng.lwm
 * @since 2019/3/7
 */
public class ResultDTO<T> implements Serializable {
    private static final long serialVersionUID = 4196449326985418273L;
    public static final int ERROR_CODE_SYSTEM_ERROR = 500;
    private boolean success;
    private int errCode;
    private String errMsg;
    private int total;
    private T data;

    public ResultDTO(boolean success) {
        this.success = success;
    }

    public ResultDTO(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public static <T> ResultDTO<T> buildSuccess(T data) {
        ResultDTO<T> resultDTO = new ResultDTO<T>(true);
        resultDTO.data = data;
        return resultDTO;
    }

    public static <T> ResultDTO<T> buildSuccess(T data, int total) {
        ResultDTO<T> resultDTO = new ResultDTO<T>(true);
        resultDTO.data = data;
        resultDTO.total = total;
        return resultDTO;
    }

    public static <T> ResultDTO<T> buildError(String errMsg) {
        ResultDTO<T> resultDTO = new ResultDTO<T>(false);
        resultDTO.errCode = ERROR_CODE_SYSTEM_ERROR;
        resultDTO.errMsg = errMsg;
        return resultDTO;
    }

    public ResultDTO<T> setErrCode(int errCode) {
        this.errCode = errCode;
        return this;
    }

    public ResultDTO<T> setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

    public ResultDTO<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public ResultDTO<T> setTotal(int total) {
        this.total = total;
        return this;
    }

    public ResultDTO<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public int getTotal() {
        return total;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
