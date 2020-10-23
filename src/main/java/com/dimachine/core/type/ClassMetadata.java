package com.dimachine.core.type;

import java.util.List;

public interface ClassMetadata {

    String getClassName();

    boolean isAbstract();

    boolean isInterface();

    boolean isFinal();

    List<AnnotationMetadata> getAnnotations();
}
