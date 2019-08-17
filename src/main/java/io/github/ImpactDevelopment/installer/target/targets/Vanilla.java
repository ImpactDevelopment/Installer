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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.impact.ImpactJsonVersion;
import io.github.ImpactDevelopment.installer.libraries.ILibrary;
import io.github.ImpactDevelopment.installer.libraries.MavenResolver;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;
import io.github.ImpactDevelopment.installer.setting.settings.MinecraftDirectorySetting;
import io.github.ImpactDevelopment.installer.setting.settings.OptiFineSetting;
import io.github.ImpactDevelopment.installer.target.InstallationMode;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.WINDOWS;
import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.getOS;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Vanilla implements InstallationMode {

    private final String id;
    private final ImpactJsonVersion version;
    private final InstallationConfig config;

    public Vanilla(InstallationConfig config) {
        this.version = config.getSettingValue(ImpactVersionSetting.INSTANCE).fetchContents();
        this.config = config;
        this.id = version.mcVersion + "-" + version.name + "_" + version.version + prettifiedOptifineVersion().orElse("");
    }

    public JsonObject generateJsonVersion() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("type", "release");
        object.addProperty("inheritsFrom", version.mcVersion);
        object.addProperty("jar", version.mcVersion);
        object.addProperty("time", version.date);
        object.addProperty("releaseTime", version.date);
        object.add("downloads", new JsonObject());
        object.addProperty("minimumLauncherVersion", 0);
        object.addProperty("mainClass", "net.minecraft.launchwrapper.Launch");
        populateArguments(object);
        populateLibraries(object);
        return object;
    }

    private void populateArguments(JsonObject object) {
        if (version.mcVersion.compareTo("1.12.2") <= 0) {
            String args = "--username ${auth_player_name} --version ${version_name} --gameDir ${game_directory} --assetsDir ${assets_root} --assetIndex ${assets_index_name} --uuid ${auth_uuid} --accessToken ${auth_access_token} --userType ${user_type}";
            for (String tweaker : version.tweakers) {
                args += " --tweakClass " + tweaker;
            }
            object.addProperty("minecraftArguments", args);
        } else {
            JsonArray game = new JsonArray();
            for (String tweaker : version.tweakers) {
                game.add("--tweakClass");
                game.add(tweaker);
            }
            JsonObject arguments = new JsonObject();
            arguments.add("game", game);
            object.add("arguments", arguments);
        }
    }

    private void populateLibraries(JsonObject object) {
        JsonArray libraries = new JsonArray();
        for (ILibrary lib : version.resolveLibraries(config)) {
            populateLib(lib, libraries);
        }
        object.add("libraries", libraries);

        populateOptifine(libraries);
    }

    private void populateOptifine(JsonArray libraries) {
        optifineVersion().ifPresent(optifine -> {
            JsonObject opti = new JsonObject();
            opti.addProperty("name", "optifine:OptiFine:" + optifine);
            libraries.add(opti);
        });
    }

    private Optional<String> optifineVersion() {
        return Optional.ofNullable(config.getSettingValue(OptiFineSetting.INSTANCE)).filter(optifine -> !optifine.equals(OptiFineSetting.NONE)).filter(optifine -> !optifine.equals(OptiFineSetting.MISSING));
    }

    private Optional<String> prettifiedOptifineVersion() {
        return optifineVersion().map(str -> {
            if (!str.startsWith(version.mcVersion + "_")) {
                throw new IllegalStateException(str + " " + version.mcVersion);
            }
            return "-OptiFine" + str.substring(version.mcVersion.length());
        });
    }

    private void populateLib(ILibrary lib, JsonArray libraries) {
        if (version.mcVersion.equals("1.14.4") && optifineVersion().isPresent() && lib.getName().equals("net.minecraft:launchwrapper:1.12")) {
            JsonObject optiLaunchWrapper = new JsonObject();
            optiLaunchWrapper.addProperty("name", "optifine:launchwrapper-of:2.1");
            libraries.add(optiLaunchWrapper);
            return;
        }
        JsonObject library = new JsonObject();
        library.addProperty("name", lib.getName());
        libraries.add(library);
        downloads:
        {
            JsonObject downloads = new JsonObject();
            library.add("downloads", downloads);
            artifact:
            {
                JsonObject artifact = new JsonObject();
                downloads.add("artifact", artifact);
                artifact.addProperty("path", MavenResolver.partsToPath(lib.getName().split(":")));
                artifact.addProperty("sha1", lib.getSHA1());
                artifact.addProperty("size", lib.getSize());
                artifact.addProperty("url", lib.getURL());
            }
        }
    }

    @Override
    public String apply() throws IOException {
        install(false);
        return "Impact has been successfully installed";
    }

    public void sanityCheck(boolean allowMinecraftToBeOpen) {
        checkDirectory();
        checkVersionInstalled();
        if (!allowMinecraftToBeOpen && isMinecraftLauncherOpen()) {
            throw new RuntimeException("Please close Minecraft and its launcher before continuing");
        }
    }

    public void install(boolean allowMinecraftToBeOpen) throws IOException {
        System.out.println("Installing impact " + getId());
        System.out.println("Info:");
        version.printInfo();
        sanityCheck(allowMinecraftToBeOpen);
        installVersionJson();
        installProfiles();
    }

    private void checkDirectory() {
        VanillaProfiles.checkDirectory(config.getSettingValue(MinecraftDirectorySetting.INSTANCE));
    }

    private void checkVersionInstalled() {
        Path path = config.getSettingValue(MinecraftDirectorySetting.INSTANCE).resolve("versions").resolve(version.mcVersion).resolve(version.mcVersion + ".jar");
        if (!Files.exists(path)) {
            throw new RuntimeException("Please install and run Vanilla " + version.mcVersion + " once as normal before continuing.", new FileNotFoundException(path.toString()));
        }
    }

    private void installVersionJson() throws IOException {
        System.out.println("Creating vanilla version");
        Path directory = config.getSettingValue(MinecraftDirectorySetting.INSTANCE).resolve("versions").resolve(id);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create \"" + directory.toAbsolutePath().toString() + "\"");
            }
        }
        System.out.println("Writing to " + directory.resolve(id + ".json"));
        Files.write(directory.resolve(id + ".json"), Installer.gson.toJson(generateJsonVersion()).getBytes(StandardCharsets.UTF_8));
    }

    private void installProfiles() throws IOException {
        System.out.println("Loading existing vanilla profiles");
        VanillaProfiles profiles = new VanillaProfiles(config);
        System.out.println("Injecting impact version...");

        // go from 4.7.0-beta to 4.7-beta
        String strippedVersion = version.version.split("-")[0];
        if (strippedVersion.indexOf('.') != strippedVersion.lastIndexOf('.')) {
            strippedVersion = strippedVersion.substring(0, strippedVersion.lastIndexOf('.'));
        }
        if (version.version.contains("-")) {
            strippedVersion += version.version.substring(version.version.lastIndexOf("-"));
        }

        profiles.addOrMutate(version.name + " " + strippedVersion + " for " + version.mcVersion, getId());
        System.out.println("Saving vanilla profiles");
        profiles.saveToDisk();
    }

    private static boolean isMinecraftLauncherOpen() {
        try {
            if (getOS() == WINDOWS) {
                return IOUtils.toString(new ProcessBuilder("tasklist", "/fi", "WINDOWTITLE eq Minecraft Launcher").start().getInputStream(), UTF_8).contains("MinecraftLauncher.exe");
            }
            return IOUtils.toString(new ProcessBuilder("ps", "-ef").start().getInputStream(), UTF_8).contains("Minecraft Launcher");
        } catch (Throwable e) {
            return false;
        }
    }

    public String getId() {
        return id;
    }
}
