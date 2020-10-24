package com.dimachine.core.type;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.Resource;

import java.lang.reflect.Constructor;

import static org.mockito.Mockito.*;

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

    public static ClassInfo makeClassInfoImplementing(Class<?> clazz, Class<?> implementedInterface) {
        try {
            Constructor<ClassInfo> constructor =
                    ClassInfo.class.getDeclaredConstructor(String.class, int.class, Resource.class);
            constructor.setAccessible(true);
            ClassInfo classInfo = mock(ClassInfo.class);
            if (implementedInterface.isInterface()) {
                when(classInfo.implementsInterface(implementedInterface.getName())).thenReturn(true);
            } else {
                when(classInfo.getSuperclass())
                        .thenReturn(makeClassInfo(implementedInterface))
                        .thenReturn(null);
                when(classInfo.getName()).thenReturn(clazz.getName());
            }
            return classInfo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
