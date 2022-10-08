package jp.unaguna.mappedftp.utils;

import java.net.URL;

public class ClasspathUtils {
    public static URL getResource(String name) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        return ClasspathUtils.class.getClassLoader().getResource(name);
    }

    public static <T> Class<? extends T> getClass(String name, Class<T> base)
            throws ClassNotFoundException, ClassCastException {
        Class<?> cls = ClasspathUtils.class.getClassLoader().loadClass(name);
        return cls.asSubclass(base);
    }
}
