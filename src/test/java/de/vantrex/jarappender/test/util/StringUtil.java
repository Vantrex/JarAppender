package de.vantrex.jarappender.test.util;

public class StringUtil {

    public static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    public static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }
}
