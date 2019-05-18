package io.github.ImpactDevelopment.installer.libraries;

import io.github.ImpactDevelopment.installer.GPG;
import io.github.ImpactDevelopment.installer.github.Github;
import io.github.ImpactDevelopment.installer.github.GithubRelease;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LibraryBaritone implements ILibrary {
    public static final String VARIANT = "baritone-api";
    private final GithubRelease release;
    private final String strippedVersion;

    public static List<LibraryBaritone> getVersionsMatching(String versionFilter) {
        List<LibraryBaritone> releases = new ArrayList<>();
        for (GithubRelease release : Github.getReleases("cabaletta/baritone")) {
            if (!release.byName("checksums_signed.asc").isPresent()) {
                continue;
            }
            if (!release.tagName.startsWith("v")) {
                throw new IllegalArgumentException(release.tagName);
            }
            String strippedVersion = release.tagName.substring(1);
            if (versionFilter.equals(strippedVersion) || (versionFilter.endsWith("*") && strippedVersion.startsWith(versionFilter.replace("*", "")))) {
                releases.add(new LibraryBaritone(release));
            }
        }
        return releases;
    }

    private LibraryBaritone(GithubRelease release) {
        this.release = release;
        this.strippedVersion = release.tagName.substring(1);
    }

    public void verify() {
        System.out.println("Verifying GPG signatures on Baritone release " + strippedVersion);
        if (!GPG.verifyRelease(release, "checksums.txt", "checksums_signed.asc", sigs -> sigs.contains(GPG.brady) || sigs.contains(GPG.leijurv))) {
            throw new IllegalStateException("Invalid signature on Baritone release " + release.tagName);
        }
    }

    private String getReleasedJarName() {
        return VARIANT + "-" + strippedVersion + ".jar";
    }

    @Override
    public String getName() {
        return "cabaletta:" + VARIANT + ":" + strippedVersion;
    }

    @Override
    public String getSHA1() {
        try {
            String checksums = release.byName("checksums.txt").get().fetch();
            String ourLine = Stream.of(checksums.split("\n")).filter(line -> line.endsWith(getReleasedJarName())).findFirst().get();
            return ourLine.substring(0, 40);
        } catch (Exception e) {
            throw new RuntimeException(strippedVersion, e);
        }
    }

    @Override
    public int getSize() {
        return release.byName(getReleasedJarName()).get().size;
    }

    @Override
    public String getURL() {
        verify(); // disallow actually getting the URL unless the GPG signature is valid
        return release.byName(getReleasedJarName()).get().browserDownloadUrl;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getVersion() {
        return release.tagName;
    }

    public boolean equals(Object o) {
        return o instanceof LibraryBaritone && ((LibraryBaritone) o).release.equals(release);
    }
}
