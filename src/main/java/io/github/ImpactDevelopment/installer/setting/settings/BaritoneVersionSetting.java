package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.impact.ImpactJsonVersion;
import io.github.ImpactDevelopment.installer.libraries.LibraryBaritone;
import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

import java.util.Optional;

public enum BaritoneVersionSetting implements ChoiceSetting<LibraryBaritone> {
    INSTANCE;

    @Override
    public LibraryBaritone[] getPossibleValues(InstallationConfig config) {
        ImpactJsonVersion impact = config.getSettingValue(ImpactVersionSetting.INSTANCE).fetchContents();
        Optional<String> versionFilter = impact.baritoneVersionFilter();
        // this impact version is before baritone
        LibraryBaritone[] empty = new LibraryBaritone[0];
        return versionFilter
                .map(vf -> LibraryBaritone.getVersionsMatching(vf).toArray(empty))
                .orElse(empty);
    }
}
