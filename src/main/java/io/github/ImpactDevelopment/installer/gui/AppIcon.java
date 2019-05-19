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

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.OSX;
import static io.github.ImpactDevelopment.installer.utils.OperatingSystem.getOS;

public class AppIcon {

    // This class is static
    private AppIcon() {}

    private static List<ImageIcon> icons;

    public static List<ImageIcon> getIcons() {
        if (icons == null) {
            icons = loadIcons();
        }
        return icons;
    }

    public static List<Image> getImages() {
        return getIcons().stream().map(ImageIcon::getImage).collect(Collectors.toList());
    }

    @Nullable
    public static ImageIcon getIcon(int size) {
        return getIcons().stream()
                .filter(icon -> icon.getIconHeight() == size)
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public static Image getImage(int size) {
        return Optional.ofNullable(getIcon(size))
                .map(ImageIcon::getImage)
                .orElse(null);
    }

    @Nullable
    public static ImageIcon getLargestIcon() {
        List<ImageIcon> list = getIcons();
        // icons are sorted on load so we can assume the last icon is the biggest
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    @Nullable
    public static Image getLargestImage() {
        return Optional.ofNullable(getLargestIcon()).map(ImageIcon::getImage).orElse(null);
    }

    @Nullable
    public static ImageIcon getSmallestIcon() {
        List<ImageIcon> list = getIcons();
        // icons are sorted on load so we can assume the first icon is the smallest
        return list.isEmpty() ? null : list.get(0);
    }

    @Nullable
    public static Image getSmallestImage() {
        return Optional.ofNullable(getSmallestIcon()).map(ImageIcon::getImage).orElse(null);
    }

    @Nullable
    public static ImageIcon getLargestIcon(int max) {
        List<ImageIcon> list = getIcons();
        if (!list.isEmpty()) {
            // icons are sorted on load so we can assume the last icon is the biggest
            int i = list.size();
            while (i-- > 0) {
                ImageIcon icon = list.get(i);
                if (icon.getIconHeight() <= max) {
                    return icon;
                }
            }
        }
        return null;
    }

    @Nullable
    public static Image getLargestImage(int max) {
        return Optional.ofNullable(getLargestIcon(max)).map(ImageIcon::getImage).orElse(null);
    }

    @Nullable
    public static ImageIcon getSmallestIcon(int min) {
        List<ImageIcon> list = getIcons();
        if (!list.isEmpty()) {
            // icons are sorted on load so we can assume the first icon is the smallest
            for (ImageIcon icon : list) {
                if (icon.getIconHeight() >= min) {
                    return icon;
                }
            }
        }
        return null;
    }

    @Nullable
    public static Image getSmallestImage(int min) {
        return Optional.ofNullable(getSmallestIcon(min)).map(ImageIcon::getImage).orElse(null);
    }

    public static void setAppIcon(Window window) {
        // Pass all the icon sizes to the system
        window.setIconImages(getImages());

        // OSX is a complete mess
        if (getOS() == OSX) {
            // OSX doesn't want to use the window icon so we have to set an "application" icon
            // The methods to do so only exist on OSX, so we must use reflection otherwise the
            // build will fail on non-OSX platforms.

            System.setProperty("apple.laf.useScreenMenuBar", "true");

            // Find the largest icon (but no bigger than 512)
            Image icon = getLargestImage(512);
            // Try the java 10 icon method first
            if (!reflect("java.awt.Taskbar", "getTaskbar", "setIconImage", icon)) {
                // If that fails, fall back to the java 6 method (deprecated in 11)
                reflect("com.apple.eawt.Application", "getApplication", "setDockIconImage", icon);
            }
        }
    }

    // Helper method just so we don't have to write the OSX reflection code twice
    private static boolean reflect(String className, String getInstance, String setIcon, Image icon) {
        try {
            Class clazz = Class.forName(className);
            Object instance = clazz.getMethod(getInstance).invoke(clazz);
            clazz.getMethod(setIcon, Image.class).invoke(instance, icon);
        } catch (Throwable ignored) {
            return false;
        }
        return true;
    }

    private static List<ImageIcon> loadIcons() {
        try {
            URI uri = AppIcon.class.getResource("/icons").toURI();
            Path dir;
            try {
                dir = Paths.get(uri);
            } catch (Throwable t) {
                // If it fails the first time, do a meme with filesystems then try again
                FileSystems.newFileSystem(uri, new HashMap<String, String>() {{
                    put("create", "true");
                }});
                dir = Paths.get(uri);
            }
            if (!Files.isDirectory(dir)) {
                throw new RuntimeException("icons/ should be a directory");
            }

            return StreamSupport.stream(Files.newDirectoryStream(dir).spliterator(), true)
                    .map(path -> {
                        try {
                            return new ImageIcon(Files.readAllBytes(path));
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(ImageIcon::getIconHeight))
                    .collect(Collectors.toList());
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
