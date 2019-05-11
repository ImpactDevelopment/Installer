package io.github.ImpactDevelopment.installer;

import static java.util.Locale.ROOT;

/**
 * @author Brady
 * @since 3/7/2019
 */
public enum OperatingSystem {

    WINDOWS,
    OSX,
    LINUX,
    UNKNOWN;

    public static OperatingSystem getOS() {
        String name = System.getProperty("os.name").toLowerCase(ROOT);
        if (name.contains("windows")) {
            return WINDOWS;
        }
        if (name.contains("mac")) {
            return OSX;
        }
        if (name.contains("linux") || name.contains("solaris") || name.contains("sunos") || name.contains("unix")) {
            return LINUX;
        }
        return UNKNOWN;
    }
}
