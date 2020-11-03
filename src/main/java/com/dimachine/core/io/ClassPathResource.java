package com.dimachine.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ClassPathResource implements Resource {
    private final String path;

    public ClassPathResource(String path) {
        this.path = path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return ClassLoader.getSystemResourceAsStream(path);
    }

    @Override
    public boolean exists() {
        return ClassLoader.getSystemResource(path) != null;
    }

    @Override
    public URL getURL() {
        return ClassLoader.getSystemResource(path);
    }
}
