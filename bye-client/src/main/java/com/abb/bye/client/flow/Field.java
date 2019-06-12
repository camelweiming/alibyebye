package com.abb.bye.client.flow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author cenpeng.lwm
 * @since 2019/6/11
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
    /**
     * 表单的类型，checkbox,radio,hidden
     *
     * @return
     */
    String type() default "input";

    /**
     * 表单名称
     *
     * @return
     */
    String name();

    /**
     * 显示名称
     *
     * @return
     */
    String label() default "";

    /**
     * 是否多值，checkbox,radio 为true
     *
     * @return
     */
    boolean multiValue() default false;

    boolean required() default false;

    boolean readonly() default false;

    /**
     * 是否持久化
     *
     * @return
     */
    boolean persistence() default false;

}
