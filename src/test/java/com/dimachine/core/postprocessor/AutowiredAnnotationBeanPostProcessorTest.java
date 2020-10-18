package com.dimachine.core.postprocessor;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.BeanInitialisationException;
import com.dimachine.core.FieldInjectionFailedException;
import com.dimachine.core.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AutowiredAnnotationBeanPostProcessorTest {
    private final BeanFactory beanFactory = mock(BeanFactory.class);
    private AutowiredAnnotationBeanPostProcessor postProcessor;

    @BeforeEach
    public void setUp() {
        when(beanFactory.getBean(FooService.class)).thenReturn(new FooService());
        postProcessor = new AutowiredAnnotationBeanPostProcessor(beanFactory);
    }

    @Test
    public void shouldAutowireFieldsAnnotatedWithAutowired() {
        BarService barService = new BarService();

        postProcessor.postProcessBeforeInitialisation(barService, "beanName");

        assertNotNull(barService.fooService);
    }

    @Test
    public void shouldAutowireFieldsAnnotatedWithAutowiredFromSuperclass() {
        BarService barService = new SuperclassAutowireBarService();

        postProcessor.postProcessBeforeInitialisation(barService, "beanName");

        assertNotNull(barService.fooService);
    }

    @Test
    public void shouldAutowireSettersAnnotatedWithAutowired() {
        SetterAutowireBarService barService = new SetterAutowireBarService();

        postProcessor.postProcessBeforeInitialisation(barService, "beanName");

        assertNotNull(barService.fooService);
    }

    @Test
    public void shouldAutowireSettersAnnotatedWithAutowiredFromSuperclass() {
        SuperclassSetterBarService barService = new SuperclassSetterBarService();

        postProcessor.postProcessBeforeInitialisation(barService, "beanName");

        assertNotNull(barService.fooService);
    }

    @Test
    public void shouldThrowBeanInitialisationException_whenSetterInjectionFails() {
        ExceptionSetterBarService barService = new ExceptionSetterBarService();

        assertThrows(BeanInitialisationException.class, () -> postProcessor.postProcessBeforeInitialisation(barService, "beanName"));
    }

    @Test
    public void shouldIgnoreAutowiredOptionalFields() {
        OptionalAutowireBarService barService = new OptionalAutowireBarService();

        postProcessor.postProcessBeforeInitialisation(barService, "beanName");

        assertNull(barService.fooService);
        verifyNoInteractions(beanFactory);
    }

    @Test
    public void whenAutowiringListType_shouldInjectAllBeansOfThatType() {
        List<FooService> expectedDependencies = List.of(new FooService(), new YetAnotherFooService());
        when(beanFactory.getAllBeansOfType(FooService.class)).thenReturn(expectedDependencies);
        AutowireListBarService bean = new AutowireListBarService();

        postProcessor.postProcessBeforeInitialisation(bean, "testBean");

        assertEquals(expectedDependencies, bean.fooServices);
    }

    @Test
    public void whenAutowiringListWithWildcardType_shouldInjectAllBeansOfThatType() {
        List<FooService> expectedDependencies = List.of(new FooService(), new YetAnotherFooService());
        when(beanFactory.getAllBeansOfType(FooService.class)).thenReturn(expectedDependencies);
        AutowireWildcardListBarService bean = new AutowireWildcardListBarService();

        postProcessor.postProcessBeforeInitialisation(bean, "testBean");

        assertEquals(expectedDependencies, bean.fooServices);
    }

    @Test
    public void whenAutowiringListWithGenericWildcardType_shouldInjectAllBeansOfThatType() {
        AutowireGenericWildcardListBarService bean = new AutowireGenericWildcardListBarService();

        assertThrows(FieldInjectionFailedException.class, () -> postProcessor.postProcessBeforeInitialisation(bean, "testBean"));
    }

    private static class FooService {
    }

    private static class YetAnotherFooService extends FooService {
    }

    private static class BarService {
        @Autowired
        protected FooService fooService;
    }

    private static class SuperclassAutowireBarService extends BarService {
    }

    private static class OptionalAutowireBarService {
        @Autowired(required = false)
        private FooService fooService;
    }

    private static class FieldAutowireFailureBarService {
        @Autowired(required = true)
        private final FooService fooService;

        private FieldAutowireFailureBarService() {
            this.fooService = null;
        }
    }

    private static class SetterAutowireBarService {
        protected FooService fooService;

        @Autowired
        private void setFooService(FooService fooService) {
            this.fooService = fooService;
        }
    }

    private static class SuperclassSetterBarService extends SetterAutowireBarService {
    }

    private static class ExceptionSetterBarService extends SetterAutowireBarService {
        @Autowired
        private void setFooService(FooService fooService) {
            throw new IllegalArgumentException();
        }
    }

    private static class AutowireListBarService {
        @Autowired
        private List<FooService> fooServices;
    }

    private static class AutowireWildcardListBarService {
        @Autowired
        private List<? extends FooService> fooServices;
    }

    private static class AutowireGenericWildcardListBarService {
        @Autowired
        private List<?> fooServices;
    }
}
