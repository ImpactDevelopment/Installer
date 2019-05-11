package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.impact.ImpactVersion;
import io.github.ImpactDevelopment.installer.impact.ImpactVersions;
import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

import java.util.Comparator;

public enum ImpactVersionSetting implements ChoiceSetting<ImpactVersion> {
    INSTANCE;

    @Override
    public ImpactVersion[] getPossibleValues(InstallationConfig config) {
        String mcVersion = config.getSettingValue(MinecraftVersionSetting.INSTANCE);
        return ImpactVersions.getAllVersions().stream()
                .filter(version -> mcVersion.equals(version.mcVersion))
                .sorted(Comparator.comparing((ImpactVersion version) -> version.impactVersion).reversed())
                .toArray(ImpactVersion[]::new);
    }
}
