package com.abb.bye.client.flow;

/**
 * @author cenpeng.lwm
 * @since 2019/6/11
 */
public class TestForm {
    @Field(name = "days", label = "天数")
    private Integer days;
    @Field(name = "description", label = "理由")
    private String description;
}
