/*
 * This file is part of Impact Installer.
 *
 * Impact Installer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Impact Installer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impact Installer.  If not, see <https://www.gnu.org/licenses/>.
 */

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
