package de.vantrex.jarappender.impl;

import com.google.common.base.Suppliers;
import de.vantrex.jarappender.ClassPathAppender;
import de.vantrex.jarappender.exception.ClassLoaderNotProvidedException;
import de.vantrex.jarappender.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * The Memory Classpath Appender appends jars from memory.
 * The use case for this would be if you do not want to write the jar you want to append on the disk
 */
public class MemoryClassPathAppender implements ClassPathAppender<byte[]> {

    private static final Logger LOGGER = LogManager.getLogger(MemoryClassPathAppender.class);

    private URLClassLoader classLoader;
    private final Supplier<Method> addUrlMethod;

    public MemoryClassPathAppender() {
        this.addUrlMethod = Suppliers.memoize(() -> {
            if (Util.isJava9OrNewer()) {
                LOGGER.info("It is safe to ignore any warning printed following this message " + "starting with 'WARNING: An illegal reflective access operation has occurred, Illegal reflective " + "access by " + getClass().getName() + "'. This is intended, and will not have any impact on the " + "operation of the software.");
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
    public void addToClassPath(byte[] bytes) throws ClassLoaderNotProvidedException {
        if (this.classLoader == null) {
            throw new ClassLoaderNotProvidedException("No Classloader provided!");
        }
        final Map<String, byte[]> map = new HashMap<>();
        try (JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(bytes))) {
            for (; ; ) {
                JarEntry nextEntry = jarInputStream.getNextJarEntry();
                if (nextEntry == null) break;
                final int entrySize = (int) nextEntry.getSize();
                byte[] data = new byte[entrySize > 0 ? entrySize : 1024];
                int real = 0;
                for (int r = jarInputStream.read(data); r > 0; r = jarInputStream.read(data, real, data.length - real))
                    if (data.length == (real += r)) {
                        data = Arrays.copyOf(data, data.length * 2);
                    }
                if (real != data.length) {
                    data = Arrays.copyOf(data, real);
                }
                map.put("/" + nextEntry.getName(), data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final URL url = createUrl(map);
        try {
            addUrlMethod.get().invoke(this.classLoader, url);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClassLoader(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Creates a URL from the buffer
     *
     * @param pathMap the map we want to create url from
     * @return an url generated from the pathMap
     */
    private static URL createUrl(Map<String, byte[]> pathMap) {
        final URL url;
        try {
            final String uniqueId = UUID.randomUUID().toString();
            url = new URL("x-buffer", null, -1, "/" + uniqueId + "/", new URLStreamHandler() {
                protected URLConnection openConnection(URL u) throws IOException {
                    String filePath = u.getFile().substring(uniqueId.length() + 1);
                    final byte[] data = pathMap.get(filePath);
                    if (data == null) throw new FileNotFoundException(filePath);
                    return new URLConnection(u) {
                        public void connect() throws IOException {
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return new ByteArrayInputStream(data);
                        }
                    };
                }
            });
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }
}