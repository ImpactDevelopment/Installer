package io.github.ImpactDevelopment.installer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.ImpactDevelopment.installer.gui.AppIcon;
import io.github.ImpactDevelopment.installer.gui.Wizard;
import io.github.ImpactDevelopment.installer.impact.ImpactJsonVersion;
import io.github.ImpactDevelopment.installer.profiles.VanillaProfiles;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;
import io.github.ImpactDevelopment.installer.setting.settings.MinecraftVersionSetting;
import io.github.ImpactDevelopment.installer.setting.settings.OptiFineSetting;
import io.github.ImpactDevelopment.installer.versions.Vanilla;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static io.github.ImpactDevelopment.installer.OperatingSystem.*;

public class Installer {
    public static final String project = "Impact";
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static void main(String... args) throws Throwable {

        // OSX and Linux systems should set swing.defaultlaf
        // explicitly setting the look and feel may override that
        // So we only do it on windows where it probably isn't set
        // They can still override us with swing.crossplatformlaf
        if (getOS() == WINDOWS)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        InstallationConfig test = new InstallationConfig();
        test.setSettingValue(MinecraftVersionSetting.INSTANCE, "1.13.2");
        System.out.println(Arrays.asList(OptiFineSetting.INSTANCE.getPossibleValues(test)));
        //install(test);
        test.setSettingValue(MinecraftVersionSetting.INSTANCE, "1.12.2");
        System.out.println(Arrays.asList(OptiFineSetting.INSTANCE.getPossibleValues(test)));
        //install(test);


        Wizard wizard = new Wizard();
        wizard.setTitle(getTitle());
        if (getOS() == OSX) // window.setTitle() isn't good enough on OSX
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", getTitle());
        AppIcon.setAppIcon(wizard);
        wizard.setSize(690, 420);
        wizard.setResizable(false);
        wizard.setVisible(true);

        System.exit(0);
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

        profiles.addOrMutate(version.name + " " + version.mcVersion, vanilla.getId());
        System.out.println("Saving vanilla profiles");
        profiles.saveToDisk();
    }

    public static String getTitle() {
        return project + " Installer";
    }
}
