package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.StreamSupport;

public enum OptiFineSetting implements ChoiceSetting<String> {
    INSTANCE;

    @Override
    public String[] getPossibleValues(InstallationConfig config) {
        String minecraftVersion = config.getSettingValue(MinecraftVersionSetting.INSTANCE);
        Path minecraftDirectory = config.getSettingValue(MinecraftDirectorySetting.INSTANCE);

        System.out.println("Fetching installed OptiFine versions for " + minecraftVersion);
        try {
            return StreamSupport.stream(Files.newDirectoryStream(minecraftDirectory.resolve("libraries").resolve("optifine").resolve("OptiFine")).spliterator(), false)
                    .map(Path::getFileName)
                    .map(Object::toString)
                    .filter(name -> name.startsWith(minecraftVersion))
                    .sorted(Comparator.reverseOrder())
                    .toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }
}
