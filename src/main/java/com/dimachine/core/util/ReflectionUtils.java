package com.dimachine.core.util;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectionUtils {

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Object instance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeMethod(Object instance, Method method, Object... parameterValues) {
        try {
            method.setAccessible(true);
            return method.invoke(instance, parameterValues);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        List<Method> declaredMethods = getDeclaredMethods(clazz);
        for (Method method : declaredMethods) {
            if (method.getName().equals(methodName) && Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                return method;
            }
        }
        return null;
    }

    public static List<Method> getDeclaredMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        do {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        } while ((clazz = clazz.getSuperclass()) != null);
        return methods;
    }

    @SuppressWarnings("unchecked")
    public static <T> T makeInstance(Class<T> clazz, Object... constructorArguments) {
        try {
            Class<?>[] parameterTypes = Arrays.stream(constructorArguments)
                    .map(Object::getClass)
                    .toArray(Class[]::new);
            Constructor<?> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return (T) constructor.newInstance(constructorArguments);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String makePrettyModifiers(int modifiers) {
        if (Modifier.isPublic(modifiers))
            return "public";
        if (Modifier.isPrivate(modifiers))
            return "private";
        if (Modifier.isProtected(modifiers))
            return "protected";
        return "";
    }

    public static Field[] getDeclaredFields(Class<?> beanClass) {
        List<Field> declaredFields = new ArrayList<>();
        while (beanClass != null) {
            declaredFields.addAll(Arrays.asList(beanClass.getDeclaredFields()));
            beanClass = beanClass.getSuperclass();
        }
        return declaredFields.toArray(Field[]::new);
    }

    public static String makePrettyMethodSignature(Class<?> clazz, Method proceed) {
        String methodParameters = Arrays.stream(proceed.getParameterTypes())
                .map(Class::getName)
                .collect(Collectors.joining(","));
        return "%s %s %s.%s(%s)".formatted(
                ReflectionUtils.makePrettyModifiers(proceed.getModifiers()),
                proceed.getReturnType().getName(),
                clazz.getName(),
                proceed.getName(),
                String.join(",", methodParameters)
        );
    }
}
