package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.impact.ImpactVersion;
import io.github.ImpactDevelopment.installer.impact.ImpactVersions;
import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum ImpactVersionSetting implements ChoiceSetting<ImpactVersion> {
    INSTANCE;

    @Override
    public List<ImpactVersion> getPossibleValues(InstallationConfig config) {
        String mcVersion = config.getSettingValue(MinecraftVersionSetting.INSTANCE);
        return ImpactVersions.getAllVersions().stream()
                .filter(config.getSettingValue(InstallationModeSetting.INSTANCE)::supports)
                .filter(version -> mcVersion.equals(version.mcVersion))
                .sorted(Comparator.comparing((ImpactVersion version) -> version.impactVersion).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public String displayName(InstallationConfig config, ImpactVersion option) {
        String ret = option.impactVersion;
        if (getPossibleValues(config).indexOf(option) == 0) { // hitting nae nae on the O(n^2)
            ret += " (latest)";
        }
        return ret;
    }
}
