package com.dimachine.core;

import com.dimachine.core.annotation.Ordered;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultBeanFactory extends AbstractBeanRegistry implements BeanFactory, BeanRegistry {
    private final ClasspathScanner classpathScanner;
    private final BeanDefinitionMaker beanDefinitionMaker = new DefaultBeanDefinitionMaker();
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public DefaultBeanFactory(String... packagesToScan) {
        String currentPackage = getCurrentPackage();
        this.classpathScanner = new ClasspathScanner(makePackagesToScan(currentPackage, packagesToScan));
    }

    protected String[] makePackagesToScan(String currentPackage, String[] packagesToScan) {
        List<String> resultingPackages = new ArrayList<>();
        resultingPackages.add(currentPackage);
        resultingPackages.addAll(Arrays.asList(packagesToScan));
        return resultingPackages.toArray(new String[0]);
    }

    private String getCurrentPackage() {
        return getClass().getPackageName();
    }

    @Override
    public Object getBean(String name) {
        return singletonBeans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().getBeanName().equals(name))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean with name " + name + " found"));
    }

    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        return singletonBeans.entrySet()
                .stream()
                .filter(beanEntry -> beanEntry.getKey().getBeanName().equals(name))
                .filter(beanEntry -> clazz.isAssignableFrom(beanEntry.getKey().getBeanClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(clazz::cast)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean with name " + name + " and type " + clazz + " found"));
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        BeanDefinition foundBeanDefinition = getBeanDefinition(clazz);
        if (foundBeanDefinition.isPrototype()) {
            return objectFactory.instantiate(clazz, this);
        }
        return singletonBeans.entrySet()
                .stream()
                .filter(entry -> clazz.isAssignableFrom(entry.getKey().getBeanClass()))
                .findFirst()
                .map(Map.Entry::getValue)
                .map(clazz::cast)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean with of type " + clazz + " found"));
    }

    @Override
    public boolean contains(String beanName) {
        return singletonBeans.entrySet()
                .stream()
                .anyMatch(beanEntry -> beanEntry.getKey().getBeanName().equals(beanName));
    }

    @Override
    public <T> boolean contains(Class<T> clazz) {
        return singletonBeans.entrySet()
                .stream()
                .anyMatch(beanEntry -> clazz.isAssignableFrom(beanEntry.getKey().getBeanClass()));
    }

    @Override
    public <T> boolean containsBeanDefinitionOfType(Class<T> clazz) {
        return beanDefinitions.stream()
                .anyMatch(beanDefinition -> clazz.isAssignableFrom(beanDefinition.getBeanClass()));
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> beanClass) {
        return beanDefinitions.stream()
                .filter(beanDefinition -> beanClass.isAssignableFrom(beanDefinition.getBeanClass()))
                .findFirst()
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean definition of type " + beanClass + " found"));
    }

    @Override
    public void registerSingleton(BeanDefinition beanDefinition, Object instance) {
        if (beanDefinition.isSingleton()) {
            singletonBeans.put(beanDefinition, instance);
            beanDefinitions.add(beanDefinition);
        }
    }

    @Override
    public <T> List<T> getAllBeansOfType(Class<T> clazz) {
        return singletonBeans.entrySet()
                .stream()
                .filter(beanEntry -> clazz.isAssignableFrom(beanEntry.getKey().getBeanClass()))
                .map(Map.Entry::getValue)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    @Override
    public void refresh() {
        List<String> scannedClasses = scanClasspath();
        List<BeanDefinition> beanDefinitions = scannedClasses.stream()
                .map(beanDefinitionMaker::makeBeanDefinition)
                .collect(Collectors.toList());
        beanDefinitions.forEach(this::registerBeans);
        registerBeanFactory();
        instantiateSingletonBeans();
        invokeBeanPostProcessors();
    }

    private void registerBeanFactory() {
        BeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(getClass().getName())
                .beanName("beanFactory")
                .build();
        beanDefinitions.add(beanDefinition);
        singletonBeans.put(beanDefinition, this);
    }

    protected List<String> scanClasspath() {
        return classpathScanner.scan();
    }

    private void invokeBeanPostProcessors() {
        orderBeanPostProcessors();
        postProcessBeforeInitialisation();
        postProcessAfterInitialisation();
    }

    private void orderBeanPostProcessors() {
        beanPostProcessors.sort(new Comparator<>() {
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
        });
    }

    private void postProcessBeforeInitialisation() {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            for (Map.Entry<BeanDefinition, Object> singletonBean : singletonBeans.entrySet()) {
                Object bean = singletonBean.getValue();
                String beanName = singletonBean.getKey().getBeanName();
                if (!isBeanPostProcessor(bean)) {
                    Object newBeanInstance = beanPostProcessor.postProcessBeforeInitialisation(bean, beanName);
                    singletonBean.setValue(newBeanInstance);
                }
            }
        }
    }

    private void postProcessAfterInitialisation() {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            for (Map.Entry<BeanDefinition, Object> singletonBean : singletonBeans.entrySet()) {
                Object bean = singletonBean.getValue();
                String beanName = singletonBean.getKey().getBeanName();
                if (!isBeanPostProcessor(bean)) {
                    Object newBeanInstance = beanPostProcessor.postProcessAfterInitialisation(bean, beanName);
                    singletonBean.setValue(newBeanInstance);
                }
            }
        }
    }

    private void instantiateSingletonBeans() {
        List<BeanDefinition> beanDefinitionsToProcess = new ArrayList<>(this.beanDefinitions);
        beanDefinitionsToProcess.forEach(this::makeSingletonIfNeeded);
    }

    private void makeSingletonIfNeeded(BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton() && !singletonBeans.containsKey(beanDefinition)) {
            Object beanInstance = objectFactory.instantiate(beanDefinition.getBeanClass(), this);
            if (isBeanPostProcessor(beanInstance)) {
                beanPostProcessors.add((BeanPostProcessor) beanInstance);
            }
            singletonBeans.put(beanDefinition, beanInstance);
        }
    }

    private boolean isBeanPostProcessor(Object beanInstance) {
        return beanInstance instanceof BeanPostProcessor ||
                beanInstance instanceof BeanFactory;
    }
}
