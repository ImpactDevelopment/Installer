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

import com.google.gson.*;
import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.MultiMCDirectorySetting;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.StreamSupport;

public class MultiMC extends Vanilla {

    private static final String ICON_KEY = "impact_512";

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
        object.addProperty("fileID", version.id);
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

    public JsonObject generateMMCPack() {
        // This can be fairly minimal, MMC will populate the lwjgl version and a bunch of caching metadata on first run
        JsonObject object = new JsonObject();
        JsonArray components = new JsonArray();
        JsonObject vanilla = new JsonObject();
        JsonObject impact = new JsonObject();

        impact.addProperty("uid", version.id);
        impact.addProperty("version", getId());
        impact.addProperty("important", true);
        vanilla.addProperty("uid", "net.minecraft");
        vanilla.addProperty("version", version.mcVersion);
        vanilla.addProperty("important", true);

        components.add(vanilla);
        components.add(impact);
        object.add("components", components);
        object.addProperty("formatVersion", 1);

        return object;
    }

    public String generateInstanceConfig() {
        StringBuilder cfg = new StringBuilder();
        cfg.append("InstanceType=OneSix\n");
        cfg.append("iconKey=").append(ICON_KEY).append("\n");
        cfg.append("name=").append(instanceName).append("\n");
        return cfg.toString();
    }

    public void addToGroup() throws IOException {
        Path instgroups = instance.getParent().resolve("insgroups.json");
        JsonObject json = new JsonObject();

        // Read the current file content
        if (Files.isRegularFile(instgroups)) {
            try {
                byte[] bytes = Files.readAllBytes(instgroups);
                String string = new String(bytes, StandardCharsets.UTF_8);
                json = new JsonParser().parse(string).getAsJsonObject();
            } catch (IllegalStateException | JsonParseException e) {
                e.printStackTrace();
            }
        }

        // Sanitise the json
        if (json.has("formatVersion")) {
            String v = json.getAsJsonPrimitive("formatVersion").getAsString();
            if (!v.equals("1")) {
                System.err.printf("Unexpected jormatVersion in instgroups.json found %s but expected %s%n", v, "1");
            }
        } else {
            json.addProperty("formatVersion", "1");
        }

        JsonObject groups = new JsonObject();
        if (json.has("groups")) {
            groups = json.getAsJsonObject("groups");
        } else {
            json.add("groups", groups);
        }

        JsonObject impact = new JsonObject();
        if (groups.has("Impact")) {
            impact = groups.getAsJsonObject("Impact");
        } else {
            groups.add("Impact", impact);
        }

        if (!impact.has("hidden")) {
            impact.addProperty("hidden", false);
        }

        JsonArray instances = new JsonArray();
        if (impact.has("instances")) {
            instances = impact.getAsJsonArray("instances");
        } else {
            impact.add("instances", instances);
        }

        // mutate the json if the id isn't already in the group
        if (StreamSupport.stream(instances.spliterator(), false)
                .map(element -> element.getAsJsonPrimitive().getAsString())
                .noneMatch(element -> element.equals(getId()))) {
            instances.add(getId());
        }

        // Write the modified json
        System.out.println("Saving modified instgroups.json to " + instgroups);
        byte[] bytes = Installer.gson.toJson(json).getBytes(StandardCharsets.UTF_8);
        Files.write(instgroups, bytes);
    }

    private JsonArray generateTweakers() {
        JsonArray arrayTweakers = new JsonArray();
        version.tweakers.forEach(arrayTweakers::add);
        return arrayTweakers;
    }
}
