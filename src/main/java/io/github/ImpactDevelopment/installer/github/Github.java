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

package io.github.ImpactDevelopment.installer.github;


import io.github.ImpactDevelopment.installer.utils.Fetcher;
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
