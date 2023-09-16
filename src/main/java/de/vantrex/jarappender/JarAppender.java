package de.vantrex.jarappender;

import de.vantrex.jarappender.exception.ClassLoaderNotProvidedException;
import de.vantrex.jarappender.impl.DefaultClassPathAppender;

public class JarAppender {

    private ClassPathAppender<?> appender;

    public JarAppender() {
        this.appender = new DefaultClassPathAppender();
    }

    public JarAppender(ClassPathAppender<?> appender) {
        this.appender = appender;
    }

    public void setAppender(ClassPathAppender<?> appender) {
        if (this.appender != null) {
            this.appender.close();
        }
        this.appender = appender;
    }

    public <T> void addJarToClassPath(T t) {
        ClassPathAppender<T> pathAppender = (ClassPathAppender<T>) this.appender;
        try {
            pathAppender.addToClassPath(t);
        } catch (ClassLoaderNotProvidedException e) {
            throw new RuntimeException(e);
        }
    }

    public ClassPathAppender<?> getAppender() {
        return appender;
    }
}