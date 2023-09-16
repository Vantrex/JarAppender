package de.vantrex.jarappender;

import de.vantrex.jarappender.exception.ClassLoaderNotProvidedException;

import java.net.URLClassLoader;

/**
 * @param <T> The "data" we want to append, e.g. can be a path or bytes directly
 */
public interface ClassPathAppender<T> {

    /**
     * Adds a jar/class to the classpath
     *
     * @throws ClassLoaderNotProvidedException if there is no class loader provided
     */
    void addToClassPath(T t) throws ClassLoaderNotProvidedException;

    /**
     * Provides the classloader
     *
     * @param classLoader The {@link URLClassLoader classloader} we want to provide
     */
    void setClassLoader(URLClassLoader classLoader);

    default void close() {

    };

}
