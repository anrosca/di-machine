package com.dimachine.core;

public class SimpleBeanDefinition implements BeanDefinition {
    private final String className;
    private final String beanName;
    private final BeanScope scope;

    private SimpleBeanDefinition(SimpleBeanDefinitionBuilder builder) {
        this.className = builder.className;
        this.beanName = builder.beanName;
        this.scope = builder.scope;
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
    public Class<?> getBeanClass() {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BeanClassMissingException("Bean class " + className +
                    " could not be loaded", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleBeanDefinition that = (SimpleBeanDefinition) o;

        if (!className.equals(that.className)) return false;
        if (!beanName.equals(that.beanName)) return false;
        return scope == that.scope;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
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

        public SimpleBeanDefinition build() {
            return new SimpleBeanDefinition(this);
        }
    }
}
