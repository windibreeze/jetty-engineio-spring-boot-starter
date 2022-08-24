package io.github.windibreeze.org.springframework.engineio.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Component
public class EndpointProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointProcessor.class);

    private final Map<String, List<Endpoint>> endpointMap = new HashMap<>();

    public void registerEndpoint(Object bean, String name, Method method) {
        Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());
        List<ParamIndex> paramIndexList = this.collectParamIndex(method);
        Endpoint endpoint = new Endpoint();
        endpoint.setBean(bean);
        endpoint.setMethod(invocableMethod);
        endpoint.setParamIndexList(paramIndexList);
        endpointMap.computeIfAbsent(name, k -> new ArrayList<>()).add(endpoint);
    }

    private List<ParamIndex> collectParamIndex(Method method) {
        Class<?>[] params = method.getParameterTypes();
        List<ParamIndex> paramIndexList = new ArrayList<>();
        if (params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                String className = params[i].getTypeName();
                paramIndexList.add(new ParamIndex(className, i));
            }
        }
        return paramIndexList;
    }

    public void handleEndpoint(Class<? extends Annotation> annotation, Object... args) {
        String key = annotation.getSimpleName();
        if (!this.endpointMap.containsKey(key)) {
            return;
        }

        List<Endpoint> endpointList = this.endpointMap.get(key);
        for (Endpoint endpoint : endpointList) {
            try {
                this.invoke(endpoint, args);
            } catch (Exception e) {
                LOGGER.error("{} invoke error", key, e);
            }
        }
    }

    private void invoke(Endpoint endpoint, Object[] args) throws InvocationTargetException, IllegalAccessException {
        List<ParamIndex> paramIndexList = endpoint.getParamIndexList();
        Object[] params = new Object[paramIndexList.size()];

        Queue<Object> last = new LinkedList<>();
        for (Object arg : args) {
            if (null == arg) {
                continue;
            }

            boolean mismatchFlag = true;
            for (ParamIndex paramType : paramIndexList) {
                if (!arg.getClass().getTypeName().equals(paramType.getType())) {
                    continue;
                }
                int index = paramType.getIndex();
                if (params.length > index && null == params[index]) {
                    params[index] = arg;
                    mismatchFlag = false;
                    break;
                }
            }

            if (mismatchFlag) {
                last.add(arg);
            }
        }
        if (!last.isEmpty()) {
            for (int i = 0; i < params.length; i++) {
                if (null != params[i]) {
                    continue;
                }
                params[i] = last.remove();
            }
        }

        endpoint.getMethod().invoke(endpoint.getBean(), params);
    }
}
