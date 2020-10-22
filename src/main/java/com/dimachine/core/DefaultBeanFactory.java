package com.dimachine.core;

import com.dimachine.core.annotation.Component;
import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Ordered;
import com.dimachine.core.annotation.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DefaultBeanFactory extends AbstractBeanDefinitionRegistry implements BeanFactory, BeanDefinitionRegistry {
    private static final List<Class<?>> targetAnnotations = List.of(Component.class, Service.class, Configuration.class);

    private final Map<BeanDefinition, Object> singletonBeans = new ConcurrentHashMap<>();
    private final DefaultObjectFactory objectFactory = new DefaultObjectFactory();
    private final ClasspathScanner classpathScanner;
    private final BeanDefinitionMaker beanDefinitionMaker = new DefaultBeanDefinitionMaker();
    private final List<BeanPostProcessor> beanPostProcessors = Collections.synchronizedList(new ArrayList<>());
    private final ProxyFactory proxyFactory = new DefaultProxyFactory();
    private final AnnotationConfigObjectFactory configObjectFactory = new AnnotationConfigObjectFactory(this);
    private final AtomicBoolean wasClosed = new AtomicBoolean();

    public DefaultBeanFactory(String... packagesToScan) {
        this.classpathScanner = new ClasspathScanner(targetAnnotations, packagesToScan);
    }

    public DefaultBeanFactory(Class<?>... configurationClasses) {
        this.classpathScanner = new ClasspathScanner(targetAnnotations);
        register(configurationClasses);
    }

    @Override
    public void register(Class<?>... configurationClasses) {
        BeanDefinition[] beanDefinitions = Arrays.stream(configurationClasses)
                .map(configClass -> beanDefinitionMaker.makeBeanDefinition(configClass.getName()))
                .toArray(BeanDefinition[]::new);
        registerBeans(beanDefinitions);
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
                .filter(beanEntry -> clazz.isAssignableFrom(beanEntry.getKey().getBeanAssignableClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(clazz::cast)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean with name " + name + " and type " + clazz + " found"));
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        BeanDefinition foundBeanDefinition = getBeanDefinition(clazz)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean definition of type " + clazz + " found"));
        if (foundBeanDefinition.isPrototype()) {
            return instantiatePrototype(clazz, foundBeanDefinition);
        }
        return singletonBeans.entrySet()
                .stream()
                .filter(entry -> clazz.isAssignableFrom(entry.getValue().getClass()))
                .findFirst()
                .map(Map.Entry::getValue)
                .map(clazz::cast)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean with of type " + clazz + " found"));
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiatePrototype(Class<T> clazz, BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        if (beanDefinition.getClassName() == null) {
            ObjectProvider objectProvider = beanDefinition.getObjectProvider();
            return (T) postProcessBeanInstance(objectProvider.makeObject(this), beanName);
        }
        return (T) postProcessBeanInstance(objectFactory.instantiate(clazz, this), beanName);
    }

    private Object postProcessBeanInstance(Object beanInstance, String beanName) {
        beanInstance = postProcessBeanBeforeInitialisation(beanInstance, beanName);
        return postProcessBeanAfterInitialisation(beanInstance, beanName);
    }

    @Override
    public boolean contains(String beanName) {
        return getBeanDefinition(beanName)
                .map(beanDefinition -> contains(beanName, beanDefinition))
                .orElse(false);
    }

    private Boolean contains(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton()) {
            return singletonBeans.entrySet()
                    .stream()
                    .anyMatch(beanEntry -> beanName.equals(beanEntry.getKey().getBeanName()));
        }
        return beanName.equals(beanDefinition.getBeanName());
    }

    @Override
    public <T> boolean contains(Class<T> clazz) {
        return getBeanDefinition(clazz)
                .map(beanDefinition -> contains(clazz, beanDefinition))
                .orElse(false);
    }

    private <T> Boolean contains(Class<T> clazz, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton()) {
            return containsSingleton(clazz);
        }
        return clazz.isAssignableFrom(beanDefinition.getBeanAssignableClass());
    }

    @Override
    public <T> boolean containsSingleton(Class<T> singletonClass) {
        return singletonBeans.entrySet()
                .stream()
                .anyMatch(beanEntry -> singletonClass.isAssignableFrom(beanEntry.getKey().getBeanAssignableClass()));
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
                .filter(beanEntry -> clazz.isAssignableFrom(beanEntry.getKey().getBeanAssignableClass()))
                .map(Map.Entry::getValue)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    @Override
    public <T> Map<String, T> getBeansMapOfType(Class<T> clazz) {
        return singletonBeans.entrySet()
                .stream()
                .filter(beanEntry -> clazz.isAssignableFrom(beanEntry.getKey().getBeanAssignableClass()))
                .collect(Collectors.toMap(beanEntry -> beanEntry.getKey().getBeanName(), beanEntry -> clazz.cast(beanEntry.getValue())));
    }

    @Override
    public void close() throws Exception {
        if (wasClosed.compareAndSet(false, true)) {
            invokeDisposableBeans();
            clearBeanFactory();
        }
    }

    @Override
    public void refresh() {
        loadFactories();
        BeanDefinition[] beanDefinitions = scanClasspath().stream()
                .map(beanDefinitionMaker::makeBeanDefinition)
                .toArray(BeanDefinition[]::new);
        registerBeans(beanDefinitions);
        registerBeanFactory();
        instantiateSingletonBeans();
        invokeBeanPostProcessors();
    }

    private void loadFactories() {
        DiMachineFactoriesLoader factoriesLoader = new DiMachineFactoriesLoader();
        factoriesLoader.load().forEach(this::registerBeans);
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
        beanPostProcessors.sort(new BeanPostProcessorOrderedComparator());
    }

    private void invokeDisposableBeans() throws Exception {
        for (Object singletonBean : singletonBeans.values()) {
            if (singletonBean instanceof DisposableBean disposableBean) {
                disposableBean.destroy();
            }
        }
    }

    private void clearBeanFactory() {
        beanPostProcessors.clear();
        singletonBeans.clear();
        beanDefinitions.clear();
        beanNames.clear();
    }

    private static class BeanPostProcessorOrderedComparator implements Comparator<BeanPostProcessor> {
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
    }

    private void postProcessBeforeInitialisation() {
        for (Map.Entry<BeanDefinition, Object> singletonBean : singletonBeans.entrySet()) {
            String beanName = singletonBean.getKey().getBeanName();
            Object newBeanInstance = postProcessBeanBeforeInitialisation(singletonBean.getValue(), beanName);
            singletonBean.setValue(newBeanInstance);
        }
    }

    private Object postProcessBeanBeforeInitialisation(Object bean, String beanName) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            if (!isBeanPostProcessor(bean)) {
                bean = beanPostProcessor.postProcessBeforeInitialisation(bean, beanName);
            }
        }
        return bean;
    }

    private void postProcessAfterInitialisation() {
        for (Map.Entry<BeanDefinition, Object> singletonBean : singletonBeans.entrySet()) {
            String beanName = singletonBean.getKey().getBeanName();
            Object newBeanInstance = postProcessBeanAfterInitialisation(singletonBean.getValue(), beanName);
            singletonBean.setValue(newBeanInstance);
        }
    }

    private Object postProcessBeanAfterInitialisation(Object bean, String beanName) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            if (!isBeanPostProcessor(bean)) {
                bean = beanPostProcessor.postProcessAfterInitialisation(bean, beanName);
            }
        }
        return bean;
    }

    private void instantiateSingletonBeans() {
        List<BeanDefinition> beanDefinitionsToProcess = new ArrayList<>(this.beanDefinitions);
        beanDefinitionsToProcess.forEach(this::makeSingletonIfNeeded);
    }

    private void makeSingletonIfNeeded(BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton() && !singletonBeans.containsKey(beanDefinition)) {
            Object beanInstance = objectFactory.instantiate(beanDefinition.getRealBeanClass(), this);
            if (isConfigurationBean(beanDefinition.getRealBeanClass())) {
                Class<?> originalConfigClass = beanDefinition.getRealBeanClass();
                if (shouldProxyConfigClass(originalConfigClass)) {
                    beanInstance = proxyFactory.proxyConfigurationClass(beanInstance, beanDefinition, this);
                }
                configObjectFactory.instantiateSingletonsFromConfigClass(beanInstance, originalConfigClass);
            }
            if (isBeanPostProcessor(beanInstance)) {
                beanPostProcessors.add((BeanPostProcessor) beanInstance);
            }
            singletonBeans.put(beanDefinition, beanInstance);
        }
    }

    private boolean shouldProxyConfigClass(Class<?> originalConfigClass) {
        Configuration configuration = originalConfigClass.getAnnotation(Configuration.class);
        return configuration.proxyBeanMethods();
    }

    private boolean isConfigurationBean(Class<?> beanClass) {
        return beanClass.isAnnotationPresent(Configuration.class);
    }

    private boolean isBeanPostProcessor(Object beanInstance) {
        return beanInstance instanceof BeanPostProcessor ||
                beanInstance instanceof BeanFactory;
    }
}
