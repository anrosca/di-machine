package com.dimachine.core.type;

import com.dimachine.core.annotation.Service;
import org.junit.jupiter.api.Test;

import static com.dimachine.core.type.ClassGraphUtil.makeClassInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClassGraphAnnotationMetadataTest {

    @Test
    public void shouldBeAbleToGetAnnotationClassName() {
        ClassGraphAnnotationMetadata annotationMetadata = new ClassGraphAnnotationMetadata(makeClassInfo(Service.class));

        assertEquals(Service.class.getName(), annotationMetadata.getAnnotationClassName());
    }

    @Test
    public void shouldThrowIllegalArgumentException_whenConstructorArgumentIsNotAnAnnotation() {
        assertThrows(IllegalArgumentException.class, () -> new ClassGraphAnnotationMetadata(makeClassInfo(String.class)));
    }
}
