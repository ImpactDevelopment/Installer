package io.github.ImpactDevelopment.installer.profiles;

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
            if (profiles.has(name)) {
                profiles.remove(name); // just in case (shouldn't happen)

            }

            profiles.add(name, profile = new JsonObject());
            profile.addProperty("name", name);
        } else { // Mutate mode
            profile = profiles.get(id.get()).getAsJsonObject();
        }
        if (profile.has("lastUsed")) {
            profile.remove("lastUsed");
        }
        profile.addProperty("lastUsed", dateFormat.format(new Date())); // always bump.

        if (profile.has("lastVersionId")) {
            profile.remove("lastVersionId");
        }
        profile.addProperty("lastVersionId", version);

        if (profile.has("icon")) {
            profile.remove("icon");
        }
        profile.addProperty("icon", ICON);
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
}
