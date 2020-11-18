package com.dimachine.core;

import com.dimachine.core.postprocessor.SetterInjectionFailedException;
import com.dimachine.core.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class BeanParameterResolver {
    private final BeanFactory beanFactory;

    public BeanParameterResolver(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Object[] resolve(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return makeParameterValues(parameterTypes, method);
    }

    public Object resolve(Object bean, Field field) {
        if (CollectionUtils.isCollection(field.getType())) {
            return resolveCollectionFieldValue(bean, field);
        }
        if (CollectionUtils.isMap(field.getType())) {
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

    private Object[] makeParameterValues(Class<?>[] parameterTypes, Method method) {
        Object[] parameterValues = new Object[parameterTypes.length];
        int parameterIndex = 0;
        for (Class<?> parameterType : parameterTypes) {
            parameterValues[parameterIndex++] = revolveParameterValue(parameterType, method);
        }
        return parameterValues;
    }

    private Object revolveParameterValue(Class<?> parameterType, Method method) {
        if (CollectionUtils.isCollection(parameterType)) {
            return resolveCollectionMethodParameterValue(parameterType, method);
        }
        if (CollectionUtils.isMap(parameterType)) {
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
            throw new BeanInitializationException("Injection failed for " + entityName + ". Map key is not a string");
        }
    }

    private Object convertMapToType(Class<?> dependencyClass, Class<?> mapType) {
        Map<String, ?> beansMapOfType = beanFactory.getBeansMapOfType(dependencyClass);
        Map<Object, Object> resultingMap = CollectionUtils.newMapOfType(mapType);
        resultingMap.putAll(beansMapOfType);
        return resultingMap;
    }

    private Object resolveCollectionMethodParameterValue(Class<?> parameterType, Method method) {
        try {
            ParameterizedType genericType = (ParameterizedType) method.getGenericParameterTypes()[0];
            return doResolveCollectionValue(genericType, parameterType, method.getName());
        } catch (ClassNotFoundException e) {
            throw new BeanInitializationException("Dependency injection failed " + method.getName() +
                    ". Parameter class not found", e);
        }
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
        Collection<Object> collection = CollectionUtils.newCollectionOfType(collectionType);
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
}
