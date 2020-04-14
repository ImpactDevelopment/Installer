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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainPage extends JPanel {
    public MainPage(AppWindow app) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(buildPathSetting(MinecraftDirectorySetting.INSTANCE, "Minecraft directory", JFileChooser.DIRECTORIES_ONLY, app));
        addSetting(InstallationModeSetting.INSTANCE, "Install for", app);
        addSetting(MinecraftVersionSetting.INSTANCE, "Minecraft version", app);
        addSetting(ImpactVersionSetting.INSTANCE, "Impact version", app);
        addOptifineSetting(app);

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

    private void addOptifineSetting(AppWindow app) {
        OptiFineSetting setting = OptiFineSetting.INSTANCE;
        InstallationConfig config = app.config;
        Boolean val = config.getSettingValue(setting);
        if (val == null) {
            return;
        }

        JPanel grid = new JPanel(new GridLayout(4, 1, 0, 0));

        JPanel radial = new JPanel(new FlowLayout());

        radial.add(new JLabel("Include OptiFine? "));

        ActionListener listener = (event) -> {
            try {
                config.setSettingValue(setting, event.getActionCommand().equalsIgnoreCase("yes"));
            } catch (Throwable e) {
                app.exception(e);
                config.setSettingValue(setting, val);
            }
            app.recreate();
        };
        JRadioButton yes = new JRadioButton("Yes");
        JRadioButton no = new JRadioButton("No");
        yes.setMnemonic(KeyEvent.VK_Y);
        yes.addActionListener(listener);
        yes.setSelected(val);
        no.setMnemonic(KeyEvent.VK_N);
        no.addActionListener(listener);
        no.setSelected(!val);
        ButtonGroup group = new ButtonGroup();
        group.add(yes);
        group.add(no);
        radial.add(yes);
        radial.add(no);

        grid.add(radial);

        if (val) {
            grid.add(buildPathSetting(OptiFineFileSetting.INSTANCE, "OptiFine jar", JFileChooser.FILES_ONLY, app));


            grid.add(new JLabel("OptiFine can sometimes cause visual glitches in Impact; only include it if you need it!"));

            JButton link = new JButton();
            link.setText("<html>You can download OptiFine from their website: <font color=\"#0000CC\"><u>https://optifine.net/downloads</u></font></html>");
            link.setBackground(Color.WHITE);
            link.setBorderPainted(false);
            link.setHorizontalAlignment(SwingConstants.LEFT);
            link.setOpaque(false);
            link.addActionListener((ActionEvent) -> {
                try {
                    Desktop.getDesktop().browse(new URI("https://optifine.net/downloads"));
                } catch (Exception ex) {
                    app.exception(ex);
                }
            });
            grid.add(link);
        }

        add(grid);
    }

    private JPanel buildPathSetting(Setting<Path> setting, String text, int selectionMode, AppWindow app) {
        InstallationConfig config = app.config;
        Path current = config.getSettingValue(setting);
        JPanel container = new JPanel(new FlowLayout());
        JTextField field = new JTextField(current.toString());
        field.setColumns(15);
        JButton button = new JButton("Browse");
        JFileChooser dialog = new JFileChooser(current.toFile());
        dialog.setFileSelectionMode(selectionMode);
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
        return container;
    }
}
