package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.impact.ImpactJsonVersion;
import io.github.ImpactDevelopment.installer.libraries.LibraryBaritone;
import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public enum BaritoneVersionSetting implements ChoiceSetting<LibraryBaritone> {
    INSTANCE;

    @Override
    public List<LibraryBaritone> getPossibleValues(InstallationConfig config) {
        ImpactJsonVersion impact = config.getSettingValue(ImpactVersionSetting.INSTANCE).fetchContents();
        Optional<String> versionFilter = impact.baritoneVersionFilter();
        if (versionFilter.isPresent()) {
            return LibraryBaritone.getVersionsMatching(versionFilter.get());
        }
        return Collections.emptyList();
    }

    @Override
    public String displayName(InstallationConfig config, LibraryBaritone option) {
        String ret = option.getVersion();
        if (getPossibleValues(config).indexOf(option) == 0) { // hitting nae nae on the O(n^2)
            ret += " (latest)";
        }
        return ret;
    }
}
