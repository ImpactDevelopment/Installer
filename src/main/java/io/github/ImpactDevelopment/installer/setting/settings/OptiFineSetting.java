/*
 * This file is part of Impact Installer.
 *
 * Copyright (C) 2019  ImpactDevelopment and contributors
 *
 * See the CONTRIBUTORS.md file for a list of copyright holders
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package io.github.ImpactDevelopment.installer.setting.settings;

import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.target.InstallationModeOptions;

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
    public static final String MISSING = "Missing";

    @Override
    public List<String> getPossibleValues(InstallationConfig config) {
        if (config.getSettingValue(InstallationModeSetting.INSTANCE) == InstallationModeOptions.FORGE || config.getSettingValue(MinecraftVersionSetting.INSTANCE).compareTo("1.14.4") > 0) {
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
        if (result.isEmpty()) {
            result.add(MISSING);
        } else {
            result.add(0, NONE);
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
