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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
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
    private static final int BUFFER_SIZE = 4096;

    private final Path jarPath;

    private String version = "";
    private String mcVersion = "";
    private String tweaker = "";
    private String transformer = "";
    private String launchwrapperEntry = "";

    public OptiFine(Path jarPath) {
        this.jarPath = jarPath;
        try {
            try (ZipFile file = new ZipFile(jarPath.toFile())) {
                final Enumeration<? extends ZipEntry> entries = file.entries();
                while (entries.hasMoreElements()) {
                    final ZipEntry entry = entries.nextElement();
                    if (LW_REGEX.matcher(entry.getName()).matches()) {
                        launchwrapperEntry = entry.getName();
                    }
                    // This is probably the best way to get the version, since filenames can be easily changed
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
            e.printStackTrace();
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

    // Extract the launchwrapper jar to the target libraries directory
    public void installCustomLaunchwrapper(Path libs) throws IOException {
        if (getLaunchwrapperID() != null) {
            Path outputPath = libs.resolve(pathFromID(getLaunchwrapperID()));
            Files.createDirectories(outputPath.getParent());
            Files.deleteIfExists(outputPath);
            try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputPath.toFile()))) {
                try (ZipFile file = new ZipFile(jarPath.toFile())) {
                    InputStream input = file.getInputStream(file.getEntry(launchwrapperEntry));
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int read = 0;
                    while ((read = input.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                }
            }
        }
    }

    // Copy the optifine jar to the target libraries directory
    public void installOptiFine(Path libs) throws IOException {
        Path outputPath = libs.resolve(pathFromID(getOptiFineID()));
        Files.createDirectories(outputPath.getParent());
        Files.copy(jarPath, outputPath, REPLACE_EXISTING);
    }

    // Get a maven path based on an artifact id.
    // Ignores any classifier since last I checked, so does the Minecraft Launcher
    private Path pathFromID(String artifact) {
        String[] parts = artifact.split(":");
        if (parts.length < 3) {
            throw new InvalidParameterException("OptiFine.pathFromID expected an artifact id with at least three parts, got "+artifact);
        }
        String group = parts[0].replace(".", File.separator);
        String id = parts[1];
        String version = parts[2];
        return Paths.get(group).resolve(id).resolve(version).resolve(String.format("%s-%s.jar", id, version));
    }
}
