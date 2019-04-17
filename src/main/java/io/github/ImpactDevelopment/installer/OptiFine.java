package io.github.ImpactDevelopment.installer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class OptiFine {

    public static Optional<String> getInstalledOptiFineVersion(String minecraftVersion) {
        System.out.println("Fetching installed OptiFine versions for " + minecraftVersion);
        try {
            return StreamSupport
                    .stream(Files.newDirectoryStream(OperatingSystem.getMinecraftDirectory().resolve("libraries").resolve("optifine").resolve("OptiFine")).spliterator(), false)
                    .map(Path::getFileName)
                    .map(Object::toString)
                    .filter(name -> name.startsWith(minecraftVersion))
                    .max(Comparator.naturalOrder());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
