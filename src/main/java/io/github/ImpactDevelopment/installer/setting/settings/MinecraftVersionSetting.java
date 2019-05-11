package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.impact.ImpactVersions;
import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

import java.util.Comparator;

public enum MinecraftVersionSetting implements ChoiceSetting<String> {
    INSTANCE;

    @Override
    public String[] getPossibleValues(InstallationConfig config) {
        return ImpactVersions.getAllVersions().stream()
                .map(version -> version.mcVersion)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toArray(String[]::new);
    }
}
