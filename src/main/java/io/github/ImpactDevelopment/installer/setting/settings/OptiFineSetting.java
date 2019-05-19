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

package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.versions.InstallationModeOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public enum OptiFineSetting implements ChoiceSetting<String> {
    INSTANCE;

    public static final String NONE = "None";

    @Override
    public List<String> getPossibleValues(InstallationConfig config) {
        if (config.getSettingValue(InstallationModeSetting.INSTANCE) == InstallationModeOptions.FORGE) {
            return Collections.emptyList();
        }
        String minecraftVersion = config.getSettingValue(MinecraftVersionSetting.INSTANCE);
        Path minecraftDirectory = config.getSettingValue(MinecraftDirectorySetting.INSTANCE);

        List<String> result = new ArrayList<>();
        try {
            result.addAll(StreamSupport.stream(Files.newDirectoryStream(minecraftDirectory.resolve("libraries").resolve("optifine").resolve("OptiFine")).spliterator(), false)
                    .map(Path::getFileName)
                    .map(Object::toString)
                    .filter(name -> name.startsWith(minecraftVersion + "_"))
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        result.add(NONE);
        return result;
    }
}
