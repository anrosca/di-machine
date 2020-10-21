package com.dimachine.core;

import com.dimachine.core.annotation.PreDestroy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
            protected List<String> scanClasspath() {
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
        assertFalse(beanFactory.contains(firstPrototype.getClass()));
        assertFalse(beanFactory.contains(secondPrototype.getClass()));
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
        assertTrue(beansMapOfType.get("firstBean") instanceof TargetBean);
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
