package com.dimachine.core;

import java.util.List;

public class SimpleBeanDefinition implements BeanDefinition {
    private final String className;
    private final String beanName;
    private final BeanScope scope;
    private final Class<?> beanAssignableClass;
    private ObjectProvider objectProvider;
    private final List<String> aliases;

    private SimpleBeanDefinition(SimpleBeanDefinitionBuilder builder) {
        this.className = builder.className;
        this.beanName = builder.beanName;
        this.scope = builder.scope;
        this.beanAssignableClass = builder.beanAssignableClass;
        this.objectProvider = builder.objectProvider;
        this.aliases = builder.aliases;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public BeanScope getBeanScope() {
        return scope;
    }

    @Override
    public boolean isSingleton() {
        return scope == BeanScope.SINGLETON;
    }

    @Override
    public boolean isPrototype() {
        return !isSingleton();
    }

    @Override
    public Class<?> getRealBeanClass() {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BeanClassMissingException("Bean class " + className +
                    " could not be loaded", e);
        }
    }

    @Override
    public Class<?> getBeanAssignableClass() {
        return beanAssignableClass != null ? beanAssignableClass : getRealBeanClass();
    }

    @Override
    public ObjectProvider getObjectProvider() {
        return objectProvider;
    }

    @Override
    public void setObjectProvider(ObjectProvider objectProvider) {
        this.objectProvider = objectProvider;
    }

    @Override
    public boolean isCompatibleWith(String beanName, Class<?> clazz) {
        return isCompatibleWith(beanName) && clazz.isAssignableFrom(getBeanAssignableClass());
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean isCompatibleWith(String beanName) {
        return this.beanName.equals(beanName) || aliases.contains(beanName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleBeanDefinition that = (SimpleBeanDefinition) o;

        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        if (!beanName.equals(that.beanName)) return false;
        return scope == that.scope;
    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = 31 * result + beanName.hashCode();
        result = 31 * result + scope.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SimpleBeanDefinition{" +
                "className='" + className + '\'' +
                ", beanName='" + beanName + '\'' +
                ", scope=" + scope +
                '}';
    }

    public static SimpleBeanDefinitionBuilder builder() {
        return new SimpleBeanDefinitionBuilder();
    }

    public static class SimpleBeanDefinitionBuilder {
        private String className;
        private String beanName;
        private BeanScope scope = BeanScope.SINGLETON;
        private Class<?> beanAssignableClass;
        public ObjectProvider objectProvider;
        public List<String> aliases = List.of();

        public SimpleBeanDefinitionBuilder className(String className) {
            this.className = className;
            return this;
        }

        public SimpleBeanDefinitionBuilder beanName(String beanName) {
            this.beanName = beanName;
            return this;
        }

        public SimpleBeanDefinitionBuilder scope(BeanScope scope) {
            this.scope = scope;
            return this;
        }

        public SimpleBeanDefinitionBuilder beanAssignableClass(Class<?> beanAssignableClass) {
            this.beanAssignableClass = beanAssignableClass;
            return this;
        }

        public SimpleBeanDefinitionBuilder objectProvider(ObjectProvider objectProvider) {
            this.objectProvider = objectProvider;
            return this;
        }

        public SimpleBeanDefinitionBuilder aliases(List<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        public SimpleBeanDefinition build() {
            return new SimpleBeanDefinition(this);
        }
    }
}
