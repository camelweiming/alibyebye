package com.abb.bye.client.flow;

/**
 * @author cenpeng.lwm
 * @since 2019/6/11
 */
public @interface Field {
    String type() default "input";

    String name();

    String label();

    boolean required() default false;

    boolean readonly() default false;

    boolean persistence() default false;

}
