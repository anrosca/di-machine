package com.dimachine.core.util;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Method> getDeclaredMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        do {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        } while ((clazz = clazz.getSuperclass()) != null);
        return methods;
    }

    public static Object makeInstance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
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
}
