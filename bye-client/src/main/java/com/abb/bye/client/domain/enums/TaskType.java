package com.abb.bye.client.domain.enums;

/**
 * @author cenpeng.lwm
 * @since 2019/5/27
 */
public enum TaskType {
    HOLIDAY(1, "请假", "approve_holiday.htm");
    private final int type;
    private final String name;
    private final String approveLink;

    TaskType(int type, String name, String approveLink) {
        this.type = type;
        this.name = name;
        this.approveLink = approveLink;
    }

    public String getApproveLink() {
        return approveLink;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static TaskType getByType(int type) {
        for (TaskType t : values()) {
            if (t.type == type) {
                return t;
            }
        }
        throw new RuntimeException("Type not defined :" + type);
    }
}
