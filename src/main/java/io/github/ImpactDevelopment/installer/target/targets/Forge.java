/*
 * This file is part of Impact Installer.
 *
 * Impact Installer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Impact Installer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impact Installer.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.ImpactDevelopment.installer.target.targets;

import io.github.ImpactDevelopment.installer.impact.ImpactJsonVersion;
import io.github.ImpactDevelopment.installer.libraries.ILibrary;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;
import io.github.ImpactDevelopment.installer.setting.settings.MinecraftDirectorySetting;
import io.github.ImpactDevelopment.installer.target.InstallationMode;
import io.github.ImpactDevelopment.installer.utils.Fetcher;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Forge implements InstallationMode {

    private final ImpactJsonVersion version;
    private final InstallationConfig config;

    public Forge(InstallationConfig config) {
        this.version = config.getSettingValue(ImpactVersionSetting.INSTANCE).fetchContents();
        this.config = config;
    }

    @Override
    public String apply() {
        File outputFolder = config.getSettingValue(MinecraftDirectorySetting.INSTANCE).resolve("mods").toFile();
        outputFolder.mkdir();
        File outputFile = new File(outputFolder, version.name + "-" + version.version + "-" + version.mcVersion + ".jar");
        try (
                FileOutputStream fileOut = new FileOutputStream(outputFile);
                JarOutputStream jarOut = new JarOutputStream(fileOut);
        ) {
            for (ILibrary library : version.resolveLibraries(config)) {
                byte[] b = Fetcher.fetchBytes(library.getURL());
                if (b.length != library.getSize() || !sha1hex(b).equals(library.getSHA1())) {
                    throw new RuntimeException(b.length + " " + library.getSize() + " " + sha1hex(b) + " " + library.getSHA1());
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
                    if (!name.equals("icons/") && name.endsWith("/") || name.startsWith("META-INF/MUMFREY")) {
                        continue;
                    }
                    jarOut.putNextEntry(new JarEntry(name));
                    IOUtils.copy(input, jarOut);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (File f : outputFolder.listFiles()) {
            if (f.getName().startsWith("Impact-") && !f.getName().equals(outputFile.getName())) {
                JOptionPane.showMessageDialog(null, "Replacing older version of Impact " + f, "\uD83D\uDE0E", JOptionPane.INFORMATION_MESSAGE);
                f.delete();
            }
        }
        return "Impact Forge has been successfully installed at " + outputFile;
    }

    // Change the tweak class to point to MixinTweaker
    private void mutateManifest(InputStream input, OutputStream output) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));

        String line;
        while (null != (line = reader.readLine())) {
            if (line.startsWith("TweakClass:")) {
                line = "TweakClass: org.spongepowered.asm.launch.MixinTweaker";
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
