package de.vantrex.jarappender.test;

import de.vantrex.jarappender.ClassPathAppender;
import de.vantrex.jarappender.JarAppender;
import de.vantrex.jarappender.impl.MemoryClassPathAppender;
import de.vantrex.jarappender.test.util.LibraryRepository;
import de.vantrex.jarappender.test.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.net.URLClassLoader;

/**
 * This test tests the {@link MemoryClassPathAppender}
 */
public class MemoryClassPathAppenderTest {


    /**
     * Downloads the org.yaml.snakeyaml version 1.20 jar into memory and adds it to the classpath
     */
    @Test
    public void testMemoryClassPathAppender() {
        final String groupId = "org{}yaml";
        final String artifactId = "snakeyaml";
        final String version = "1.20";

        JarAppender appender = new JarAppender();
        final ClassPathAppender<byte[]> classPathAppender = new MemoryClassPathAppender();
        final String path = String.format(StringUtil.MAVEN_FORMAT,
                StringUtil.rewriteEscaping(groupId).replace(".", "/"),
                StringUtil.rewriteEscaping(artifactId),
                version,
                StringUtil.rewriteEscaping(artifactId),
                version
        );
        try {
            final byte[] data = LibraryRepository.MAVEN_CENTRAL.download(path);

            classPathAppender.setClassLoader((URLClassLoader) MemoryClassPathAppender.class.getClassLoader());
            appender.setAppender(classPathAppender);
            appender.addJarToClassPath(data);
            try {
                Class.forName("org.yaml.snakeyaml.Yaml");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}