package io.github.ImpactDevelopment.installer.setting;

public interface ChoiceSetting<T> extends Setting<T> {
    T[] getPossibleValues(InstallationConfig config);

    @Override
    default T getDefaultValue(InstallationConfig config) {
        T[] values = getPossibleValues(config);
        if (values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    default boolean validSetting(InstallationConfig config, T value) {
        T[] values = getPossibleValues(config);
        if (value == null) {
            return values.length == 0;
        }
        for (T option : values) {
            if (option.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
