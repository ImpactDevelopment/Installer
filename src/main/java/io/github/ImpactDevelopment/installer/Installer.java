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

package io.github.ImpactDevelopment.installer;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ImpactDevelopment.installer.gui.AppIcon;
import io.github.ImpactDevelopment.installer.gui.AppWindow;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.utils.Tracky;

import javax.swing.*;
import java.text.SimpleDateFormat;

import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.*;

public class Installer {

    public static final String project = "Impact";
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    public static final Args args = new Args();

    public static void main(String... argv) throws Throwable {
        // Parse CLI arguments
        JCommander cmd = JCommander.newBuilder()
                .addObject(args)
                .args(argv)
                .build();
        if (args.showUsage) {
            cmd.usage();
            return;
        }
        if (args.showVersion) {
            System.out.println(getVersion());
            return;
        }


        InstallationConfig config = new InstallationConfig();
        args.apply(config);
        if (args.noGUI) {
            // run it now
            String ret = config.execute();
            System.out.println("Message: " + ret);
        } else {
            Tracky.awtEnabled();
            setupGUI(config);
        }
    }

    private static void setupGUI(InstallationConfig config) throws Throwable {
        // OSX systems should set swing.defaultlaf
        // explicitly setting the look and feel may override that
        // So we only do it on windows and linux where it probably isn't set
        // They can still override us with swing.crossplatformlaf
        if (getOS() != OSX) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }

        // Windows fucks OptionPane icons if DPI scaling is used
        // TODO consider rolling our own message solution
        if (getOS() == WINDOWS) {
            for (String key : new String[]{"OptionPane.warningIcon", "OptionPane.questionIcon", "OptionPane.errorIcon", "OptionPane.informationIcon"}) {
                UIManager.put(key, AppIcon.getLargestIcon(64));
            }
        }

        if (getOS() == OSX) {
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
        }

        SwingUtilities.invokeLater(() -> new AppWindow(config));
    }

    public static String getVersion() {
        return args.getClass().getPackage().getImplementationVersion();
    }

    public static String getTitle() {
        return project + " Installer";
    }
}
