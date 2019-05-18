package io.github.ImpactDevelopment.installer.versions;

import io.github.ImpactDevelopment.installer.impact.ImpactVersion;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

import java.util.function.Function;

public enum InstallationModeOptions {
    VANILLA(Vanilla::new), FORGE(Forge::new);

    InstallationModeOptions(Function<InstallationConfig, InstallationMode> mode) {
        this.mode = mode;
    }

    public final Function<InstallationConfig, InstallationMode> mode;

    public boolean supports(ImpactVersion impact) {
        switch (this) {
            case FORGE:
                return impact.mcVersion.equals("1.12.2") && impact.impactVersion.equals("4.6");
            case VANILLA:
            default:
                return true;
        }
    }

    @Override
    public String toString() {
        // incredibly based code
        String name = super.toString();
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
