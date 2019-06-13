package com.abb.flowable.domain.component;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public class TextComponent extends Component {
    private String value;

    public TextComponent() {
        super("text");
    }

    public String getValue() {
        return value;
    }

    public TextComponent setValue(String value) {
        this.value = value;
        return this;
    }
}
