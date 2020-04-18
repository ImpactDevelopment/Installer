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

package io.github.ImpactDevelopment.installer;

import com.beust.jcommander.Parameter;
import io.github.ImpactDevelopment.installer.impact.ImpactVersion;
import io.github.ImpactDevelopment.installer.impact.ImpactVersionDisk;
import io.github.ImpactDevelopment.installer.impact.ImpactVersionReleased;
import io.github.ImpactDevelopment.installer.impact.ImpactVersions;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.*;
import io.github.ImpactDevelopment.installer.target.InstallationModeOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Args {

    @Parameter(names = {"--no-gpg", "--disable-gpg"}, description = "Disable checking the release signature for testing purposes")
    public boolean noGPG = false;

    @Parameter(names = {"-i", "--impact-version"}, description = "The fully qualified Impact version (e.g. 4.6-1.12.2)")
    public String impactVersion;

    @Parameter(names = {"-f", "--json-file", "--file"}, description = "A json file to install from. Overrides impactVersion.")
    public String file;

    @Parameter(names = {"-m", "--mode"}, description = "The mode of installation to execute")
    public String mode;

    @Parameter(names = {"--no-gui", "--disable-gui"}, description = "Disable the GUI and execute the specifcied mode")
    public boolean noGUI = false;

    @Parameter(names = {"--pre", "--include-pre", "--prerelease", "--prereleases", "--include-prereleases"}, description = "Include releases marked as prerelease on GitHub")
    public boolean prereleases = false;

    @Parameter(names = {"--all"}, description = "Run on all Impact releases")
    public boolean all = false;

    @Parameter(names = {"--mc-dir", "--minecraft-dir", "--minecraft-directory", "--mc-path"}, description = "Path to the Minecraft directory")
    public String mcPath;

    @Parameter(names = {"--optifine", "--of"}, description = "Path to an OptiFine installer jar")
    public String optifine;

    @Parameter(names = {"--no-ga", "--no-analytics", "--dnt", "--no-tracky"}, description = "Disable Google Analytics")
    public boolean noAnalytics = false;

    @Parameter(names = {"-h", "-?", "--help"}, description = "Display this help and exit", help = true, order = 0)
    public boolean showUsage = false;

    @Parameter(names = {"--version"}, description = "Output version information and exit\n", help = true, order = 1)
    public boolean showVersion = false;

    public Args() {
        // Lets look for a properties file and use it to override defaults
        try {
            getProperties("default_args.properties").forEach((o1, o2) -> {
                String key, value;
                try {
                    key = (String) o1;
                    value = (String) o2;
                } catch (Throwable ignored) {
                    System.err.println("WTF! unable to cast key or value to string: " + o1 + ", " + o2);
                    return;
                }

                try {
                    Field field = Args.class.getField(key);
                    if (!field.isAnnotationPresent(Parameter.class)) {
                        System.err.println("default_args.properties tried to override non-parameter field " + field.getName());
                        return;
                    }
                    // Parse value to the correct type and set the field's value
                    field.set(this, toType(field.getType(), value));
                } catch (Throwable t) {
                    System.err.println("Error setting default value: " + key + " = " + value);
                    t.printStackTrace();
                }
            });
        } catch (Throwable t) {
            if (!(t instanceof FileNotFoundException)) {
                t.printStackTrace();
            }
        }
    }

    public void apply(InstallationConfig config) {
        if (mcPath != null) {
            Path path = Paths.get(mcPath);
            if (!Files.isDirectory(path)) {
                throw new IllegalStateException(path + " is not a directory");
            }
            config.setSettingValue(MinecraftDirectorySetting.INSTANCE, path);
        }
        if (mode != null) {
            config.setSettingValue(InstallationModeSetting.INSTANCE, InstallationModeOptions.valueOf(mode.toUpperCase()));
        }
        if (all) {
            for (ImpactVersionReleased version : ImpactVersions.getAllVersions()) {
                setImpactVersion(config, true, version);
                try {
                    System.out.println(config.execute());
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (impactVersion != null) {
            setImpactVersion(config, true,
                    ImpactVersions.getAllVersions().stream()
                            .filter(version -> version.getCombinedVersion().equals(impactVersion))
                            .findAny()
                            .orElseThrow(() -> new IllegalArgumentException("No impact version matches " + impactVersion))
            );
        }
        if (file != null) {
            setImpactVersion(config, false, new ImpactVersionDisk(Paths.get(file)));
        }
        if (optifine != null) {
            config.setSettingValue(OptiFineToggleSetting.INSTANCE, true);
            if (!config.setSettingValue(OptiFineFileSetting.INSTANCE, Paths.get(optifine))) {
                throw new IllegalArgumentException(optifine + " is not found");
            }
        }
    }

    private void setImpactVersion(InstallationConfig config, boolean checkMcVersionValidityAgainstReleases, ImpactVersion version) {
        config.setSettingValue(MinecraftVersionSetting.INSTANCE, version.mcVersion);
        if (checkMcVersionValidityAgainstReleases && !ImpactVersionSetting.INSTANCE.validSetting(config, version)) {
            throw new IllegalStateException(impactVersion + " is not a valid selection in the current configuration. Perhaps try a different mode or version");
        }
        config.setSettingValue(ImpactVersionSetting.INSTANCE, version);
    }

    // Get a properties file from the classpath
    private static Properties getProperties(String filename) throws IOException {
        Properties properties = new Properties();

        InputStream inputStream = Args.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            throw new FileNotFoundException(filename + "' not found in the classpath");
        }

        try {
            properties.load(inputStream);
        } finally {
            inputStream.close();
        }

        return properties;
    }

    // Convert a string value to a primitive type
    // Won't work for non-primitive types, but luckily all our args are primitives
    private static Object toType(Class<?> type, String value) {
        if (Boolean.class == type || Boolean.TYPE == type) return Boolean.parseBoolean(value);
        if (Byte.class == type || Byte.TYPE == type) return Byte.parseByte(value);
        if (Short.class == type || Short.TYPE == type) return Short.parseShort(value);
        if (Integer.class == type || Integer.TYPE == type) return Integer.parseInt(value);
        if (Long.class == type || Long.TYPE == type) return Long.parseLong(value);
        if (Float.class == type || Float.TYPE == type) return Float.parseFloat(value);
        if (Double.class == type || Double.TYPE == type) return Double.parseDouble(value);
        return value;
    }
}
