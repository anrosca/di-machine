package com.dimachine.core.proxy;

import com.dimachine.core.BeanDefinition;
import com.dimachine.core.BeanFactory;
import com.dimachine.core.BeanScope;
import com.dimachine.core.ScopeResolver;
import com.dimachine.core.scanner.AnnotationBeanDefinitionScanner;
import com.dimachine.core.scanner.BeanDefinitionScanner;
import com.dimachine.core.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationClassInvocationHandler implements MethodInterceptor {
    private final Logger log = LoggerFactory.getLogger(ConfigurationClassInvocationHandler.class);
    private final Object lock = new Object();
    private final Set<String> executedMethods = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final BeanFactory beanFactory;
    private final ScopeResolver scopeResolver = new ScopeResolver();
    private final BeanDefinitionScanner beanDefinitionScanner = new AnnotationBeanDefinitionScanner();

    public ConfigurationClassInvocationHandler(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        BeanScope beanScope = scopeResolver.resolveScope(thisMethod);
        if (isPrototype(beanScope)) {
            return proceed.invoke(self, args);
        } else {
            return handleSingletonMethodInvocation(self, thisMethod, proceed, args);
        }
    }

    private Object handleSingletonMethodInvocation(Object self, Method thisMethod, Method proceed, Object[] args) throws Exception {
        synchronized (lock) {
            String methodSignature = ReflectionUtils.makePrettyMethodSignature(self.getClass(), proceed);
            BeanDefinition beanDefinition = beanDefinitionScanner.makeBeanDefinition(thisMethod);
            if (!methodWasExecuted(methodSignature)) {
                executedMethods.add(methodSignature);
                Object beanInstance = proceed.invoke(self, args);
                beanFactory.registerSingleton(beanDefinition, beanInstance);
                return beanInstance;
            } else {
                log.debug("@Bean method already invoked. Getting bean from BeanFactory instead");
                return beanFactory.getBean(beanDefinition.getBeanName(), thisMethod.getReturnType());
            }
        }
    }

    private boolean methodWasExecuted(String methodSignature) {
        return executedMethods.contains(methodSignature);
    }

    private boolean isPrototype(BeanScope beanScope) {
        return beanScope == BeanScope.PROTOTYPE;
    }
}
