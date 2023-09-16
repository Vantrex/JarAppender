package de.vantrex.jarappender.util;

/**
 * @author marinus
 */
public class Util {

    /**
     * Checks if the runtime Java version is 9 or higher
     *
     * @return true if the version is 9 or higher
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    public static boolean isJava9OrNewer() {
        try {
            // method was added in the Java 9 release
            Runtime.class.getMethod("version");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}