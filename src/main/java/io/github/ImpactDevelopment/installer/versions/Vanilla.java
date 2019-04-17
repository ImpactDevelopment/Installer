package io.github.ImpactDevelopment.installer.versions;

import com.google.gson.annotations.Expose;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static io.github.ImpactDevelopment.installer.OperatingSystem.getMinecraftDirectory;

public class Vanilla {

    private final String id;
    private final String json;

    public Vanilla(String id, String json) {
        this.id = id;
        this.json = json;
    }

    public void saveToDisk() throws IOException {
        Path directory = getMinecraftDirectory().resolve("versions").resolve(id);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create \"" + directory.toAbsolutePath().toString() + "\"");
            }
        }

        Files.write(directory.resolve(id + ".json"), json.getBytes(StandardCharsets.UTF_8));
    }

    public String getId() {
        return id;
    }

    private class Arguments {

        @Expose
        private final List<String> game;

        private Arguments(String... args) {
            this(Arrays.asList(args));
        }

        private Arguments(List<String> args) {
            game = args;
        }
    }
}
