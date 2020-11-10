package com.dimachine.core.io;

public class DefaultResourceLoader implements ResourceLoader {
    private static final String CLASSPATH_PREFIX = "classpath:";

    @Override
    public Resource getResource(String location) {
        if (location.startsWith(CLASSPATH_PREFIX)) {
            String targetLocation = location.substring(CLASSPATH_PREFIX.length());
            return new ClassPathResource(targetLocation);
        }
        return null;
    }
}
