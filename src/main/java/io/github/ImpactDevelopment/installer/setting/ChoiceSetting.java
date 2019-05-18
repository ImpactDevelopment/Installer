package io.github.ImpactDevelopment.installer.setting;

import java.util.List;

public interface ChoiceSetting<T> extends Setting<T> {
    List<T> getPossibleValues(InstallationConfig config);

    default String displayName(InstallationConfig config, T option) {
        return option.toString();
    }

    @Override
    default T getDefaultValue(InstallationConfig config) {
        List<T> values = getPossibleValues(config);
        if (values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    @Override
    default boolean validSetting(InstallationConfig config, T value) {
        List<T> values = getPossibleValues(config);
        if (value == null) {
            return values.isEmpty();
        }
        return values.contains(value);
    }
}
