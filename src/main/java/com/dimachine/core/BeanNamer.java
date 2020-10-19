package com.dimachine.core;

import com.dimachine.core.annotation.Bean;

import java.lang.reflect.Method;

public interface BeanNamer {
    String makeBeanName(String beanClassName);

    String makeBeanName(Method method, Bean beanAnnotation);
}
