package io.github.windibreeze.org.springframework.engineio.processor;

import io.github.windibreeze.org.springframework.engineio.annotation.OnClose;
import io.github.windibreeze.org.springframework.engineio.annotation.OnConnection;
import io.github.windibreeze.org.springframework.engineio.annotation.OnMessage;
import io.github.windibreeze.org.springframework.engineio.annotation.OnOpen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EngineIoAnnotationProcessor implements BeanPostProcessor, Ordered, SmartInitializingSingleton {
    private static final Logger LOGGER = LoggerFactory.getLogger(EngineIoAnnotationProcessor.class);
    private static final Set<Class<? extends Annotation>> SCAN_ANNOTATION = new HashSet<>();
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    static {
        SCAN_ANNOTATION.add(OnConnection.class);
        SCAN_ANNOTATION.add(OnOpen.class);
        SCAN_ANNOTATION.add(OnClose.class);
        SCAN_ANNOTATION.add(OnMessage.class);
    }

    @Autowired
    private EndpointProcessor endpointProcessor;

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void afterSingletonsInstantiated() {
        LOGGER.debug("clear cache");
        this.nonAnnotatedClasses.clear();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AopInfrastructureBean) {
            return bean;
        }

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (this.nonAnnotatedClasses.contains(targetClass) ||
                !AnnotationUtils.isCandidateClass(targetClass, SCAN_ANNOTATION)) {
            return bean;
        }

        boolean hasMethod = false;
        for (Class<? extends Annotation> annotation : SCAN_ANNOTATION) {
            Map<Method, Set> methods = MethodIntrospector.selectMethods(targetClass,
                    (MethodIntrospector.MetadataLookup<Set>) method -> {
                        Set listenerMethods = AnnotatedElementUtils.getAllMergedAnnotations(
                                method, annotation);
                        return (listenerMethods.isEmpty() ? null : listenerMethods);
                    });

            if (methods.isEmpty()) {
                continue;
            }

            hasMethod = true;
            String name = annotation.getSimpleName();
            methods.forEach((method, listeners) ->
                    listeners.forEach(listener -> endpointProcessor.registerEndpoint(bean, name, method))
            );
            LOGGER.debug(" @{} methods processed on bean {}: {}", annotation.getSimpleName(), beanName, methods);
        }

        if (!hasMethod) {
            this.nonAnnotatedClasses.add(targetClass);
            LOGGER.trace("No annotations found on bean type: {}", targetClass.getSimpleName());
        }

        return bean;
    }

}
