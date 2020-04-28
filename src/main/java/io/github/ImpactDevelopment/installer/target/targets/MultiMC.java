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

package io.github.ImpactDevelopment.installer.target.targets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.MultiMCDirectorySetting;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.StreamSupport;

public class MultiMC extends Vanilla {

    private final String instanceName;
    private final String instanceID;
    private final Path mmc;
    private final Path instance;

    public MultiMC(InstallationConfig config) {
        super(config);

        this.instanceName = version.name + " " + getStrippedVersion() + " for " + version.mcVersion + (optifine == null ? "" : " with OptiFine " + optifine.getOptiFineVersion());
        this.instanceID = version.name + "-" + getStrippedVersion() + "-" + version.mcVersion + (optifine == null ? "" : "-OptiFine-" + optifine.getOptiFineVersion());
        this.mmc = config.getSettingValue(MultiMCDirectorySetting.INSTANCE);
        this.instance = mmc.resolve("instances").resolve(instanceID);
    }

    @Override
    public String apply() {
        JsonObject toDisplay = generateJsonVersion();
        String data = Installer.gson.toJson(toDisplay);
        if (Installer.args.noGUI) {
            return data;
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(toDisplay.get("version").getAsString());
            JTextArea area = new JTextArea();
            area.setEditable(true);
            area.append(data);
            area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            frame.getContentPane().add(new JScrollPane(area));
            frame.setSize(690, 420);
            frame.setVisible(true);
        });
        return "Here is the JSON for MultiMC " + toDisplay.get("version");
    }

    @Override
    public String installOptifine() throws IOException {
        if (optifine == null) {
            throw new IllegalStateException("No optifine specified, cannot install OptiFine");
        }

        optifine.install(instance.resolve("libraries"), vanillaJar, false);
        return "Installed OptiFine successfully";
    }

    @Override
    public JsonObject generateJsonVersion() {
        JsonObject object = new JsonObject();
        object.addProperty("fileID", "net.impactclient.Impact");
        object.addProperty("mainClass", version.mainClass);
        object.addProperty("mcVersion", version.mcVersion);
        object.addProperty("name", "Impact " + version.version);
        object.addProperty("order", 10);
        object.addProperty("version", id);
        object.add("+tweakers", generateTweakers());
        JsonArray libraries = generateLibraries();

        // Append "MMC-hint": "local" to any optifine libraries
        StreamSupport.stream(libraries.spliterator(), false)
                .filter(lib -> {
                    try {
                        return lib.getAsJsonObject().getAsJsonPrimitive("name").getAsString().startsWith("optifine:");
                    } catch (NullPointerException | ClassCastException | IllegalStateException ignored) {
                        return false;
                    }
                })
                .map(JsonElement::getAsJsonObject)
                .forEach(lib -> lib.addProperty("MMC-hint", "local"));

        object.add("+libraries", libraries);
        return object;
    }

    private JsonArray generateTweakers() {
        JsonArray arrayTweakers = new JsonArray();
        version.tweakers.forEach(arrayTweakers::add);
        return arrayTweakers;
    }
}
