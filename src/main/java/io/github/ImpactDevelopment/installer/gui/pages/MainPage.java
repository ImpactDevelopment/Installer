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

package io.github.ImpactDevelopment.installer.gui.pages;

import io.github.ImpactDevelopment.installer.gui.AppWindow;
import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.Setting;
import io.github.ImpactDevelopment.installer.setting.settings.*;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainPage extends JPanel {
    public MainPage(AppWindow app) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addPathSetting(MinecraftDirectorySetting.INSTANCE, "Minecraft directory", app);
        addSetting(InstallationModeSetting.INSTANCE, "Install for", app);
        addSetting(MinecraftVersionSetting.INSTANCE, "Minecraft version", app);
        addSetting(ImpactVersionSetting.INSTANCE, "Impact version", app);
        addSetting(OptiFineSetting.INSTANCE, "OptiFine version", app);

        JButton install = new JButton("Install");
        install.addActionListener((ActionEvent) -> {
            try {
                String msg = app.config.execute();
                JOptionPane.showMessageDialog(app, msg, "\uD83D\uDE0E", JOptionPane.INFORMATION_MESSAGE);
            } catch (Throwable e) {
                app.exception(e);
            }
        });
        add(install);
    }

    private <T> void addSetting(ChoiceSetting<T> setting, String text, AppWindow app) {
        T val = app.config.getSettingValue(setting);
        if (val == null) {
            return;
        }
        InstallationConfig config = app.config;
        JPanel container = new JPanel(new FlowLayout());
        if (val.equals(OptiFineSetting.MISSING)) {
            container.add(new JLabel("No OptiFine installation is detected for Minecraft " + config.getSettingValue(MinecraftVersionSetting.INSTANCE)));
            JButton button = new JButton();
            button.setText("<html>If you need OptiFine, install it separately beforehand. <font color=\"#0000CC\"><u>https://optifine.net/downloads</u></font></html>");
            button.setBackground(Color.WHITE);
            button.setBorderPainted(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setOpaque(false);
            button.addActionListener((ActionEvent) -> {
                try {
                    Desktop.getDesktop().browse(new URI("https://optifine.net/downloads"));
                } catch (Exception ex) {
                    app.exception(ex);
                }
            });
            container.add(button);
            add(container);
            return;
        }
        container.add(new JLabel(text + ": "));
        JComboBox<String> comboBox = new JComboBox<>(setting.getPossibleValues(config).stream().map(v -> setting.displayName(config, v)).toArray(String[]::new));
        comboBox.setSelectedIndex(setting.getPossibleValues(config).indexOf(val));
        comboBox.addActionListener((ActionEvent) -> {
            try {
                config.setSettingValue(setting, setting.getPossibleValues(config).get(comboBox.getSelectedIndex()));
            } catch (Throwable e) {
                app.exception(e);
                config.setSettingValue(setting, val);
            }
            app.recreate();
        });
        container.add(comboBox);
        if (!val.equals(OptiFineSetting.NONE) && setting instanceof OptiFineSetting) {
            container.add(new JLabel("OptiFine can sometimes cause visual glitches in Impact; only include it if you need it."));
        }
        add(container);
    }

    private void addPathSetting(Setting<Path> setting, String text, AppWindow app) {
        InstallationConfig config = app.config;
        Path current = config.getSettingValue(setting);
        JPanel container = new JPanel();
        container.setLayout(new FlowLayout());
        JTextField field = new JTextField(current.toString());
        field.setColumns(15);
        JButton button = new JButton("Browse");
        JFileChooser dialog = new JFileChooser(current.toFile());
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        field.addActionListener(event -> {
            Path newPath = Paths.get(field.getText());
            config.setSettingValue(setting, newPath);
            app.recreate();
        });
        button.addActionListener(event -> {
            int ret = dialog.showOpenDialog(app);
            if (ret == JFileChooser.APPROVE_OPTION) {
                Path newPath = dialog.getSelectedFile().toPath();
                config.setSettingValue(setting, newPath);
                app.recreate();
            }
        });
        container.add(new JLabel(text + ": "));
        container.add(field);
        container.add(button);
        add(container);
    }
}
