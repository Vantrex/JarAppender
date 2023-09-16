package de.vantrex.jarappender.test;

import de.vantrex.jarappender.JarAppender;
import de.vantrex.jarappender.test.util.LibraryRepository;
import de.vantrex.jarappender.test.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URLClassLoader;
import java.nio.file.Files;

public class DefaultClassPathAppenderTest {

    private final String groupId = "it{}unimi{}dsi";
    private final String artifactId = "fastutil";
    private final String version = "8.1.0";


    /**
     * Downloads it.unimi.dsi.fastutil version 8.1.0 jar, writes it to the disk and adds it to the classpath
     */
    @Test
    public void testDefaultClassPathAppender() {

        final JarAppender appender = new JarAppender();
        appender.getAppender().setClassLoader((URLClassLoader) DefaultClassPathAppenderTest.class.getClassLoader());
        final String path = String.format(StringUtil.MAVEN_FORMAT,
                StringUtil.rewriteEscaping(groupId).replace(".", "/"),
                StringUtil.rewriteEscaping(artifactId),
                version,
                StringUtil.rewriteEscaping(artifactId),
                version
        );
        final File file = new File("test.jar");
        if (file.exists())
            file.delete();
        file.deleteOnExit();
        try {
            final byte[] data = LibraryRepository.MAVEN_CENTRAL.download(path);
            Files.write(file.toPath(), data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        appender.addJarToClassPath(file.toPath());
        try {
            Class.forName("it.unimi.dsi.fastutil.Arrays");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
