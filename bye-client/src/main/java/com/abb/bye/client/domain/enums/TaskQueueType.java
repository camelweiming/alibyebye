package com.abb.bye.client.domain.enums;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
public enum TaskQueueType {
    SYS_LOCK(0, "system_lock", 3),
    DEMO(1, "demo", 3);
    private int type;
    private String name;
    private int executeTimeoutSeconds = 10;

    TaskQueueType(int type, String name, int executeTimeoutSeconds) {
        this.type = type;
        this.name = name;
        this.executeTimeoutSeconds = executeTimeoutSeconds;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getExecuteTimeoutSeconds() {
        return executeTimeoutSeconds;
    }

    public static TaskQueueType getByType(int type) {
        for (TaskQueueType t : values()) {
            if (t.type == type) {
                return t;
            }
        }
        throw new RuntimeException("Type not defined :" + type);
    }
}
