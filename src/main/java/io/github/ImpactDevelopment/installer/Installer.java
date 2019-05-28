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

package io.github.ImpactDevelopment.installer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ImpactDevelopment.installer.gui.AppIcon;
import io.github.ImpactDevelopment.installer.gui.AppWindow;

import javax.swing.*;
import java.text.SimpleDateFormat;

import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.*;

public class Installer {
    public static final String project = "Impact";
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static void main(String... args) throws Throwable {

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

        SwingUtilities.invokeLater(AppWindow::new);
    }

    public static String getTitle() {
        return project + " Installer";
    }
}
