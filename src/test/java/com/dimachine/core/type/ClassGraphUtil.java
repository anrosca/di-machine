package com.dimachine.core.type;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.Resource;

import java.lang.reflect.Constructor;

public class ClassGraphUtil {

    public static ClassInfo makeClassInfo(Class<?> clazz) {
        try {
            Constructor<ClassInfo> constructor =
                    ClassInfo.class.getDeclaredConstructor(String.class, int.class, Resource.class);
            constructor.setAccessible(true);
            return constructor.newInstance(clazz.getName(), clazz.getModifiers(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
