package com.abb.bye.client.flow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author cenpeng.lwm
 * @since 2019/6/11
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
    String type() default "input";

    String name();

    String label() default "";

    boolean required() default false;

    boolean readonly() default false;

    boolean persistence() default false;

}
