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

package io.github.ImpactDevelopment.installer.target.targets;

import com.google.gson.JsonObject;
import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.target.InstallationMode;

import javax.swing.*;

public class ShowJSON implements InstallationMode {
    private final InstallationConfig config;

    public ShowJSON(InstallationConfig config) {
        this.config = config;
    }

    @Override
    public String apply() {
        JsonObject toDisplay = new Vanilla(config).generateJsonVersion();
        String data = Installer.gson.toJson(toDisplay);
        if (Installer.args.noGUI) {
            return data;
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(toDisplay.get("id").getAsString());
            JTextArea area = new JTextArea();
            area.setEditable(true);
            area.append(data);
            area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            frame.getContentPane().add(new JScrollPane(area));
            frame.setSize(690, 420);
            frame.setVisible(true);
        });
        return "Here is the JSON for " + toDisplay.get("id");
    }
}
