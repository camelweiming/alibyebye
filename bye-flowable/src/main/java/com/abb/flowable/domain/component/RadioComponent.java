package com.abb.flowable.domain.component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public class RadioComponent extends Component {
    private List<ComponentOption> options;

    public RadioComponent() {
        super("radio");
    }

    public List<ComponentOption> getOptions() {
        return options;
    }

    public void setOptions(List<ComponentOption> options) {
        this.options = options;
    }

    public RadioComponent addOption(ComponentOption option) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(option);
        return this;
    }
}
