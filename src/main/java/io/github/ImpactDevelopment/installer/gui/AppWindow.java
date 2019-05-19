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

package io.github.ImpactDevelopment.installer.gui;

import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.gui.pages.MainPage;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

import javax.swing.*;

import static io.github.ImpactDevelopment.installer.OperatingSystem.OSX;
import static io.github.ImpactDevelopment.installer.OperatingSystem.getOS;

public class AppWindow extends JFrame {

    public final InstallationConfig config = new InstallationConfig();
    private final JPanel wrapper;

    public AppWindow() {
        setTitle(Installer.getTitle());
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

    public void exception(Throwable e) {
        try {
            throw e;
        } catch (ExceptionInInitializerError ex) {
            exception(ex.getCause());
        } catch (Throwable th) {
            th.printStackTrace();
            JOptionPane.showMessageDialog(this, th + "\n" + th.getCause(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
