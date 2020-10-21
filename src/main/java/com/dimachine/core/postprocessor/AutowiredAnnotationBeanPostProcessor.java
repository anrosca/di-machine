package com.dimachine.core.postprocessor;

import com.dimachine.core.*;
import com.dimachine.core.annotation.Autowired;
import com.dimachine.core.annotation.Ordered;
import com.dimachine.core.util.CollectionFactory;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Ordered(Order.HIGHEST_PRECEDENCE)
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {
    private final BeanFactory beanFactory;

    public AutowiredAnnotationBeanPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialisation(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        autowireFields(bean, beanClass);
        autowireSetters(bean, beanClass);
        return bean;
    }

    private void autowireSetters(Object bean, Class<?> beanClass) {
        do {
            doAutowireSettersFor(bean, beanClass);
        } while ((beanClass = beanClass.getSuperclass()) != null);
    }

    private void doAutowireSettersFor(Object bean, Class<?> beanClass) {
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Autowired.class)) {
                Autowired autowired = method.getAnnotation(Autowired.class);
                if (autowired.required()) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    invokeMethod(bean, method, makeParameterValues(parameterTypes, method));
                }
            }
        }
    }

    private Object[] makeParameterValues(Class<?>[] parameterTypes, Method method) {
        Object[] parameterValues = new Object[parameterTypes.length];
        int parameterIndex = 0;
        for (Class<?> parameterType : parameterTypes) {
            parameterValues[parameterIndex++] = revolveParameterValue(parameterType, method);
        }
        return parameterValues;
    }

    private Object revolveParameterValue(Class<?> parameterType, Method method) {
        if (isCollection(parameterType)) {
            return resolveCollectionMethodParameterValue(parameterType, method);
        }
        if (isMap(parameterType)) {
            return resolveMapMethodParameterValue(parameterType, method);
        }
        return beanFactory.getBean(parameterType);
    }

    private Object resolveMapMethodParameterValue(Class<?> parameterType, Method method) {
        try {
            ParameterizedType genericType = (ParameterizedType) method.getGenericParameterTypes()[0];
            return doResolveMapFieldValue(method.getName(), genericType, parameterType);
        } catch (ClassNotFoundException e) {
            throw new SetterInjectionFailedException("Setter injection failed for setter " +
                    method.getName() + ". Parameter class not found", e);
        }
    }

    private Object resolveCollectionMethodParameterValue(Class<?> parameterType, Method method) {
        try {
            ParameterizedType genericType = (ParameterizedType) method.getGenericParameterTypes()[0];
            return doResolveCollectionValue(genericType, parameterType, method.getName());
        } catch (ClassNotFoundException e) {
            throw new SetterInjectionFailedException("Setter injection failed for setter " + method.getName() +
                    ". Parameter class not found", e);
        }
    }

    private void invokeMethod(Object bean, Method method, Object[] parameterValues) {
        try {
            method.setAccessible(true);
            method.invoke(bean, parameterValues);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanInitialisationException("Could not autowire method " + method.getName(), e);
        }
    }

    protected void autowireFields(Object bean, Class<?> beanClass) {
        do {
            doAutowireFieldsFor(bean, beanClass);
        } while ((beanClass = beanClass.getSuperclass()) != null);
    }

    private void doAutowireFieldsFor(Object bean, Class<?> beanClass) {
        for (Field field : beanClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                if (autowired.required()) {
                    setField(bean, field);
                }
            }
        }
    }

    protected void setField(Object bean, Field field) {
        try {
            field.setAccessible(true);
            field.set(bean, resolveFieldValue(bean, field));
        } catch (IllegalAccessException e) {
            throw new FieldInjectionFailedException("Could not autowire field " + field.getName() +
                    " of bean " + bean.getClass(), e);
        }
    }

    private Object resolveFieldValue(Object bean, Field field) {
        if (isCollection(field.getType())) {
            return resolveCollectionFieldValue(bean, field);
        }
        if (isMap(field.getType())) {
            return resolveMapFieldValue(bean, field);
        }
        return beanFactory.getBean(field.getType());
    }

    private Object resolveMapFieldValue(Object bean, Field field) {
        try {
            return doResolveMapFieldValue(field.getName(), (ParameterizedType) field.getGenericType(), field.getType());
        } catch (ClassNotFoundException e) {
            throw new FieldInjectionFailedException("Could not autowire field " + field.getName() +
                    " of bean " + bean.getClass(), e);
        }
    }

    private Object doResolveMapFieldValue(String entityName, ParameterizedType genericType, Class<?> targetType) throws ClassNotFoundException {
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        checkMapKeyType(entityName, actualTypeArguments[0]);
        String valueTypeName = actualTypeArguments[1].getTypeName();
        if (isWildcardType(valueTypeName)) {
            valueTypeName = normalizeWildcardTypeName(valueTypeName, entityName);
        }
        Class<?> dependencyClass = Class.forName(valueTypeName);
        return convertMapToType(dependencyClass, targetType);
    }

    private void checkMapKeyType(String entityName, Type actualTypeArgument) {
        String keyTypeName = actualTypeArgument.getTypeName();
        if (!keyTypeName.equals(String.class.getName())) {
            throw new FieldInjectionFailedException("Field injection failed for field " + entityName +
                    ". Map key is not a string");
        }
    }

    private Object convertMapToType(Class<?> dependencyClass, Class<?> mapType) {
        Map<String, ?> beansMapOfType = beanFactory.getBeansMapOfType(dependencyClass);
        Map<Object, Object> resultingMap = CollectionFactory.newMapOfType(mapType);
        resultingMap.putAll(beansMapOfType);
        return resultingMap;
    }

    private Object resolveCollectionFieldValue(Object bean, Field field) {
        try {
            return doResolveCollectionFieldValue(field);
        } catch (ClassNotFoundException e) {
            throw new FieldInjectionFailedException("Could not autowire field " + field.getName() +
                    " of bean " + bean.getClass(), e);
        }
    }

    private Object doResolveCollectionFieldValue(Field field) throws ClassNotFoundException {
        return doResolveCollectionValue((ParameterizedType) field.getGenericType(), field.getType(), field.getName());
    }

    private Collection<?> doResolveCollectionValue(ParameterizedType genericType, Class<?> targetType, String entityName) throws ClassNotFoundException {
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        String dependencyTypeName = actualTypeArguments[0].getTypeName();
        if (isWildcardType(dependencyTypeName)) {
            dependencyTypeName = normalizeWildcardTypeName(dependencyTypeName, entityName);
        }
        Class<?> dependencyClass = Class.forName(dependencyTypeName);
        return convertCollectionToType(dependencyClass, targetType);
    }

    private Collection<?> convertCollectionToType(Class<?> dependencyClass, Class<?> collectionType) {
        Collection<Object> collection = CollectionFactory.newCollectionOfType(collectionType);
        collection.addAll(beanFactory.getAllBeansOfType(dependencyClass));
        return collection;
    }

    private String normalizeWildcardTypeName(String dependencyTypeName, String fieldName) {
        String normalizedTypeName = dependencyTypeName.substring(dependencyTypeName.lastIndexOf(" ") + 1);
        if (normalizedTypeName.contains("?"))
            throw new FieldInjectionFailedException("Could not autowired field " + fieldName +
                    " because it's type is wildcard '" + normalizedTypeName + "'");
        return normalizedTypeName;
    }

    private boolean isWildcardType(String dependencyTypeName) {
        return dependencyTypeName.contains("?");
    }

    private boolean isCollection(Class<?> type) {
        return List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type);
    }

    private boolean isMap(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }
}
