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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.MinecraftDirectorySetting;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Optional;

import static io.github.ImpactDevelopment.installer.Installer.dateFormat;

public class VanillaProfiles {

    private static final String ICON;

    private final Path launcherProfiles;

    private final JsonObject json;

    static {
        try {
            //noinspection ConstantConditions
            ICON = "data:image/png;base64," + Base64.getEncoder().encodeToString(IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("icons/128.png")));
        } catch (IOException e) {
            throw new RuntimeException("getting icon", e);
        }
    }

    public VanillaProfiles(InstallationConfig config) throws IOException {
        this.launcherProfiles = config.getSettingValue(MinecraftDirectorySetting.INSTANCE).resolve("launcher_profiles.json");
        this.json = loadFileToJson();
    }

    public void addOrMutate(String name, String version) {
        JsonObject profiles = getProfilesList();
        JsonObject profile;

        Optional<String> id = findProfileIdFromName(name);
        if (!id.isPresent()) { // Create mode
            profiles.add(name, profile = new JsonObject());
            profile.addProperty("name", name);
        } else { // Mutate mode
            profile = profiles.get(id.get()).getAsJsonObject();
        }
        profile.addProperty("lastUsed", dateFormat.format(new Date())); // always bump.
        profile.addProperty("lastVersionId", version);
        profile.addProperty("icon", ICON);
        profile.addProperty("type", "custom");
    }

    /**
     * Find a profile with a matching name
     *
     * @param name the name to look for
     * @return the id of the first match
     */
    private Optional<String> findProfileIdFromName(String name) {
        JsonObject profiles = getProfilesList();
        for (Entry<String, JsonElement> entry : profiles.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            JsonObject profile = entry.getValue().getAsJsonObject();

            if (!profile.has("name") || !profile.get("name").isJsonPrimitive()) {
                continue;
            }
            String profileName = profile.get("name").getAsString();

            if (name.equals(profileName)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    private JsonObject getProfilesList() {
        if (!json.has("profiles")) {
            json.add("profiles", new JsonObject());
        }
        if (!json.get("profiles").isJsonObject()) {
            throw new RuntimeException(String.format("\"profiles\" is not an object in \"%s\"", launcherProfiles.toAbsolutePath().toString()));
        }

        return json.get("profiles").getAsJsonObject();
    }

    private JsonObject loadFileToJson() throws IOException {
        byte[] bytes = Files.readAllBytes(launcherProfiles);
        String json = new String(bytes, StandardCharsets.UTF_8);
        return new JsonParser().parse(json).getAsJsonObject();
    }

    public void saveToDisk() throws IOException {
        System.out.println("Saving modified profiles to " + launcherProfiles);
        byte[] bytes = Installer.gson.toJson(json).getBytes(StandardCharsets.UTF_8);
        Files.write(launcherProfiles, bytes);
    }

    public static void checkDirectory(Path path) {
        if (Files.exists(path.resolve("launcher_profiles.json"))) {
            return;
        }
        if (Files.exists(path.getParent().resolve("launcher_profiles.json"))) {
            throw new RuntimeException("Invalid Minecraft path. Did you mean " + path.getParent() + "?");
        }
        throw new RuntimeException("Vanilla Minecraft not detected at " + path + ", have you opened the Minecraft launcher before?");
    }
}
