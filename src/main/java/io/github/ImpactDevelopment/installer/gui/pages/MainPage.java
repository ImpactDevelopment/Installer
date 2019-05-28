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

package io.github.ImpactDevelopment.installer.gui.pages;

import io.github.ImpactDevelopment.installer.gui.AppWindow;
import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.Setting;
import io.github.ImpactDevelopment.installer.setting.settings.*;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainPage extends JPanel {
    public MainPage(AppWindow app) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addPathSetting(MinecraftDirectorySetting.INSTANCE, "Minecraft directory", app);
        addSetting(InstallationModeSetting.INSTANCE, "Install for", app);
        addSetting(MinecraftVersionSetting.INSTANCE, "Minecraft version", app);
        addSetting(ImpactVersionSetting.INSTANCE, "Impact version", app);
        addSetting(BaritoneVersionSetting.INSTANCE, "Baritone version", app);
        addSetting(OptiFineSetting.INSTANCE, "OptiFine version", app);

        JButton install = new JButton("Install");
        install.addActionListener((ActionEvent) -> {
            try {
                app.config.getSettingValue(InstallationModeSetting.INSTANCE).mode.apply(app.config).apply();
                JOptionPane.showMessageDialog(app, "Impact has been successfully installed", "\uD83D\uDE0E", JOptionPane.INFORMATION_MESSAGE);
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
