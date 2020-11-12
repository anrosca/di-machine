package com.dimachine.core;

import com.dimachine.core.annotation.Component;
import com.dimachine.core.annotation.Configuration;
import com.dimachine.core.annotation.Service;
import com.dimachine.core.locator.ComponentFilter;
import com.dimachine.core.locator.ComponentPackageLocator;
import com.dimachine.core.locator.ComponentTraits;
import com.dimachine.core.postprocessor.BeanPostProcessorOrderedComparator;
import com.dimachine.core.type.ClassMetadata;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DefaultBeanFactory extends AbstractBeanFactory {
    private static final String BEAN_FACTORY_BEAN_NAME = "beanFactory";
    private static final List<Class<? extends Annotation>> targetAnnotations =
            List.of(Component.class, Service.class, Configuration.class);

    private final Map<BeanDefinition, Object> singletonBeans = new ConcurrentHashMap<>();
    private final DefaultObjectFactory objectFactory = new DefaultObjectFactory();
    private final ClasspathScanner classpathScanner;
    private final BeanDefinitionMaker beanDefinitionMaker = new DefaultBeanDefinitionMaker();
    private final List<BeanPostProcessor> beanPostProcessors = Collections.synchronizedList(new ArrayList<>());
    private final AnnotationConfigObjectFactory configObjectFactory = new AnnotationConfigObjectFactory(this);
    private final AtomicBoolean wasClosed = new AtomicBoolean();

    public DefaultBeanFactory(String... packagesToScan) {
        this.classpathScanner = new ClasspathScanner(targetAnnotations, packagesToScan);
    }

    public DefaultBeanFactory(Class<?>... configurationClasses) {
        this.classpathScanner = new ClasspathScanner(targetAnnotations);
        register(configurationClasses);
    }

    public DefaultBeanFactory() {
        this.classpathScanner = new ClasspathScanner(targetAnnotations);
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
        BeanDefinition foundBeanDefinition = getBeanDefinition(name)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean definition with " + name + " found"));
        if (foundBeanDefinition.isPrototype()) {
            return instantiatePrototype(foundBeanDefinition);
        }
        return singletonBeans.entrySet()
                .stream()
                .filter(entry -> foundBeanDefinition.isCompatibleWith(entry.getKey().getBeanName()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean with name " + name + " found"));
    }

    @Override
    public <T> T getBean(String beanName, Class<T> clazz) {
        BeanDefinition foundBeanDefinition = getBeanDefinition(beanName)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean definition with " + beanName + " and type " + clazz + " found"));
        if (!foundBeanDefinition.isCompatibleWith(beanName, clazz)) {
            throw new NoSuchBeanDefinitionException("No bean definition with " + beanName + " and type " + clazz + " found");
        }
        return getBean(clazz);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        BeanDefinition foundBeanDefinition = getBeanDefinition(clazz)
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean definition of type " + clazz + " found"));
        if (foundBeanDefinition.isPrototype()) {
            return instantiatePrototype(foundBeanDefinition);
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
    private <T> T instantiatePrototype(BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        if (beanDefinition.getClassName() == null) {
            ObjectProvider objectProvider = beanDefinition.getObjectProvider();
            return (T) postProcessBeanInstance(objectProvider.makeObject(this), beanName);
        }
        return (T) postProcessBeanInstance(objectFactory.instantiate(beanDefinition.getRealBeanClass(), this), beanName);
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

    private boolean contains(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton()) {
            return singletonBeans.entrySet()
                    .stream()
                    .anyMatch(beanEntry -> beanDefinition.isCompatibleWith(beanEntry.getKey().getBeanName()));
        }
        return beanDefinition.isCompatibleWith(beanName);
    }

    @Override
    public <T> boolean contains(Class<T> clazz) {
        return getBeanDefinition(clazz)
                .map(beanDefinition -> contains(clazz, beanDefinition))
                .orElse(false);
    }

    private <T> boolean contains(Class<T> clazz, BeanDefinition beanDefinition) {
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
        makeEnvironment();
        loadFactories();
        BeanDefinition[] beanDefinitions = scanBeanDefinitions();
        registerBeans(beanDefinitions);
        registerBeanFactory();
        instantiateSingletonBeans();
        invokeBeanPostProcessors();
    }

    private BeanDefinition[] scanBeanDefinitions() {
        return scanClasspath(findPackagesToScan())
                .stream()
                .map(beanDefinitionMaker::makeBeanDefinition)
                .toArray(BeanDefinition[]::new);
    }

    private ComponentTraits findPackagesToScan() {
        List<? extends Class<?>> classesToScan = beanDefinitions.stream()
                .map(BeanDefinition::getRealBeanClass)
                .collect(Collectors.toList());
        ComponentPackageLocator locator = new ComponentPackageLocator();
        return locator.locate(classesToScan);
    }

    protected void loadFactories() {
        DiMachineFactoriesLoader factoriesLoader = new DiMachineFactoriesLoader();
        factoriesLoader.load().forEach(this::registerBeans);
    }

    private void registerBeanFactory() {
        BeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                .className(getClass().getName())
                .beanName(BEAN_FACTORY_BEAN_NAME)
                .build();
        beanDefinitions.add(beanDefinition);
        singletonBeans.put(beanDefinition, this);
    }

    protected List<String> scanClasspath(ComponentTraits componentTraits) {
        ComponentFilter componentFilter = componentTraits.getComponentFilter();
        return classpathScanner.scan(getAdditionalPackages(componentTraits))
                .stream()
                .filter(componentFilter::matches)
                .map(ClassMetadata::getClassName)
                .collect(Collectors.toList());
    }

    private List<String> getAdditionalPackages(ComponentTraits componentTraits) {
        return componentTraits.getComponentPackages()
                .stream()
                .distinct()
                .collect(Collectors.toList());
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

    private void postProcessBeforeInitialisation() {
        for (Map.Entry<BeanDefinition, Object> singletonBean : singletonBeans.entrySet()) {
            String beanName = singletonBean.getKey().getBeanName();
            Object newBeanInstance = postProcessBeanBeforeInitialisation(singletonBean.getValue(), beanName);
            singletonBean.setValue(newBeanInstance);
        }
    }

    private Object postProcessBeanBeforeInitialisation(Object bean, String beanName) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.postProcessBeforeInitialisation(bean, beanName);
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
            bean = beanPostProcessor.postProcessAfterInitialisation(bean, beanName);
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
            singletonBeans.put(beanDefinition, beanInstance);
            processConfigClass(beanDefinition, beanInstance);
            if (isBeanPostProcessor(beanInstance)) {
                beanPostProcessors.add((BeanPostProcessor) beanInstance);
            }
        }
    }

    private void processConfigClass(BeanDefinition beanDefinition, Object beanInstance) {
        if (isConfigurationClass(beanDefinition.getRealBeanClass())) {
            Class<?> originalConfigClass = beanDefinition.getRealBeanClass();
            configObjectFactory.instantiateSingletonsFromConfigClass(beanInstance, originalConfigClass);
            scanNestedClasses(originalConfigClass.getDeclaredClasses());
        }
    }

    private void scanNestedClasses(Class<?>[] declaredClasses) {
        for (Class<?> configClass : declaredClasses) {
            if (isConfigurationClass(configClass)) {
                BeanDefinition beanDefinition = beanDefinitionMaker.makeBeanDefinition(configClass.getName());
                makeSingletonIfNeeded(beanDefinition);
            }
        }
    }

    private boolean isConfigurationClass(Class<?> beanClass) {
        return beanClass.isAnnotationPresent(Configuration.class);
    }

    private boolean isBeanPostProcessor(Object beanInstance) {
        return beanInstance instanceof BeanPostProcessor ||
                beanInstance instanceof BeanFactory;
    }
}
