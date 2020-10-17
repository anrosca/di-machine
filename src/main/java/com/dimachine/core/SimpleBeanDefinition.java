package com.dimachine.core;

public class SimpleBeanDefinition implements BeanDefinition {
    private final String className;
    private final String beanName;
    private final Scope scope;

    public SimpleBeanDefinition(String className, String beanName) {
        this(className, beanName, Scope.SINGLETON);
    }

    public SimpleBeanDefinition(String className, String beanName, Scope scope) {
        this.className = className;
        this.beanName = beanName;
        this.scope = scope;
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
    public Scope getBeanScope() {
        return scope;
    }

    @Override
    public boolean isSingleton() {
        return scope == Scope.SINGLETON;
    }

    @Override
    public Class<?> getBeanClass() {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
}
