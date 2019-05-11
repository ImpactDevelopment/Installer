package io.github.ImpactDevelopment.installer.libraries;

import com.google.gson.reflect.TypeToken;
import io.github.ImpactDevelopment.installer.Fetcher;
import io.github.ImpactDevelopment.installer.Installer;

import java.util.Map;

public class MavenResolver {
    private static final Map<String, String> MAVEN_MAP = getMavenMap();

    private static Map<String, String> getMavenMap() {
        return Installer.gson.fromJson(Fetcher.fetch("https://impactdevelopment.github.io/Resources/data/maven.refmap.json"), new TypeToken<Map<String, String>>() {}.getType());
    }

    public static String getURLBase(String mavenGroup) {
        String ret = MAVEN_MAP.get(mavenGroup);
        if (ret == null) {
            throw new IllegalArgumentException("Can't get URL for maven group " + mavenGroup);
        }
        return ret;
    }

    public static String getFullURL(String mavenName) {
        String[] parts = mavenName.split(":");
        return getURLBase(parts[0]) + partsToPath(parts);
    }

    public static String partsToPath(String[] parts) {
        return parts[0].replace(".", "/") + "/" + parts[1] + "/" + parts[2] + "/" + parts[1] + "-" + parts[2] + ".jar";
    }
}
