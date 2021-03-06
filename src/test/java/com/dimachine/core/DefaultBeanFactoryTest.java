package com.dimachine.core;

import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.PreDestroy;
import com.dimachine.core.locator.ComponentTraits;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.TestBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultBeanFactoryTest {

    private DefaultBeanFactory beanFactory;
    private final SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
            .className(TargetBean.class.getName())
            .beanName("testBean")
            .build();

    @BeforeEach
    public void setUp() {
        beanFactory = new DefaultBeanFactory(new String[]{}) {
            @Override
            protected List<String> scanClasspath(ComponentTraits additionalPackages) {
                return Collections.emptyList();
            }
        };
    }

    @Test
    public void shouldBeAbleToGetBeanByName_whenItIsPresentInBeanFactory() {
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        Object bean = beanFactory.getBean("testBean");

        assertNotNull(bean);
        assertEquals(TargetBean.class, bean.getClass());
    }

    @Test
    public void shouldBeAbleToGetBeanByType_whenItIsPresentInBeanFactory() {
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        TargetBean bean = beanFactory.getBean(TargetBean.class);

        assertNotNull(bean);
        assertEquals(TargetBean.class, bean.getClass());
    }

    @Test
    public void shouldBeAbleToFindBeanByName_whenItIsPresentInBeanFactory() {
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        assertTrue(beanFactory.contains("testBean"));
    }

    @Test
    public void shouldBeAbleToFindPrototypeBeanByName_whenItIsPresentInBeanFactory() {
        BeanDefinition prototypeDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanAssignableClass(TargetBean.class)
                .beanName("targetBean")
                .scope(BeanScope.PROTOTYPE)
                .build();
        beanFactory.registerBeans(prototypeDefinition);
        beanFactory.refresh();

        assertTrue(beanFactory.contains("targetBean"));
    }

    @Test
    public void shouldBeAbleToGetBeanByAlias() {
        BeanDefinition prototypeDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanAssignableClass(TargetBean.class)
                .beanName("targetBean")
                .scope(BeanScope.SINGLETON)
                .aliases(List.of("target_bean"))
                .build();
        beanFactory.registerBeans(prototypeDefinition);
        beanFactory.refresh();

        assertNotNull(beanFactory.getBean("target_bean"));
        assertNotNull(beanFactory.getBean("target_bean", TargetBean.class));
        assertTrue(beanFactory.contains("target_bean"));
    }

    @Test
    public void shouldBeAbleToFindPrototypeBeanByType_whenItIsPresentInBeanFactory() {
        BeanDefinition prototypeDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanAssignableClass(TargetBean.class)
                .beanName("targetBean")
                .scope(BeanScope.PROTOTYPE)
                .build();
        beanFactory.registerBeans(prototypeDefinition);
        beanFactory.refresh();

        assertTrue(beanFactory.contains(TargetBean.class));
    }

    @Test
    public void shouldBeAbleToFindBeanByType_whenItIsPresentInBeanFactory() {
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        assertTrue(beanFactory.contains(TargetBean.class));
    }

    @Test
    public void shouldNotBeAbleToFindBeanByType_whenItIsNotPresentInBeanFactory() {
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        assertFalse(beanFactory.contains(ObjectFactory.class));
    }

    @Test
    public void shouldNotFindBeanByName_whenItIsNotPresentInBeanFactory() {
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        assertFalse(beanFactory.contains("transactionManager"));
    }

    @Test
    public void shouldBeAbleToGetBeanByTypeAndName_whenItIsPresentInBeanFactory() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanName("testBean")
                .build();
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        TargetBean bean = beanFactory.getBean("testBean", TargetBean.class);

        assertNotNull(bean);
        assertEquals(TargetBean.class, bean.getClass());
    }

    @Test
    public void shouldBeAbleToGetPrototypeBeanByType() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanName("testBean")
                .scope(BeanScope.PROTOTYPE)
                .build();
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        TargetBean firstPrototype = beanFactory.getBean(TargetBean.class);
        TargetBean secondPrototype = beanFactory.getBean(TargetBean.class);

        assertNotNull(firstPrototype);
        assertNotNull(secondPrototype);
        assertEquals(TargetBean.class, firstPrototype.getClass());
        assertEquals(TargetBean.class, secondPrototype.getClass());
        assertFalse(beanFactory.containsSingleton(firstPrototype.getClass()));
        assertFalse(beanFactory.containsSingleton(secondPrototype.getClass()));
        assertNotSame(firstPrototype, secondPrototype);
    }

    @Test
    public void shouldBeAbleToGetPrototypeBeanByName() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanName("targetBean")
                .scope(BeanScope.PROTOTYPE)
                .build();
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        TargetBean firstPrototype = (TargetBean) beanFactory.getBean("targetBean");
        TargetBean secondPrototype = (TargetBean) beanFactory.getBean("targetBean");

        assertNotNull(firstPrototype);
        assertNotNull(secondPrototype);
        assertEquals(TargetBean.class, firstPrototype.getClass());
        assertEquals(TargetBean.class, secondPrototype.getClass());
        assertFalse(beanFactory.containsSingleton(firstPrototype.getClass()));
        assertFalse(beanFactory.containsSingleton(secondPrototype.getClass()));
        assertNotSame(firstPrototype, secondPrototype);
    }

    @Test
    public void shouldBeAbleToGetPrototypeBeanByTypeAndName() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanName("targetBean")
                .scope(BeanScope.PROTOTYPE)
                .build();
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        TargetBean firstPrototype = beanFactory.getBean("targetBean", TargetBean.class);
        TargetBean secondPrototype = beanFactory.getBean("targetBean", TargetBean.class);

        assertNotNull(firstPrototype);
        assertNotNull(secondPrototype);
        assertEquals(TargetBean.class, firstPrototype.getClass());
        assertEquals(TargetBean.class, secondPrototype.getClass());
        assertFalse(beanFactory.containsSingleton(firstPrototype.getClass()));
        assertFalse(beanFactory.containsSingleton(secondPrototype.getClass()));
        assertNotSame(firstPrototype, secondPrototype);
    }

    @Test
    public void shouldBeAbleToGetBeanByAssignableTypeAndName_whenItIsPresentInBeanFactory() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanName("testBean")
                .build();
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();

        Comparable<?> bean = beanFactory.getBean("testBean", Comparable.class);

        assertNotNull(bean);
        assertEquals(TargetBean.class, bean.getClass());
    }

    @Test
    public void shouldThrowNoSuchBeanDefinitionException_whenGettingBeanByNameFromEmptyBeanFactory() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> beanFactory.getBean("foo"));
    }

    @Test
    public void shouldThrowNoSuchBeanDefinitionException_whenGettingBeanByTypeFromBeanFactory() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> beanFactory.getBean(TargetBean.class));
    }

    @Test
    public void shouldThrowNoSuchBeanDefinitionException_whenGettingBeanByTypeAndNameFromBeanFactory() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> beanFactory.getBean("foo", TargetBean.class));
    }

    @Test
    public void shouldSortBeanPostProcessorsAccordingToTheirOrder() {
        SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanName("firstBean")
                .build();
        SimpleBeanDefinition beanPostProcessorDefinition = SimpleBeanDefinition.builder()
                .className(YetAnotherTargetBean.class.getName())
                .beanName("secondBean")
                .build();
        beanFactory.registerBeans(beanDefinition, beanPostProcessorDefinition);
        beanFactory.refresh();

        Map<String, TargetBean> beansMapOfType = beanFactory.getBeansMapOfType(TargetBean.class);

        assertEquals(2, beansMapOfType.size());
        assertNotNull(beansMapOfType.get("firstBean"));
        assertTrue(beansMapOfType.get("secondBean") instanceof YetAnotherTargetBean);
    }

    @Test
    public void shouldBeAbleToGetAllSingletonsOfGivenType() {
        SimpleBeanDefinition secondBeanDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanName("yetAnotherTargetBean")
                .build();
        beanFactory.registerBeans(beanDefinition, secondBeanDefinition);
        beanFactory.refresh();

        List<TargetBean> beans = beanFactory.getAllBeansOfType(TargetBean.class);

        assertEquals(2, new HashSet<>(beans).size());
    }

    @Test
    public void shouldCloseDisposableBeansUponBeanFactoryClosing() throws Exception {
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();
        TargetBean.wasDestroyed = false;

        beanFactory.close();

        assertTrue(TargetBean.wasDestroyed);
    }

    @Test
    public void shouldCallPreDestroyMethodsUponBeanFactoryClosing() throws Exception {
        beanFactory.registerBeans(beanDefinition);
        beanFactory.refresh();
        TargetBean.preDestroyMethodCalled = false;

        beanFactory.close();

        assertTrue(TargetBean.preDestroyMethodCalled);
    }

    @Test
    public void shouldBeAbleToGetAllBeanDefinitions() {
        beanFactory.registerBeans(beanDefinition);

        Set<BeanDefinition> allBeanDefinitions = beanFactory.getBeanDefinitions();

        assertEquals(Set.of(beanDefinition), allBeanDefinitions);
    }

    @Test
    public void whenNoBeanDefinitionsAreRegistered_getBeanDefinitionsShouldReturnEmptyCollection() {
        assertEquals(0, beanFactory.getBeanDefinitions().size());
    }

    @Test
    public void shouldBeAbleToRegisterJavaConfigClasses_afterBeanFactoryCreation() {
        beanFactory.register(AppConfig.class);
        beanFactory.refresh();

        AppConfig config = beanFactory.getBean(AppConfig.class);
        assertNotNull(config);
    }

    @Test
    public void shouldBeAbleToCheckIfBeanFactoryContainsGivenSingletonBean() {
        SimpleBeanDefinition singletonBeanDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanName("testBean")
                .scope(BeanScope.SINGLETON)
                .build();
        beanFactory.registerBeans(singletonBeanDefinition);
        beanFactory.refresh();

        assertTrue(beanFactory.containsSingleton(TargetBean.class));
    }

    @Test
    public void whenGivenAPrototypeBean_containsSingletonShouldReturnFalse() {
        SimpleBeanDefinition singletonBeanDefinition = SimpleBeanDefinition.builder()
                .className(TargetBean.class.getName())
                .beanName("testBean")
                .scope(BeanScope.PROTOTYPE)
                .build();
        beanFactory.registerBeans(singletonBeanDefinition);
        beanFactory.refresh();

        assertFalse(beanFactory.containsSingleton(TargetBean.class));
    }

    @Configuration
    static class AppConfig {
    }

    private static class TargetBean implements Comparable<TargetBean>, DisposableBean {
        private static boolean wasDestroyed;
        private static boolean preDestroyMethodCalled;

        public TargetBean() {
        }

        @Override
        public int compareTo(TargetBean other) {
            return 0;
        }

        @Override
        public void destroy() {
            wasDestroyed = true;
        }

        @PreDestroy
        private void close() {
            preDestroyMethodCalled = true;
        }
    }

    private static class YetAnotherTargetBean extends TargetBean {
    }
}
