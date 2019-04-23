package io.github.ImpactDevelopment.installer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ImpactDevelopment.installer.GithubReleases.GithubRelease;
import io.github.ImpactDevelopment.installer.gui.Wizard;
import io.github.ImpactDevelopment.installer.profiles.VanillaProfiles;
import io.github.ImpactDevelopment.installer.versions.Vanilla;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import static io.github.ImpactDevelopment.installer.OperatingSystem.WINDOWS;
import static io.github.ImpactDevelopment.installer.OperatingSystem.getOS;

public class Installer {
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    //Temp debugging data
    private static final String project = "Impact";

//    private static final List<Image> ICONS = Arrays.asList(
//            new ImageIcon("icon_16.png").getImage(),
//            new ImageIcon("icon_32.png").getImage(),
//            new ImageIcon("icon_64.png").getImage());

    public static void main(String... args) throws Throwable {
        GithubRelease[] releases = GithubReleases.getReleases("cabaletta/baritone");
        for (GithubRelease release : releases) {
            System.out.println(release.tag_name);
            if (!GPG.verifyRelease(release)) {
                throw new IllegalStateException();
            }
        }
        // OSX and Linux systems should set swing.defaultlaf
        // explicitly setting the look and feel may override that
        // So we only do it on windows where it probably isn't set
        // They can still override us with swing.crossplatformlaf
        if (getOS() == WINDOWS)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        Wizard wizard = new Wizard();
        wizard.setTitle("Impact Installer");
//        wizard.setIconImages(ICONS);
        wizard.setSize(600, 420);
        wizard.setResizable(false);
        wizard.setVisible(true);

        System.exit(0);
    }

    public static void install(String optifine) throws IOException {
        String id = getId();
        System.out.println("Installing impact " + id);


        System.out.println("Creating vanilla version");
        Vanilla vanilla = new Vanilla(id, new String(Files.readAllBytes(Paths.get(id, id + ".json"))));
        vanilla.saveToDisk();

        System.out.println("Loading existing vanilla profiles");
        VanillaProfiles profiles = new VanillaProfiles();
        System.out.println("Injecting impact version...");
        profiles.addOrMutate(vanilla.getId().split("-")[1].replace("_", " "), vanilla.getId());
        System.out.println("Saving vanilla profiles");
        profiles.saveToDisk();
    }

    public static String getId() {
        for (File file : new File(".").listFiles()) {
            if (file.getName().toLowerCase().contains(project.toLowerCase())) {
                return file.getName();
            }
        }
        throw new IllegalStateException();
    }
}
