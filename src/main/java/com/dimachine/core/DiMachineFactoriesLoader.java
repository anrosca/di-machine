package com.dimachine.core;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class DiMachineFactoriesLoader {
    private static final String FACTORIES_RESOURCE_LOCATION = "/META-INF";
    private static final String FACTORIES_FILE_NAME = "di-machine.factories";

    public List<BeanDefinition> load() {
        Properties properties = new Properties();
        try (ScanResult scanResult = new ClassGraph().acceptPathsNonRecursive(FACTORIES_RESOURCE_LOCATION).scan()) {
            scanResult.getResourcesWithLeafName(FACTORIES_FILE_NAME)
                    .forEachInputStreamIgnoringIOException((resource, inputStream) -> loadProperties(properties, inputStream));
        }
        return toBeanDefinitions(postProcessFactories(properties));
    }

    private Map<String, List<String>> postProcessFactories(Properties properties) {
        Map<String, List<String>> factories = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String[] parts = ((String) entry.getValue()).split(",");
            factories.put((String) entry.getKey(), Arrays.asList(parts));
        }
        return factories;
    }

    private List<BeanDefinition> toBeanDefinitions(Map<String, List<String>> factories) {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        BeanNamer beanNamer = new DefaultBeanNamer();
        for (Map.Entry<String, List<String>> factoryEntry : factories.entrySet()) {
            for (String factoryClassName : factoryEntry.getValue()) {
                BeanDefinition beanDefinition = SimpleBeanDefinition.builder()
                        .className(factoryClassName)
                        .beanName(beanNamer.makeBeanName(factoryClassName))
                        .build();
                beanDefinitions.add(beanDefinition);
            }
        }
        return beanDefinitions;
    }

    protected void loadProperties(Properties propertiesCollector, InputStream inputStream) {
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            mergeProperties(properties, propertiesCollector);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void mergeProperties(Properties properties, Properties propertiesCollector) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            propertiesCollector.merge(entry.getKey(), entry.getValue(), (v1, v2) -> v1 + "," + v2);
        }
    }
}
