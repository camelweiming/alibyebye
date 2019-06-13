package com.abb.flowable.domain.component;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public class HiddenComponent extends Component {
    private String value;
    public HiddenComponent() {
        super("hidden");
    }

    public String getValue() {
        return value;
    }

    public HiddenComponent setValue(String value) {
        this.value = value;
        return this;
    }
}
