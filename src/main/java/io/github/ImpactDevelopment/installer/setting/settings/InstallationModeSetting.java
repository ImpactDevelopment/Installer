package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.versions.InstallationModeOptions;

import java.util.Arrays;
import java.util.List;

public enum InstallationModeSetting implements ChoiceSetting<InstallationModeOptions> {
    INSTANCE;

    @Override
    public List<InstallationModeOptions> getPossibleValues(InstallationConfig config) {
        return Arrays.asList(InstallationModeOptions.values());
    }
}
