## DI Machine

A lightweight dependency injection framework copying (~~shamelessly~~) the Spring Framework.

### Maven dependency

Build the project using the following maven command:
`mvn clean install`

It will build and install the artifact into your local maven repository. 
After that, you'll be able to add the following maven dependency to your project's pom.


```xml
     <dependency>
          <groupId>org.example</groupId>
          <artifactId>di-machine</artifactId>
          <version>1.0-SNAPSHOT</version>
    </dependency>
```

### Examples

```java
    @Configuration
    public static class AppConfiguration {

        @Bean
        public TestBean testBean(FooService fooService) {
            return new TestBean(fooService);
        }

        @Bean
        public FooService fooService() {
            return new FooService();
        }
        
        public static void main(String[] args) {
            DefaultBeanFactory beanFactory = new DefaultBeanFactory(AppConfiguration.class);
            beanFactory.refresh();
            TestBean bean = beanFactory.getBean(TestBean.class);
        }   
    }
```

### License

The DI Machine Framework is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
