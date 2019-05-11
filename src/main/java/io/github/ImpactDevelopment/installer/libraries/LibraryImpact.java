package io.github.ImpactDevelopment.installer.libraries;

import io.github.ImpactDevelopment.installer.github.GithubRelease;
import io.github.ImpactDevelopment.installer.impact.ImpactJsonLibrary;

public class LibraryImpact extends LibraryMaven {
    private final GithubRelease release;

    public LibraryImpact(GithubRelease release, ImpactJsonLibrary lib) {
        super(lib);
        String mavenNameExpectedFromRelease = "com.github.ImpactDevelopment:Impact:" + release.tagName;
        if (!mavenNameExpectedFromRelease.equals(getName())) {
            throw new IllegalStateException("Malformed Impact release / json " + mavenNameExpectedFromRelease + " " + getName());
        }
        this.release = release;
    }

    @Override
    public String getURL() {
        // e.g. Impact-4.6-1.13.2.jar
        return release.byName("Impact-" + release.tagName + ".jar").get().browserDownloadUrl;
    }
}
