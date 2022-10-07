package jp.unaguna.mappedftp.internal;

import java.net.URL;

public class ClasspathUtils {
    public static URL getResource(String name) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        return ClasspathUtils.class.getClassLoader().getResource(name);
    }
}
