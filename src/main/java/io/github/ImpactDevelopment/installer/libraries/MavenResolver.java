/*
 * This file is part of Impact Installer.
 *
 * Copyright (C) 2019  ImpactDevelopment and contributors
 *
 * See the CONTRIBUTORS.md file for a list of copyright holders
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package io.github.ImpactDevelopment.installer.libraries;

import com.google.gson.reflect.TypeToken;
import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.utils.Fetcher;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenResolver {
    private static Map<String, String> MAVEN_MAP = null;

    private static Map<String, String> getMavenMap() {
        return Installer.gson.fromJson(Fetcher.fetch("https://impactdevelopment.github.io/Resources/data/maven.refmap.json"), new TypeToken<Map<String, String>>() {}.getType());
    }

    public static String getURLBase(String mavenGroup) {
        if (MAVEN_MAP == null) {
            // don't do this in the class initializer, so that if it fails we don't have a broken class that can't be referenced in the future
            MAVEN_MAP = getMavenMap();
        }
        String ret = MAVEN_MAP.get(mavenGroup);
        if (ret == null) {
            throw new IllegalArgumentException("Can't get URL for maven group " + mavenGroup);
        }
        return ret;
    }

    public static String getFullURL(String coords) {
        Artifact artifact = new Artifact(coords);
        String base = getURLBase(artifact.getGroupId());
        if (!base.endsWith("/")) {
            base += "/";
        }
        return base + artifact.getPath();
    }

    public static String getPath(String coords) {
        return new Artifact(coords).getPath();
    }

    public static String getFilename(String coords) {
        return new Artifact(coords).getFilename();
    }

    private static class Artifact {

        // org.eclipse.aether.artifact.DefaultArtifact uses <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>
        // however for some reason gradle, mojang, and multimc seem to agree on <group>:<name>:<version>[:classifier][@extension]
        private static final Pattern PATTERN = Pattern.compile("([^: ]+):([^: ]+):([^: ]+)(?::([^: ]+))?(?:@([^@ ]+))?");
        private static final String READABLE_PATTERN = "<group>:<name>:<version>[:classifier][@extension]";

        private final String groupId;
        private final String artifactId;
        private final String version;
        private final String extension;
        private final String classifier;

        public Artifact(String coords) throws IllegalArgumentException {
            Matcher m = PATTERN.matcher(coords);
            if (!m.matches()) {
                throw new IllegalArgumentException("Bad artifact coordinates " + coords + ", expected format is " + READABLE_PATTERN);
            }
            groupId = m.group(1);
            artifactId = m.group(2);
            version = m.group(3);
            classifier = get(m.group(4), "");
            extension = get(m.group(5), "jar");
        }

        private static String get(String value, String defaultValue) {
            return (value == null || value.isEmpty()) ? defaultValue : value;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public String getVersion() {
            return version;
        }

        public String getExtension() {
            return extension;
        }

        public String getClassifier() {
            return classifier;
        }

        public boolean hasClassifier() {
            return !getClassifier().isEmpty();
        }

        public String getFilename() {
            return getArtifactId() + "-" + getVersion() + (hasClassifier() ? "-" + getClassifier() : "") + "." + getExtension();
        }

        public String getPath() {
            // Deliberately use "/" not File.separator
            return getGroupId().replace(".", "/") + "/" + getArtifactId() + "/" + getVersion() + "/" + getFilename();
        }
    }
}
