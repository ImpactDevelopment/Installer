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

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ImpactDevelopment.installer.gui.AppIcon;
import io.github.ImpactDevelopment.installer.gui.AppWindow;
import io.github.ImpactDevelopment.installer.impact.ImpactJsonVersion;
import io.github.ImpactDevelopment.installer.profiles.VanillaProfiles;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;
import io.github.ImpactDevelopment.installer.versions.Vanilla;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.text.SimpleDateFormat;

import static io.github.ImpactDevelopment.installer.OperatingSystem.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Installer {
    public static final String project = "Impact";
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    public static final Args args = new Args();

    public static void main(String... argv) throws Throwable {
        // Parse CLI arguments
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(argv);

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

    public static void install(InstallationConfig config) throws Exception { // really anything can happen lol
        ImpactJsonVersion version = config.getSettingValue(ImpactVersionSetting.INSTANCE).fetchContents();
        Vanilla vanilla = new Vanilla(config);
        System.out.println("Installing impact " + vanilla.getId());
        System.out.println("Info:");
        version.printInfo();

        System.out.println("Creating vanilla version");

        vanilla.apply();

        System.out.println("Loading existing vanilla profiles");
        VanillaProfiles profiles = new VanillaProfiles(config);
        System.out.println("Injecting impact version...");

        profiles.addOrMutate(version.name + " " + version.version + " for " + version.mcVersion, vanilla.getId());
        System.out.println("Saving vanilla profiles");
        profiles.saveToDisk();
    }

    public static String getTitle() {
        return project + " Installer";
    }

    public static boolean isMinecraftLauncherOpen() {
        try {
            if (getOS() == WINDOWS) {
                return IOUtils.toString(new ProcessBuilder("tasklist", "/fi", "WINDOWTITLE eq Minecraft Launcher").start().getInputStream(), UTF_8).contains("MinecraftLauncher.exe");
            }
            return IOUtils.toString(new ProcessBuilder("ps", "-ef").start().getInputStream(), UTF_8).contains("Minecraft Launcher");
        } catch (Throwable e) {
            return false;
        }
    }
}
