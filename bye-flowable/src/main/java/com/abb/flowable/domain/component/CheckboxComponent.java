package com.abb.flowable.domain.component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public class CheckboxComponent extends Component {
    private List<ComponentOption> options;

    public CheckboxComponent() {
        super("checkbox");
    }

    public List<ComponentOption> getOptions() {
        return options;
    }

    public void setOptions(List<ComponentOption> options) {
        this.options = options;
    }

    public CheckboxComponent addOption(ComponentOption option) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(option);
        return this;
    }
}
