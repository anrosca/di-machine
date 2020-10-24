package com.dimachine.core.type;

import io.github.classgraph.ClassInfo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.dimachine.core.type.ClassGraphUtil.makeClassInfo;
import static com.dimachine.core.type.ClassGraphUtil.makeClassInfoImplementing;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassGraphClassMetadataTest {

    @Test
    public void shouldBeAbleToGetClassName() {
        ClassInfo classInfo = makeClassInfo(String.class);
        ClassMetadata classMetadata = new ClassGraphClassMetadata(classInfo);

        assertEquals("java.lang.String", classMetadata.getClassName());
    }

    @Test
    public void shouldBeAbleToTellIfClassIsFinal() {
        ClassInfo classInfo = makeClassInfo(String.class);
        ClassMetadata classMetadata = new ClassGraphClassMetadata(classInfo);

        assertTrue(classMetadata.isFinal());
    }

    @Test
    public void shouldBeAbleToTellIfClassIsAbstract() {
        ClassInfo classInfo = makeClassInfo(AbstractList.class);
        ClassMetadata classMetadata = new ClassGraphClassMetadata(classInfo);

        assertTrue(classMetadata.isAbstract());
    }

    @Test
    public void shouldBeAbleToTellIfClassIInterface() {
        ClassInfo classInfo = makeClassInfo(Supplier.class);
        ClassMetadata classMetadata = new ClassGraphClassMetadata(classInfo);

        assertTrue(classMetadata.isInterface());
    }

    @Test
    public void shouldBeAbleToTellIfGivenClassImplementsInterface() {
        ClassInfo classInfo = makeClassInfoImplementing(ArrayList.class, List.class);
        ClassMetadata classMetadata = new ClassGraphClassMetadata(classInfo);

        assertTrue(classMetadata.isSubclassOf("java.util.List"));
    }

    @Test
    public void shouldBeAbleToTellIfGivenClassExtendsAnotherClass() {
        ClassInfo classInfo = makeClassInfoImplementing(ArrayList.class, AbstractList.class);
        ClassMetadata classMetadata = new ClassGraphClassMetadata(classInfo);

        assertTrue(classMetadata.isSubclassOf("java.util.AbstractList"));
    }
}
