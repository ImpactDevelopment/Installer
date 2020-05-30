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
import io.github.ImpactDevelopment.installer.gui.JHyperlink;
import io.github.ImpactDevelopment.installer.setting.ChoiceSetting;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.Setting;
import io.github.ImpactDevelopment.installer.setting.settings.*;
import io.github.ImpactDevelopment.installer.target.InstallationModeOptions;
import io.github.ImpactDevelopment.installer.utils.OperatingSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static io.github.ImpactDevelopment.installer.setting.settings.OptiFineSetting.CUSTOM;
import static io.github.ImpactDevelopment.installer.setting.settings.OptiFineSetting.MISSING;
import static io.github.ImpactDevelopment.installer.target.InstallationModeOptions.MULTIMC;
import static io.github.ImpactDevelopment.installer.target.InstallationModeOptions.SHOWJSON;
import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.OSX;
import static javax.swing.JOptionPane.*;

public class MainPage extends JPanel {
    public MainPage(AppWindow app) {
        InstallationModeOptions mode = app.config.getSettingValue(InstallationModeSetting.INSTANCE);

        this.setLayout(new BorderLayout());
        JPanel settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));

        settings.add(buildSetting(InstallationModeSetting.INSTANCE, "Install for", app));
        if (mode == MULTIMC) settings.add(buildMultiMCSetting(app));
        settings.add(buildSetting(MinecraftVersionSetting.INSTANCE, "Minecraft version", app));
        settings.add(buildSetting(ImpactVersionSetting.INSTANCE, "Impact version", app));
        switch (mode) {
            case FORGE: case FORGE_PLUS_LITELOADER: break;
            default: settings.add(buildOptiFineSetting(app));
        }

        // Add the settings to the top of the window
        this.add(settings, BorderLayout.PAGE_START);

        JButton install = new JButton(mode.getButtonText());
        install.addActionListener((ActionEvent) -> {
            try {
                Optional<Path> destination = Optional.ofNullable(app.config.getSettingValue(DestinationSetting.INSTANCE));
                if (destination.isPresent()) {
                    Path dest = destination.get();
                    // JFileChooser won't open in a directory unless it exists;
                    // If destination is inside .minecraft and .minecraft exists, create the dest dir
                    Path defaultLauncher = app.config.getSettingValue(MinecraftDirectorySetting.INSTANCE);
                    if (Files.isDirectory(defaultLauncher) && dest.toAbsolutePath().startsWith(defaultLauncher.toAbsolutePath())) {
                        Files.createDirectories(dest.getParent());
                    }
                    // Otherwise, find the nearest existing parent.
                    // (we don't want to create random directories just to open a Save dialog)
                    Path dir = dest;
                    while (dir != null && !Files.isDirectory(dir)) {
                        dir = dir.getParent();
                    }
                    // If we couldn't find a directory, set a default
                    if (dir == null) {
                        dir = OperatingSystem.getHome();
                    }

                    JFileChooser dialog = new JFileChooser(dir.toFile());
                    dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    dialog.setSelectedFile(dest.toFile());
                    switch (dialog.showSaveDialog(app)) {
                        case JFileChooser.APPROVE_OPTION:
                            // save to destination
                            app.config.setSettingValue(DestinationSetting.INSTANCE, dialog.getSelectedFile().toPath());
                            break;
                        case JFileChooser.ERROR_OPTION:
                            throw new RuntimeException("Unexpected error in save dialog");
                        case JFileChooser.CANCEL_OPTION:
                            return;
                    }
                }

                String msg = app.config.execute();

                // Special case if installing optifine in showJson mode
                if (mode == SHOWJSON && app.config.getSettingValue(OptiFineToggleSetting.INSTANCE)) {
                    msg += "\nDo you want to install OptiFine's libs?";
                    if (YES_OPTION == JOptionPane.showConfirmDialog(app, msg, "\uD83D\uDE0E", YES_NO_OPTION, INFORMATION_MESSAGE)) {
                        msg = app.config.installOptifine();
                    } else {
                        return; // Early return if no more msg dialogs needed
                    }
                }

                JOptionPane.showMessageDialog(app, msg, "\uD83D\uDE0E", INFORMATION_MESSAGE);
            } catch (Throwable e) {
                app.exception(e);
            }
        });

        // Add the button to the bottom of the window
        JPanel installPanel = new JPanel(new FlowLayout());
        installPanel.add(install);
        this.add(installPanel, BorderLayout.PAGE_END);
    }

    private <T> JPanel buildSetting(ChoiceSetting<T> setting, String text, AppWindow app) {
        T val = app.config.getSettingValue(setting);
        if (val == null) {
            throw new IllegalStateException("Cannot build setting for null value of " + setting.getClass().getSimpleName());
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

        return container;
    }

    private JPanel buildMultiMCSetting(AppWindow app) {
        String label = "MultiMC " + (OperatingSystem.getOS() == OSX ? "application" : "directory");
        return buildPathSetting(MultiMCDirectorySetting.INSTANCE,  label, JFileChooser.DIRECTORIES_ONLY, app);
    }

    private JPanel buildOptiFineSetting(AppWindow app) {
        OptiFineToggleSetting setting = OptiFineToggleSetting.INSTANCE;
        InstallationConfig config = app.config;
        Boolean usingOptiFine = config.getSettingValue(setting);
        if (usingOptiFine == null) {
            throw new IllegalStateException("Cannot build setting for null value of " + setting.getClass().getSimpleName());
        }

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));

        JPanel radial = new JPanel(new FlowLayout());

        radial.add(new JLabel("Include OptiFine? "));

        ActionListener listener = (event) -> {
            try {
                config.setSettingValue(setting, event.getActionCommand().equalsIgnoreCase("yes"));
            } catch (Throwable e) {
                app.exception(e);
                config.setSettingValue(setting, usingOptiFine);
            }
            app.recreate();
        };
        JRadioButton yes = new JRadioButton("Yes");
        JRadioButton no = new JRadioButton("No");
        yes.setMnemonic(KeyEvent.VK_Y);
        yes.addActionListener(listener);
        yes.setSelected(usingOptiFine);
        no.setMnemonic(KeyEvent.VK_N);
        no.addActionListener(listener);
        no.setSelected(!usingOptiFine);
        ButtonGroup group = new ButtonGroup();
        group.add(yes);
        group.add(no);
        radial.add(yes);
        radial.add(no);

        grid.add(radial);

        if (usingOptiFine) {
            JPanel warning = new JPanel(new FlowLayout());
            warning.add(new JLabel("<html><center>OptiFine can sometimes cause visual issues in Impact;<br>only include it if you need it!</center></html>"));
            grid.add(warning);

            String version = config.getSettingValue(OptiFineSetting.INSTANCE);
            if (!version.equals(MISSING)) {
                grid.add(buildSetting(OptiFineSetting.INSTANCE, "OptiFine version", app));
            }
            switch (version) {
                case MISSING:
                case CUSTOM:
                    grid.add(buildPathSetting(OptiFineFileSetting.INSTANCE, "OptiFine installer", JFileChooser.FILES_ONLY, app));
            }

            try {
                FlowLayout layout = new FlowLayout();
                layout.setHgap(0);
                JPanel download = new JPanel(layout);
                JLabel text = new JLabel("<html>You can download OptiFine from their website: </html>");
                download.add(text);

                JHyperlink link = new JHyperlink("optifine.net", "https://optifine.net/downloads");
                download.add(link);

                grid.add(download);
            } catch (URISyntaxException e) {
                throw new RuntimeException("", e);
            }
        }

        return grid;
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
