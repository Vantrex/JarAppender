package de.vantrex.jarappender.impl;

import com.google.common.base.Suppliers;
import de.vantrex.jarappender.ClassPathAppender;
import de.vantrex.jarappender.exception.ClassLoaderNotProvidedException;
import de.vantrex.jarappender.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * The default Classpath Appender, appends jars from the disk
 */
public class DefaultClassPathAppender implements ClassPathAppender<Path> {

    private static final Logger LOGGER = LogManager.getLogger(DefaultClassPathAppender.class);

    private URLClassLoader classLoader;
    private final Supplier<Method> addUrlMethod;

    public DefaultClassPathAppender() {
        this.addUrlMethod = Suppliers.memoize(() -> {
            if (Util.isJava9OrNewer()) {
                LOGGER.info("It is safe to ignore any warning printed following this message " +
                        "starting with 'WARNING: An illegal reflective access operation has occurred, Illegal reflective " +
                        "access by " + getClass().getName() + "'. This is intended, and will not have any impact on the " +
                        "operation of the software.");
            }
            try {
                Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrlMethod.setAccessible(true);
                return addUrlMethod;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void addToClassPath(Path path) throws ClassLoaderNotProvidedException {
        if (this.classLoader == null) {
            throw new ClassLoaderNotProvidedException("No Classloader provided!");
        }
        try {
            this.addUrlMethod.get().invoke(this.classLoader, path.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClassLoader(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}