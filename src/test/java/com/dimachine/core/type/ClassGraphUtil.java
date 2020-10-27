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
            ClassInfo classInfoSpy = spy(constructor.newInstance(clazz.getName(), clazz.getModifiers(), null));
            doReturn(clazz.getName()).when(classInfoSpy).getName();
            return classInfoSpy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClassInfo makeClassInfoImplementing(Class<?> clazz, Class<?> implementedInterface) {
        try {
            ClassInfo classInfo = makeClassInfo(clazz);
            if (implementedInterface.isInterface()) {
                doReturn(true).when(classInfo).implementsInterface(implementedInterface.getName());
            } else {
                int[] invocationCount = new int[1];
                doAnswer(invocationOnMock -> {
                    if (++invocationCount[0] > 1)
                        return null;
                    return makeClassInfo(implementedInterface);
                }).when(classInfo).getSuperclass();
            }
            return classInfo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
