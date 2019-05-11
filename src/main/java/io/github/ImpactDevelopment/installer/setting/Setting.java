package io.github.ImpactDevelopment.installer.setting;

public interface Setting<T> {
    T getDefaultValue(InstallationConfig config);

    boolean validSetting(InstallationConfig config, T value);
}
