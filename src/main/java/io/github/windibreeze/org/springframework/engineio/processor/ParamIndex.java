package io.github.windibreeze.org.springframework.engineio.processor;

public class ParamIndex {
    private String type;
    private Integer index;

    public ParamIndex(String type, Integer index) {
        this.type = type;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
