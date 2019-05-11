package io.github.ImpactDevelopment.installer.github;


import io.github.ImpactDevelopment.installer.Fetcher;
import io.github.ImpactDevelopment.installer.Installer;

import java.util.HashMap;
import java.util.Map;

public class Github {

    private static Map<String, GithubRelease[]> CACHE = new HashMap<>();

    public static synchronized GithubRelease[] getReleases(String repo) {
        return CACHE.computeIfAbsent(repo, Github::fetchReleases);
    }

    private static GithubRelease[] fetchReleases(String repo) {
        System.out.println("Fetching releases from " + repo);
        return Installer.gson.fromJson(Fetcher.fetch("https://api.github.com/repos/" + repo + "/releases"), GithubRelease[].class);
    }
}
