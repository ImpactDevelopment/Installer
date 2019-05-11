package io.github.ImpactDevelopment.installer.impact;

import io.github.ImpactDevelopment.installer.github.GithubRelease;
import io.github.ImpactDevelopment.installer.libraries.ILibrary;
import io.github.ImpactDevelopment.installer.libraries.LibraryBaritone;
import io.github.ImpactDevelopment.installer.libraries.LibraryImpact;
import io.github.ImpactDevelopment.installer.libraries.LibraryMaven;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.BaritoneVersionSetting;
import io.github.ImpactDevelopment.installer.setting.settings.ImpactVersionSetting;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An Impact version that we have the JSON for
 */
public class ImpactJsonVersion {
    public String name; // will always be Impact :wink:
    public String version;
    public String mcVersion;
    public String date;
    public String[] tweakers;
    public ImpactJsonLibrary[] libraries;

    public void printInfo() {
        System.out.println(name);
        System.out.println(version);
        System.out.println(mcVersion);
        System.out.println(date);
        System.out.println(Arrays.toString(tweakers));
        for (ImpactJsonLibrary lib : libraries) {
            System.out.println(lib.name + " " + lib.sha1 + " " + lib.size);
        }
    }

    public List<ILibrary> resolveLibraries(InstallationConfig config) {
        return Stream.of(libraries).map(lib -> {
            String[] parts = lib.name.split(":");
            if (parts.length != 3) {
                throw new IllegalStateException("malformed " + lib.name);
            }
            String name = parts[1];
            if (name.equals(LibraryBaritone.VARIANT)) {
                return config.getSettingValue(BaritoneVersionSetting.INSTANCE);
            }
            if (name.equals(this.name)) {
                GithubRelease release = config.getSettingValue(ImpactVersionSetting.INSTANCE).release;
                if (!release.tagName.equals(version + "-" + mcVersion)) {
                    throw new RuntimeException(release.tagName + " " + version + " " + mcVersion);
                }
                return new LibraryImpact(release, lib);
            }
            return new LibraryMaven(lib);
        }).collect(Collectors.toList());
    }

    public Optional<String> baritoneVersionFilter() {
        return Stream
                .of(libraries)
                .map(lib -> lib.name)
                .map(name -> name.split(":"))
                .filter(parts -> parts[1].equals(LibraryBaritone.VARIANT))
                .map(parts -> parts[2])
                .findFirst();
    }
}
