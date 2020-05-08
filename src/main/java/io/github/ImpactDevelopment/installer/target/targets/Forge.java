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

package io.github.ImpactDevelopment.installer.target.targets;

import io.github.ImpactDevelopment.installer.impact.ImpactJsonVersion;
import io.github.ImpactDevelopment.installer.libraries.ILibrary;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.DestinationSetting;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;
import io.github.ImpactDevelopment.installer.setting.settings.MinecraftDirectorySetting;
import io.github.ImpactDevelopment.installer.target.InstallationMode;
import io.github.ImpactDevelopment.installer.utils.Fetcher;
import io.github.ImpactDevelopment.installer.utils.Tracky;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Forge implements InstallationMode {

    private final ImpactJsonVersion version;
    private final InstallationConfig config;
    private final boolean liteloaderSupport;

    public Forge(InstallationConfig config, boolean liteloaderSupport) {
        this.version = config.getSettingValue(ImpactVersionSetting.INSTANCE).fetchContents();
        this.config = config;
        this.liteloaderSupport = liteloaderSupport;
    }

    @Override
    public String apply() throws IOException {
        Path out = config.getSettingValue(DestinationSetting.INSTANCE);

        if (Files.isDirectory(out)) {
            out = out.resolve(version.name + "-" + version.version + "-" + version.mcVersion + ".jar");
        }

        if (!Files.exists(out.getParent())) {
            Files.createDirectories(out.getParent());
        }

        if (liteloaderSupport) {
            JOptionPane.showMessageDialog(null, "This Forge jar will ONLY work with Liteloader + Forge, not with either on their own.\nIf you don't have liteloader, use the Forge option instead!\nIf you change your mind and just want Forge (no liteloader), you will need to reinstall Impact with the correct option!", "IMPORTANT", JOptionPane.INFORMATION_MESSAGE);
        }

        Path defaultLauncher = config.getSettingValue(MinecraftDirectorySetting.INSTANCE);
        if (Files.isDirectory(defaultLauncher)) {
            Tracky.persist(defaultLauncher);
        }

        HashSet<String> fileNames = new HashSet<>();
        try (JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(out.toFile()))) {
            for (ILibrary library : version.resolveLibraries(config)) {
                byte[] b = Fetcher.fetchBytes(library.getURL());
                if (b.length != library.getSize() || !sha1hex(b).equals(library.getSHA1())) {
                    throw new RuntimeException(b.length + " " + library.getSize() + " " + sha1hex(b) + " " + library.getSHA1());
                }
                if (liteloaderSupport && library.getURL().contains("mixin")) {
                    continue;
                }
                ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(new ByteArrayInputStream(b));
                ArchiveEntry entry;
                while ((entry = input.getNextEntry()) != null) {
                    if (!input.canReadEntryData(entry)) {
                        continue;
                    }
                    String name = entry.getName();
                    if (name.equals("META-INF/MANIFEST.MF")) {
                        // Process the MANIFEST from the main jar
                        if (library.getName().startsWith("com.github.ImpactDevelopment:Impact:")) {
                            jarOut.putNextEntry(new JarEntry(name));
                            mutateManifest(input, jarOut);
                        }
                        continue;
                    }
                    if (liteloaderSupport && name.equals("mixins.capi.json")) {
                        jarOut.putNextEntry(new JarEntry(name));
                        mutateCapi(input, jarOut);
                        continue;
                    }
                    if (name.startsWith("META-INF/MUMFREY") || name.equals("module-info.class")) {
                        continue;
                    }
                    if (fileNames.contains(name)) {
                        System.out.println("WARNING: discarding file since I've already included one with the same name: " + name);
                        continue;
                    }
                    fileNames.add(name);
                    jarOut.putNextEntry(new JarEntry(name));
                    IOUtils.copy(input, jarOut);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Look for other Impact forge jars
        try {
            final Path finalOut = out; //Variable used in lambda expression should be final or effectively final
            List<Path> conflicts = Files.list(out.getParent())
                    .filter(f -> !f.equals(finalOut))
                    .filter(f -> f.getFileName().toString().startsWith("Impact-"))
                    .filter(f -> f.getFileName().toString().endsWith(".jar"))
                    .collect(Collectors.toList());

            // If we find any Impact jars, warn the user and ask to delete them
            if (!conflicts.isEmpty()) {
                List<String> names = conflicts.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
                JOptionPane.showMessageDialog(null, "Warning: Having multiple Impact mods installed will cause errors:\n" + String.join("\n", names), "\uD83D\uDE0E", JOptionPane.WARNING_MESSAGE);

                conflicts.forEach(conflict -> {
                        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Would you like to remove " + conflict.getFileName() + "?", "\uD83D\uDE0E", JOptionPane.YES_NO_OPTION)) {
                            try {
                                Files.delete(conflict);
                                JOptionPane.showMessageDialog(null, "Removed " + conflict, "\uD83D\uDE0E", JOptionPane.INFORMATION_MESSAGE);
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(null, "Failed to remove " + conflict.getFileName() + ":\n" + e.getMessage(), "\uD83D\uDE0E", JOptionPane.ERROR_MESSAGE);
                                e.printStackTrace();
                            }
                        }
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error while checking for older Impact Forge installations: " + e.getLocalizedMessage(), "\uD83D\uDE0E", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return "Impact Forge has been successfully installed at " + out;
    }

    // Change the tweak class to point to MixinTweaker
    private void mutateManifest(InputStream input, OutputStream output) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));

        String line;
        while (null != (line = reader.readLine())) {
            if (line.startsWith("TweakClass:")) {
                line = "TweakClass: org.spongepowered.asm.launch.MixinTweaker";
            }

            if (line.startsWith("MixinConfigs: ") && liteloaderSupport) {
                line = line.replace(", ", ",");
            }

            if (line.isEmpty()) {
                continue;
            }

            line += "\n";
            output.write(line.getBytes(UTF_8));
        }

        output.write("\n".getBytes(UTF_8));
    }

    // Change the mixin minimum version to 0.7.5
    private void mutateCapi(InputStream input, OutputStream output) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));

        String line;
        while (null != (line = reader.readLine())) {
            if (line.contains("0.7.8")) {
                line = line.replace("0.7.8", "0.7.5");
            }

            if (line.isEmpty()) {
                continue;
            }

            line += "\n";
            output.write(line.getBytes(UTF_8));
        }

        output.write("\n".getBytes(UTF_8));
    }

    public static String sha1hex(byte[] data) {
        // dont use the javax.xml meme because we want to support java 8 through 11
        try {
            byte[] digest = MessageDigest.getInstance("SHA-1").digest(data);
            String result = "";
            for (byte b : digest) {
                result += String.format("%02x", b);
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
