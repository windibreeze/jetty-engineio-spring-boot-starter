package io.github.windibreeze.org.springframework.engineio.processor;


import java.lang.reflect.Method;
import java.util.List;

public class Endpoint {
    private Object bean;
    private Method method;
    private List<ParamIndex> paramIndexList;

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<ParamIndex> getParamIndexList() {
        return paramIndexList;
    }

    public void setParamIndexList(List<ParamIndex> paramIndexList) {
        this.paramIndexList = paramIndexList;
    }
}
