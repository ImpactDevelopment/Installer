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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.impact.ImpactJsonVersion;
import io.github.ImpactDevelopment.installer.libraries.ILibrary;
import io.github.ImpactDevelopment.installer.libraries.MavenResolver;
import io.github.ImpactDevelopment.installer.optifine.OptiFine;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;
import io.github.ImpactDevelopment.installer.setting.settings.MinecraftDirectorySetting;
import io.github.ImpactDevelopment.installer.setting.settings.OptiFineFileSetting;
import io.github.ImpactDevelopment.installer.setting.settings.OptiFineSetting;
import io.github.ImpactDevelopment.installer.target.InstallationMode;
import io.github.ImpactDevelopment.installer.utils.Tracky;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.WINDOWS;
import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.getOS;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Vanilla implements InstallationMode {

    private final String id;
    private final ImpactJsonVersion version;
    private final InstallationConfig config;
    private final OptiFine optifine;
    private final Path vanillaJar;

    public Vanilla(InstallationConfig config) throws RuntimeException {
        this.version = config.getSettingValue(ImpactVersionSetting.INSTANCE).fetchContents();
        this.optifine = config.getSettingValue(OptiFineSetting.INSTANCE) ? new OptiFine(config.getSettingValue(OptiFineFileSetting.INSTANCE)) : null;
        this.config = config;
        this.id = String.format("%s-%s_%s%s", version.mcVersion, version.name, version.version, optifine == null ? "" : "-OptiFine_"+optifine.getOptiFineVersion());

        // TODO consider downloading the jar from mojang ourselves?
        //      or at least consider how to do this for multimc?
        this.vanillaJar = config.getSettingValue(MinecraftDirectorySetting.INSTANCE).resolve("versions").resolve(version.mcVersion).resolve(version.mcVersion+".jar");
        if (optifine != null && !Files.isRegularFile(vanillaJar)) {
            throw new IllegalStateException("If installing OptiFine, you must play Minecraft "+version.mcVersion+" at least once before continuing!");
        }

        if (optifine != null && !optifine.getMinecraftVersion().equals(version.mcVersion)) {
            throw new IllegalStateException(String.format("OptiFine %s is not compatible with Minecraft %s", optifine.getVersion(), version.mcVersion));
        }
    }

    public JsonObject generateVanillaJsonVersion() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("type", "release");
        object.addProperty("inheritsFrom", version.mcVersion);
        object.addProperty("jar", version.mcVersion);
        object.addProperty("time", version.date);
        object.addProperty("releaseTime", version.date);
        object.add("downloads", new JsonObject());
        object.addProperty("minimumLauncherVersion", 0);
        object.addProperty("mainClass", version.mainClass);
        populateArguments(object);
        populateLibraries(object, false);
        return object;
    }

    public JsonObject generateMultiMCJsonVersion() {
        JsonObject object = new JsonObject();
        JsonArray arrayTweakers = new JsonArray();
        version.tweakers.forEach(arrayTweakers::add);
        object.addProperty("fileID", "net.impactclient.Impact");
        object.addProperty("mainClass", version.mainClass);
        object.addProperty("mcVersion", version.mcVersion);
        object.addProperty("name", "Impact " + version.version);
        object.addProperty("order", 10);
        object.addProperty("version", id);
        object.add("+tweakers", arrayTweakers);
        populateLibraries(object, true);
        return object;
    }

    private void populateArguments(JsonObject object) {
        if (version.mcVersion.compareTo("1.12.2") <= 0) {
            String defaultArgs = "--username ${auth_player_name} --version ${version_name} --gameDir ${game_directory} --assetsDir ${assets_root} --assetIndex ${assets_index_name} --uuid ${auth_uuid} --accessToken ${auth_access_token} --userType ${user_type}";
            String tweakerArgs = version.tweakers.stream().reduce("", (accumulator, tweaker) -> accumulator + " --tweakClass " + tweaker);
            object.addProperty("minecraftArguments", defaultArgs + tweakerArgs);
        } else {
            JsonArray game = new JsonArray();
            version.tweakers.forEach(tweaker -> {
                game.add("--tweakClass");
                game.add(tweaker);
            });
            JsonObject arguments = new JsonObject();
            arguments.add("game", game);
            object.add("arguments", arguments);
        }
    }

    private void populateLibraries(JsonObject object, boolean multimc) {
        JsonArray libraries = new JsonArray();
        for (ILibrary lib : version.resolveLibraries(config)) {
            populateLib(lib, libraries);
        }
        if (optifine != null) {
            JsonObject lib = new JsonObject();
            lib.addProperty("name", optifine.getOptiFineID());
            libraries.add(lib);
        }

        if (multimc) {
            object.add("+libraries", libraries);
        } else {
            object.add("libraries", libraries);
        }
    }

    private void populateLib(ILibrary lib, JsonArray libraries) {
        if (optifine != null && optifine.getLaunchwrapperID() != null && lib.getName().equals("net.minecraft:launchwrapper:1.12")) {
            JsonObject optiLaunchWrapper = new JsonObject();
            optiLaunchWrapper.addProperty("name", optifine.getLaunchwrapperID());
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

    @Override
    public String installOptifine() throws IOException {
        if (optifine == null) {
            throw new IllegalStateException("No optifine specified, cannot install OptiFine");
        }

        Path libs = config.getSettingValue(MinecraftDirectorySetting.INSTANCE).resolve("libraries");
        System.out.println("Installing OptiFine into " + libs.toString());
        optifine.install(libs, vanillaJar);

        return "Installed OptiFine successfully";
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
        Tracky.persist(config.getSettingValue(MinecraftDirectorySetting.INSTANCE));
        sanityCheck(allowMinecraftToBeOpen);
        installVersionJson();
        installProfiles();
        installOptifine();
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
        Files.write(directory.resolve(id + ".json"), Installer.gson.toJson(generateVanillaJsonVersion()).getBytes(StandardCharsets.UTF_8));
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
