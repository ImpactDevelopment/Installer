package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.OperatingSystem;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.Setting;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum MinecraftDirectorySetting implements Setting<Path> {
    INSTANCE;

    @Override
    public Path getDefaultValue(InstallationConfig config) {
        switch (OperatingSystem.getOS()) {
            case WINDOWS:
                return Paths.get(System.getenv("APPDATA")).resolve(".minecraft");
            case OSX:
                return Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Application Support").resolve("minecraft");
            default:
                return Paths.get(System.getProperty("user.home")).resolve(".minecraft");
        }
    }

    @Override
    public boolean validSetting(InstallationConfig config, Path value) {
        // we are ALL minecraft paths on this blessed day
        return true;
    }
}
