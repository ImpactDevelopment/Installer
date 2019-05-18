package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.impact.ImpactVersions;
import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum MinecraftVersionSetting implements ChoiceSetting<String> {
    INSTANCE;

    @Override
    public List<String> getPossibleValues(InstallationConfig config) {
        return ImpactVersions.getAllVersions().stream()
                .filter(config.getSettingValue(InstallationModeSetting.INSTANCE)::supports)
                .map(version -> version.mcVersion)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }
}
