package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 7021782933274931573L;
    private Long userId;
    private String userName;
    private List<UserDTO> bosses;

    public List<UserDTO> getBosses() {
        return bosses;
    }

    public void setBosses(List<UserDTO> bosses) {
        this.bosses = bosses;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
