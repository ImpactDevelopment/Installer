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
import io.github.ImpactDevelopment.installer.optifine.OptiFineExisting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.*;
import io.github.ImpactDevelopment.installer.target.InstallationMode;
import io.github.ImpactDevelopment.installer.utils.Tracky;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.ImpactDevelopment.installer.setting.settings.OptiFineSetting.CUSTOM;
import static io.github.ImpactDevelopment.installer.setting.settings.OptiFineSetting.MISSING;
import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.WINDOWS;
import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.getOS;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Vanilla implements InstallationMode {

    protected final String id;
    protected final ImpactJsonVersion version;
    protected final InstallationConfig config;
    protected final OptiFine optifine;

    protected Path vanillaJar;

    public Vanilla(InstallationConfig config) throws RuntimeException {
        Path mcDir = config.getSettingValue(MinecraftDirectorySetting.INSTANCE);
        this.config = config;
        this.version = config.getSettingValue(ImpactVersionSetting.INSTANCE).fetchContents();
        this.vanillaJar = mcDir.resolve("versions").resolve(version.mcVersion).resolve(version.mcVersion + ".jar");
        this.optifine = getOptiFine(config);
        this.id = String.format("%s-%s_%s%s", version.mcVersion, version.name, version.version, optifine == null ? "" : "-OptiFine_" + optifine.getOptiFineVersion());
        if (optifine != null && !optifine.getMinecraftVersion().equals(version.mcVersion)) {
            throw new IllegalStateException(String.format("OptiFine %s is not compatible with Minecraft %s", optifine.getVersion(), version.mcVersion));
        }
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
        object.addProperty("mainClass", version.mainClass);
        populateArguments(object);
        object.add("libraries", generateLibraries());
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

    protected JsonArray generateLibraries() {
        JsonArray libraries = new JsonArray();

        for (ILibrary lib : version.resolveLibraries(config)) {
            populateLib(lib, libraries);
        }

        if (optifine != null) {
            JsonObject lib = new JsonObject();
            lib.addProperty("name", optifine.getOptiFineID());
            libraries.add(lib);
        }

        return libraries;
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
                artifact.addProperty("path", MavenResolver.getPath(lib.getName()));
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
        optifine.install(libs, vanillaJar, true);

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
        if (optifine != null) {
            installOptifine();
        }
    }

    private void checkDirectory() {
        VanillaProfiles.checkDirectory(config.getSettingValue(MinecraftDirectorySetting.INSTANCE));
    }

    private void checkVersionInstalled() {
        if (!Files.exists(vanillaJar)) {
            throw new RuntimeException("Please install and run Vanilla " + version.mcVersion + " once as normal before continuing.", new FileNotFoundException(vanillaJar.toString()));
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
        Files.write(directory.resolve(id + ".json"), Installer.gson.toJson(generateJsonVersion()).getBytes(UTF_8));
    }

    private void installProfiles() throws IOException {
        System.out.println("Loading existing vanilla profiles");
        VanillaProfiles profiles = new VanillaProfiles(config);
        System.out.println("Injecting impact version...");


        profiles.addOrMutate(version.name + " " + getStrippedVersion() + " for " + version.mcVersion, getId());
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

    public String getStrippedVersion() {
        // go from 4.7.0-beta to 4.7-beta
        String strippedVersion = version.version.split("-")[0];
        if (strippedVersion.indexOf('.') != strippedVersion.lastIndexOf('.')) {
            strippedVersion = strippedVersion.substring(0, strippedVersion.lastIndexOf('.'));
        }
        return strippedVersion;
    }

    @Nullable
    private static OptiFine getOptiFine(InstallationConfig config) {
        if (config.getSettingValue(OptiFineToggleSetting.INSTANCE)) {
            switch (config.getSettingValue(OptiFineSetting.INSTANCE)) {
                case MISSING:
                case CUSTOM:
                    Path installer = config.getSettingValue(OptiFineFileSetting.INSTANCE);
                    if (!Files.isRegularFile(installer)) {
                        throw new IllegalArgumentException("Selected installer is not a regular file " + installer.getFileName());
                    }
                    if (!Files.isReadable(installer)) {
                        throw new IllegalArgumentException("Cannot read file " + installer.getFileName());
                    }
                    return new OptiFine(installer);
                default:
                    Path launcher = config.getSettingValue(MinecraftDirectorySetting.INSTANCE);
                    String version = config.getSettingValue(OptiFineSetting.INSTANCE);
                    return new OptiFineExisting(launcher.resolve("libraries"), version);
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
