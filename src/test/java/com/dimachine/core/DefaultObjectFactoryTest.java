package com.dimachine.core;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultObjectFactoryTest {

    private final DefaultObjectFactory objectFactory = new DefaultObjectFactory();
    private final DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();

    @Test
    public void shouldBeAbleToMakeSingletonBean_withDefaultConstructor() {
        SimpleBeanDefinition beanDefinition = makeBeanDefinitionFor(TestFooService.class);
        defaultBeanFactory.registerBeans(beanDefinition);

        Object createdInstance = objectFactory.instantiate(TestFooService.class, defaultBeanFactory);

        assertNotNull(createdInstance);
        assertTrue(createdInstance instanceof TestFooService);
    }

    @Test
    public void shouldThrowBeanCurrentlyInCreationException_whenThereAreCyclicDependencies() {
        SimpleBeanDefinition fooServiceDefinition = makeBeanDefinitionFor(CyclicFooService.class);
        SimpleBeanDefinition barServiceDefinition = makeBeanDefinitionFor(CyclicBarService.class);
        defaultBeanFactory.registerBeans(fooServiceDefinition, barServiceDefinition);

        assertThrows(BeanCurrentlyInCreationException.class, () -> objectFactory.instantiate(CyclicFooService.class, defaultBeanFactory));
    }

    @Test
    public void shouldBeAbleToMakeSingletonBean_withOneConstructorParameter() {
        SimpleBeanDefinition fooServiceDefinition = makeBeanDefinitionFor(TestFooService.class);
        SimpleBeanDefinition barServiceDefinition = makeBeanDefinitionFor(TestBarService.class);
        defaultBeanFactory.registerBeans(fooServiceDefinition, barServiceDefinition);

        Object createdInstance = objectFactory.instantiate(TestBarService.class, defaultBeanFactory);
        assertNotNull(createdInstance);
        assertTrue(createdInstance instanceof TestBarService);
    }

    @Test
    public void shouldBeAbleToMakeSingletonBean_withChoosingConstructorThatCanBeSatisfied() {
        SimpleBeanDefinition fooServiceDefinition = makeBeanDefinitionFor(ManyConstructorsFooService.class);
        SimpleBeanDefinition barServiceDefinition = makeBeanDefinitionFor(TestFooService.class);
        defaultBeanFactory.registerBeans(fooServiceDefinition, barServiceDefinition);

        Object createdInstance = objectFactory.instantiate(ManyConstructorsFooService.class, defaultBeanFactory);
        assertNotNull(createdInstance);
        assertTrue(createdInstance instanceof ManyConstructorsFooService);
    }

    @Test
    public void shouldThrowBeanCannotBeInstantiatedException_whenNoMatchingConstructorsAreFound() {
        SimpleBeanDefinition beanDefinition = makeBeanDefinitionFor(TestBarService.class);
        defaultBeanFactory.registerBeans(beanDefinition);

        assertThrows(BeanCannotBeInstantiatedException.class, () -> objectFactory.instantiate(TestBarService.class, defaultBeanFactory));
    }

    @Test
    public void shouldThrowBeanInstantiationException_whenBeanInstantiationFails() {
        SimpleBeanDefinition beanDefinition = makeBeanDefinitionFor(ExceptionFooService.class);
        defaultBeanFactory.registerBeans(beanDefinition);

        assertThrows(BeanInstantiationException.class, () -> objectFactory.instantiate(ExceptionFooService.class, defaultBeanFactory));
    }

    private SimpleBeanDefinition makeBeanDefinitionFor(Class<?> beanClass) {
        return SimpleBeanDefinition.builder()
                .className(beanClass.getName())
                .beanName(beanClass.getSimpleName())
                .build();
    }

    private static class TestFooService {

    }

    private static class TestBarService {
        private final TestFooService fooService;

        private TestBarService(TestFooService fooService) {
            this.fooService = Objects.requireNonNull(fooService);
        }
    }

    private static class CyclicFooService {
        private final CyclicBarService barService;

        protected CyclicFooService(CyclicBarService barService) {
            this.barService = barService;
        }
    }

    private static class CyclicBarService {
        private final CyclicFooService fooService;

        CyclicBarService(CyclicFooService fooService) {
            this.fooService = fooService;
        }
    }

    private static class ExceptionFooService {
        public ExceptionFooService() {
            throw new IllegalArgumentException();
        }
    }

    private static class ManyConstructorsFooService {
        private final TestFooService fooService;

        private ManyConstructorsFooService(TestFooService fooService, TestBarService barService) {
            this.fooService = fooService;
        }

        public ManyConstructorsFooService(TestFooService fooService) {
            this.fooService = Objects.requireNonNull(fooService);
        }

        private ManyConstructorsFooService(TestFooService fooService, String config) {
            this.fooService = fooService;
        }
    }
}