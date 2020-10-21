package com.dimachine.core.postprocessor;

import com.dimachine.core.BeanFactory;
import com.dimachine.core.BeanInitialisationException;
import com.dimachine.core.FieldInjectionFailedException;
import com.dimachine.core.annotation.Autowired;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void shouldAutowireSettersAcceptingListsAnnotatedWithAutowired() {
        ListSetterAutowireBarService barService = new ListSetterAutowireBarService();

        postProcessor.postProcessBeforeInitialisation(barService, "beanName");

        assertNotNull(barService.fooServices);
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
    public void whenAutowiringSetType_shouldInjectAllBeansOfThatType() {
        Set<FooService> expectedDependencies = Set.of(new FooService(), new YetAnotherFooService());
        when(beanFactory.getAllBeansOfType(FooService.class)).thenReturn(new ArrayList<>(expectedDependencies));
        AutowireSetBarService bean = new AutowireSetBarService();

        postProcessor.postProcessBeforeInitialisation(bean, "testBean");

        assertEquals(expectedDependencies, bean.fooServices);
    }

    @Test
    public void whenAutowiringMapTypeViaSetter_shouldInjectAllBeansOfThatTypeWithTheirNames() {
        Map<String, FooService> expectedDependencies = Map.of("fooService", new FooService(), "yetAnotherFooService", new YetAnotherFooService());
        when(beanFactory.getBeansMapOfType(FooService.class)).thenReturn(expectedDependencies);
        MapSetterAutowireBarService bean = new MapSetterAutowireBarService();

        postProcessor.postProcessBeforeInitialisation(bean, "testBean");

        assertEquals(expectedDependencies, bean.fooServices);
    }

    @Test
    public void whenAutowiringMapType_shouldInjectAllBeansOfThatTypeWithTheirNames() {
        Map<String, FooService> expectedDependencies = Map.of("fooService", new FooService(), "yetAnotherFooService", new YetAnotherFooService());
        when(beanFactory.getBeansMapOfType(FooService.class)).thenReturn(expectedDependencies);
        AutowireMapBarService bean = new AutowireMapBarService();

        postProcessor.postProcessBeforeInitialisation(bean, "testBean");

        assertEquals(expectedDependencies, bean.fooServices);
    }

    @Test
    public void whenAutowiringMapTypeWithWildcards_shouldInjectAllBeansOfThatTypeWithTheirNames() {
        Map<String, FooService> expectedDependencies = Map.of("fooService", new FooService(), "yetAnotherFooService", new YetAnotherFooService());
        when(beanFactory.getBeansMapOfType(FooService.class)).thenReturn(expectedDependencies);
        AutowireMapWithWildcardsBarService bean = new AutowireMapWithWildcardsBarService();

        postProcessor.postProcessBeforeInitialisation(bean, "testBean");

        assertEquals(expectedDependencies, bean.fooServices);
    }

    @Test
    public void whenAutowiringMapWithNonStringKeyType_shouldThrowFieldInjectionFailedException() {
        Map<String, FooService> expectedDependencies = Map.of("fooService", new FooService(), "yetAnotherFooService", new YetAnotherFooService());
        when(beanFactory.getBeansMapOfType(FooService.class)).thenReturn(expectedDependencies);
        AutowireMapWithWrongKeyBarService bean = new AutowireMapWithWrongKeyBarService();

        assertThrows(FieldInjectionFailedException.class, () -> postProcessor.postProcessBeforeInitialisation(bean, "testBean"));
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

    private static class ListSetterAutowireBarService {
        protected List<FooService> fooServices;

        @Autowired
        private void setFooService(List<FooService> fooServices) {
            this.fooServices = fooServices;
        }
    }

    private static class MapSetterAutowireBarService {
        protected Map<String, FooService> fooServices;

        @Autowired
        private void setFooService(Map<String, FooService> fooServices) {
            this.fooServices = fooServices;
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

    private static class AutowireSetBarService {
        @Autowired
        private Set<FooService> fooServices;
    }

    private static class AutowireMapBarService {
        @Autowired
        private Map<String, FooService> fooServices;
    }

    private static class AutowireMapWithWildcardsBarService {
        @Autowired
        private Map<String, ? extends FooService> fooServices;
    }

    private static class AutowireMapWithWrongKeyBarService {
        @Autowired
        private Map<FooService, FooService> fooServices;
    }
}
