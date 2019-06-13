package com.abb.flowable.domain.component;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public class TextAreaComponent extends Component {
    private String value;
    private int cols;
    private int rows;

    public TextAreaComponent() {
        super("textarea");
    }

    public String getValue() {
        return value;
    }

    public TextAreaComponent setValue(String value) {
        this.value = value;
        return this;
    }

    public int getCols() {
        return cols;
    }

    public TextAreaComponent setCols(int cols) {
        this.cols = cols;
        return this;
    }

    public int getRows() {
        return rows;
    }

    public TextAreaComponent setRows(int rows) {
        this.rows = rows;
        return this;
    }
}
