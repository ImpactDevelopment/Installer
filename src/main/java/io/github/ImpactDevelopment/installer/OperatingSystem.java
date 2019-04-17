package io.github.ImpactDevelopment.installer;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    public static Path getMinecraftDirectory() {
        switch (getOS()) {
            case WINDOWS: return Paths.get(System.getenv("APPDATA")).resolve(".minecraft");
            case OSX:     return Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Application Support").resolve("minecraft");
            default:      return Paths.get(System.getProperty("user.home")).resolve(".minecraft");
        }
    }
}
