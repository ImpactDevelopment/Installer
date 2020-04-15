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

package io.github.ImpactDevelopment.installer.optifine;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class OptiFine {

    private static final Pattern LW_REGEX = Pattern.compile("^(launchwrapper-of)-([0-9.]+)[.]jar$");
    private static final Pattern TWEAKER_REGEX = Pattern.compile("^TweakClass:\\s+(.+)$");
    private static final Pattern VERSION_REGEX = Pattern.compile("^OptiFine\\s+([^_]+)_(.+)$");

    private final Path jarPath;
    private final String version;
    private final String mcVersion;
    private final String tweaker;
    private final String transformer;
    private final String launchwrapperEntry;

    // public static void process(File baseFile, File diffFile, File modFile)
    private final Method patcher;

    public OptiFine(Path jarPath) throws RuntimeException {
        // Set locals first while iterating the zip file, then set the final fields after
        String version = "";
        String mcVersion = "";
        String tweaker = "";
        String transformer = "";
        String launchwrapperEntry = "";

        // Iterate over the zip entries in the jar and extract any info we care about
        try {
            try (ZipFile file = new ZipFile(jarPath.toFile())) {
                final Enumeration<? extends ZipEntry> entries = file.entries();
                while (entries.hasMoreElements()) {
                    final ZipEntry entry = entries.nextElement();
                    if (LW_REGEX.matcher(entry.getName()).matches()) {
                        launchwrapperEntry = entry.getName();
                    }
                    // This is probably the best way to get the version, since filenames can be easily changed.
                    // We set version and mcVersion to values from the first matching line in changelog.txt
                    // (this should be the first line, but loop just in case).
                    if (entry.getName().equals("changelog.txt")) {
                        try (BufferedReader input = new BufferedReader(new InputStreamReader((file.getInputStream(entry))))) {
                            while (input.ready()) {
                                Matcher line = VERSION_REGEX.matcher(input.readLine());
                                if (line.matches()) {
                                    mcVersion = line.group(1);
                                    version = line.group(2);
                                    break;
                                }
                            }
                        }
                    }
                    if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                        try (BufferedReader input = new BufferedReader(new InputStreamReader(file.getInputStream(entry)))) {
                            while (input.ready()) {
                                Matcher line = TWEAKER_REGEX.matcher(input.readLine());
                                if (line.matches()) {
                                    tweaker = line.group(1);
                                    break;
                                }
                            }
                        }
                    }
                    if (entry.getName().equals("META-INF/services/cpw.mods.modlauncher.api.ITransformationService")) {
                        try (BufferedReader input = new BufferedReader(new InputStreamReader(file.getInputStream(entry)))) {
                            if (input.ready()) {
                                transformer = input.readLine();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing OptiFine jar", e);
        }

        // Set the final fields
        this.jarPath = jarPath;
        this.version = version;
        this.mcVersion = mcVersion;
        this.tweaker = tweaker;
        this.transformer = transformer;
        this.launchwrapperEntry = launchwrapperEntry;

        // load required methods from OptiFine jar
        try {
            patcher = loadOptiFinePatcher(jarPath);
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to load optifine patcher", e);
        }
    }

    // Get the minecraft version this targets
    public String getMinecraftVersion() {
        return mcVersion;
    }

    // Get just the optifine part of the version
    public String getOptiFineVersion() {
        return version;
    }

    // Get the full version as used by maven
    public String getVersion() {
        return mcVersion+"_"+version;
    }

    // Get the tweaker class used by launchwrapper
    public String getTweaker() {
        return tweaker;
    }

    // Get the transformer class used by modloader
    public String getTransformer() {
        return transformer;
    }

    // Get the artifact id for OptiFine
    public String getOptiFineID() {
        return "optifine:OptiFine:"+getVersion();
    }

    // Get the artifact id for OptiFine's custom launchwrapper, or null if upstreams's is ok.
    @Nullable
    public String getLaunchwrapperID() {
        if (!launchwrapperEntry.isEmpty()) {
            Matcher match = LW_REGEX.matcher(launchwrapperEntry);
            if (match.matches()) {
                return String.format("optifine:%s:%s", match.group(1), match.group(2));
            }
        }
        return null;
    }

    // Install optifine jar and launchwrapper (if required) to the target libraries directory
    public void install(Path libs, Path vanilla) throws IOException {
        try {
            installOptiFine(libs.resolve(pathFromID(getOptiFineID())), vanilla);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Error calling OptiFine patcher method", e);
        }
        if (getLaunchwrapperID() != null) {
            installLaunchwrapper(libs.resolve(pathFromID(getLaunchwrapperID())));
        }
    }

    // Install the patched optifine jar in the target libraries directory
    private void installOptiFine(Path destination, Path vanilla) throws IOException, InvocationTargetException, IllegalAccessException {
        Files.deleteIfExists(destination);
        Files.createDirectories(destination.getParent());

        // Static method so null instance
        patcher.invoke(null, vanilla.toFile(), jarPath.toFile(), destination.toFile());
    }

    // Extract the launchwrapper jar to the target libraries directory
    private void installLaunchwrapper(Path destination) throws IOException {
        try (ZipFile file = new ZipFile(jarPath.toFile())) {
            try (InputStream input = file.getInputStream(file.getEntry(launchwrapperEntry))) {
                Files.createDirectories(destination.getParent());
                Files.copy(input, destination, REPLACE_EXISTING);
            }
        }
    }

    // Get a maven path based on an artifact id.
    // Ignores any classifier since last I checked, so does the Minecraft Launcher
    private Path pathFromID(String artifact) throws IllegalArgumentException {
        String[] parts = artifact.split(":");
        if (parts.length < 3) {
            throw new IllegalArgumentException("OptiFine.pathFromID expected an artifact id with at least three parts, got "+artifact);
        }
        String group = parts[0].replace(".", File.separator);
        String id = parts[1];
        String version = parts[2];
        return Paths.get(group).resolve(id).resolve(version).resolve(String.format("%s-%s.jar", id, version));
    }

    private static Method loadOptiFinePatcher(Path jarPath) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException {
        ClassLoader classloader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()});
        Class<?> patcher = classloader.loadClass("optifine.Patcher");
        return patcher.getMethod("process", File.class, File.class, File.class);
    }
}
