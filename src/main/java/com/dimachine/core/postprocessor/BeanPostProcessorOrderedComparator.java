package com.dimachine.core.postprocessor;

import com.dimachine.core.BeanPostProcessor;
import com.dimachine.core.Order;
import com.dimachine.core.annotation.Ordered;

import java.util.Comparator;

public class BeanPostProcessorOrderedComparator implements Comparator<BeanPostProcessor> {
    @Override
    public int compare(BeanPostProcessor first, BeanPostProcessor second) {
        return Integer.compare(getOrder(first), getOrder(second));
    }

    private int getOrder(BeanPostProcessor processor) {
        Class<?> processorClass = processor.getClass();
        if (processorClass.isAnnotationPresent(Ordered.class)) {
            Ordered ordered = processorClass.getAnnotation(Ordered.class);
            return ordered.value().getPrecedence();
        }
        return Order.DEFAULT_PRECEDENCE.getPrecedence();
    }
}
