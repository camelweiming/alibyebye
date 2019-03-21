package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/21
 */
public class TreeNode<T> {
    private final T data;
    private List<TreeNode<T>> children = new ArrayList<>();

    public TreeNode(T data) {
        this.data = data;
    }

    public TreeNode<T> add(TreeNode<T> t) {
        children.add(t);
        return this;
    }

    public T getData() {
        return data;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
