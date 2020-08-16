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

package io.github.ImpactDevelopment.installer.gui;

import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.gui.pages.MainPage;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.utils.Tracky;

import javax.swing.*;

import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.OSX;
import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.getOS;

public class AppWindow extends JFrame {

    public final InstallationConfig config;
    private final JPanel wrapper;

    public AppWindow(InstallationConfig config) {
        this.config = config;
        setTitle(Installer.getTitle() + " - " + Installer.getVersion());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        if (getOS() == OSX) { // window.setTitle() isn't good enough on OSX
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", getTitle());
        }
        wrapper = new JPanel();
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        setContentPane(wrapper);
        setContent(loadingScreen());
        setSize(690, 420);
        setResizable(false);
        setLocationRelativeTo(null);// center on screen
        setVisible(true);
        AppIcon.setAppIcon(this);
        recreate();
        Tracky.event("installer", "display", null);
    }

    public void recreate() {
        SwingUtilities.invokeLater(() -> {
            try {
                setContent(new MainPage(this));
                validate();
                repaint();
            } catch (Throwable e) {
                exception(e);
            }
        });
    }

    public void exception(Throwable th) {
        th.printStackTrace();
        String msg = th.getMessage() + "\n";
        if (th.getCause() != null) {
            msg += th.getCause();
            if (th.getCause().getCause() != null) {
                msg += "\n" + th.getCause().getCause();
            }
        }
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setContent(JPanel content) {
        wrapper.removeAll();
        wrapper.add(content);
    }

    private static JPanel loadingScreen() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(Box.createVerticalGlue());
        JLabel loading = new JLabel("Loading...");
        loading.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        container.add(loading);
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        container.add(bar);
        container.add(Box.createVerticalGlue());
        return container;
    }
}
