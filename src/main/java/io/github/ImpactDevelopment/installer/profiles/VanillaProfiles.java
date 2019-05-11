package io.github.ImpactDevelopment.installer.profiles;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.ImpactDevelopment.installer.Installer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Optional;

import static io.github.ImpactDevelopment.installer.Installer.dateFormat;
import static io.github.ImpactDevelopment.installer.OperatingSystem.getMinecraftDirectory;

public class VanillaProfiles {

    private static final Path LAUNCHER_PROFILES = getMinecraftDirectory().resolve("launcher_profiles.json");

    private final JsonObject json;

    public VanillaProfiles() throws IOException {
        this.json = loadFileToJson();
    }

    public void addOrMutate(String name, String version) {
        JsonObject profiles = getProfilesList();
        JsonObject profile;

        Optional<String> id = findProfileIdFromName(name);
        if (!id.isPresent()) { // Create mode
            if (profiles.has(name)) profiles.remove(name); // just in case (shouldn't happen)

            profiles.add(name, profile = new JsonObject());
            profile.addProperty("name", name);
            profile.addProperty("lastUsed", dateFormat.format(new Date())); //TODO consider always bumping?
        } else { // Mutate mode
            profile = profiles.get(id.get()).getAsJsonObject();
        }

        if (profile.has("lastVersionId")) profile.remove("lastVersionId");
        profile.addProperty("lastVersionId", version);
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
            if (!entry.getValue().isJsonObject()) continue;
            JsonObject profile = entry.getValue().getAsJsonObject();

            if (!profile.has("name") || !profile.get("name").isJsonPrimitive()) continue;
            String profileName = profile.get("name").getAsString();

            if (name.equals(profileName)) return Optional.of(entry.getKey());
        }
        return Optional.empty();
    }

    private JsonObject getProfilesList() {
        if (!json.has("profiles"))
            json.add("profiles", new JsonObject());
        if (!json.get("profiles").isJsonObject())
            throw new RuntimeException(String.format("\"profiles\" is not an object in \"%s\"", LAUNCHER_PROFILES.toAbsolutePath().toString()));

        return json.get("profiles").getAsJsonObject();
    }

    private JsonObject loadFileToJson() throws IOException {
        byte[] bytes = Files.readAllBytes(LAUNCHER_PROFILES);
        String json = new String(bytes, StandardCharsets.UTF_8);
        return new JsonParser().parse(json).getAsJsonObject();
    }

    public void saveToDisk() throws IOException {
        byte[] bytes = Installer.gson.toJson(json).getBytes(StandardCharsets.UTF_8);
        Files.write(LAUNCHER_PROFILES, bytes);
    }
}
