package com.dimachine.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface Resource {

    InputStream getInputStream() throws IOException;

    boolean exists();

    URL getURL();
}
