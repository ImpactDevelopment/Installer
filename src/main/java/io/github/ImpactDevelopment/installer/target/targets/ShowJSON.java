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
