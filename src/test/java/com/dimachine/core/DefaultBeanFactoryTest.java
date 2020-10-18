package com.dimachine.core;

import com.dimachine.core._component.TestComponentWithoutExplicitName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultBeanFactoryTest {

    private final DefaultBeanFactory beanFactory = new DefaultBeanFactory();
    private final SimpleBeanDefinition beanDefinition = SimpleBeanDefinition.builder()
            .className(TargetBean.class.getName())
            .beanName("testBean")
            .build();

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

        assertTrue(beanFactory.contains(TestComponentWithoutExplicitName.class));
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

    private static class TargetBean implements Comparable<TargetBean> {
        @Override
        public int compareTo(TargetBean other) {
            return 0;
        }
    }
}
