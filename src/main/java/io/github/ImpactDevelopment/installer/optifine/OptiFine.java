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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class OptiFine {

    private static final String GROUP = "optifine";
    private static final Pattern TWEAKER_REGEX = Pattern.compile("^TweakClass:\\s+(.+)$");

    private final Path jarPath;
    private final String[] version;
    private final String tweaker;
    private final String transformer;
    private final String launchwrapperVersion;

    // patcherMethod takes a vanilla "base" jar along with an OptiFine "diff" jar and writes the result to a "mod" jar
    // public static void process(File baseFile, File diffFile, File modFile)
    private final Method patcherMethod;

    public OptiFine(Path jarPath) throws RuntimeException {
        // Set locals first while iterating the zip file, then set the final fields after
        String version;
        String launchwrapperVersion = null;
        String tweaker = "";
        String transformer = "";

        // load required methods from the OptiFine installer jar
        Method getOptiFineVersionMethod;
        Method patcherMethod;
        try {
            ClassLoader classloader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()});
            patcherMethod = classloader.loadClass("optifine.Patcher").getMethod("process", File.class, File.class, File.class);
            getOptiFineVersionMethod = classloader.loadClass("optifine.Installer").getMethod("getOptiFineVersion", ZipFile.class);
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to load OptiFine classes", e);
        }

        // Iterate over the zip entries in the jar and extract any info we care about
        try {
            try (ZipFile jar = new ZipFile(jarPath.toFile())) {
                // Extract optifine version
                version = (String) getOptiFineVersionMethod.invoke(null, jar);

                final Enumeration<? extends ZipEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    final ZipEntry entry = entries.nextElement();
                    if (entry.getName().equals("launchwrapper-of.txt")) {
                        try (BufferedReader input = new BufferedReader(new InputStreamReader((jar.getInputStream(entry))))) {
                            if (input.ready()) {
                                launchwrapperVersion = input.readLine();
                            }
                        }
                    }
                    if (entry.getName().equals("META-INF/services/cpw.mods.modlauncher.api.ITransformationService")) {
                        try (BufferedReader input = new BufferedReader(new InputStreamReader(jar.getInputStream(entry)))) {
                            if (input.ready()) {
                                transformer = input.readLine();
                            }
                        }
                    }
                    if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                        try (BufferedReader input = new BufferedReader(new InputStreamReader(jar.getInputStream(entry)))) {
                            while (input.ready()) {
                                Matcher line = TWEAKER_REGEX.matcher(input.readLine());
                                if (line.matches()) {
                                    tweaker = line.group(1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error processing OptiFine jar", e);
        }

        // Set the final fields
        this.jarPath = jarPath;
        this.version = version.split("_");
        this.tweaker = tweaker;
        this.transformer = transformer;
        this.launchwrapperVersion = launchwrapperVersion;
        this.patcherMethod = patcherMethod;
    }

    // Get the minecraft version this targets
    public String getMinecraftVersion() {
        return version[1];
    }

    // Get just the optifine part of the version
    public String getOptiFineVersion() {
        if (version.length <= 2) {
            throw new IllegalStateException("OptiFine version has too few elements");
        }
        return String.join("_", Arrays.asList(version).subList(2, version.length));
    }

    // Get the full version as used by maven
    public String getVersion() {
        return String.format("%s_%s", getMinecraftVersion(), getOptiFineVersion());
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
        return String.format("%s:%s:%s", GROUP, version[0], getVersion());
    }

    // Get the artifact id for OptiFine's custom launchwrapper, or null if upstreams's is ok.
    @Nullable
    public String getLaunchwrapperID() {
        return launchwrapperVersion == null ? null : String.format("%s:launchwrapper-of:%s", GROUP, launchwrapperVersion);
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
    protected void installOptiFine(Path destination, Path vanilla) throws IOException, InvocationTargetException, IllegalAccessException {
        Files.deleteIfExists(destination);
        Files.createDirectories(destination.getParent());

        // Static method so null instance
        patcherMethod.invoke(null, vanilla.toFile(), jarPath.toFile(), destination.toFile());
    }

    // Extract the launchwrapper jar to the target libraries directory
    protected void installLaunchwrapper(Path destination) throws IOException {
        String entry = String.format("launchwrapper-of-%s.jar", launchwrapperVersion);
        try (ZipFile file = new ZipFile(jarPath.toFile())) {
            try (InputStream input = file.getInputStream(file.getEntry(entry))) {
                Files.createDirectories(destination.getParent());
                Files.copy(input, destination, REPLACE_EXISTING);
            }
        }
    }

    // Get a maven path based on an artifact id.
    // Ignores any classifier since (last I checked) so does the Minecraft Launcher
    private static Path pathFromID(String artifact) throws IllegalArgumentException {
        String[] parts = artifact.split(":");
        if (parts.length < 3) {
            throw new IllegalArgumentException("OptiFine.pathFromID() expected an artifact id with at least three parts, got "+artifact);
        }
        String group = parts[0].replace(".", File.separator);
        String id = parts[1];
        String version = parts[2];
        return Paths.get(group).resolve(id).resolve(version).resolve(String.format("%s-%s.jar", id, version));
    }
}
