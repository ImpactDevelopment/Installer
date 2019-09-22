/*
 * This file is part of Impact Installer.
 *
 * Copyright (C) 2019  ImpactDevelopment and contributors
 *
 * See the CONTRIBUTORS.md file for a list of copyright holders
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package io.github.ImpactDevelopment.installer.github;


import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.utils.Fetcher;

import java.util.HashMap;
import java.util.Map;

public class Github {

    private static Map<String, GithubRelease[]> CACHE = new HashMap<>();

    public static synchronized GithubRelease[] getReleases(String repo) {
        return CACHE.computeIfAbsent(repo, Github::fetchReleases);
    }

    private static GithubRelease[] fetchReleases(String repo) {
        System.out.println("Fetching releases from " + repo);
        if (repo.equals("ImpactDevelopment/ImpactReleases")) {
            try {
                return getFromURL("http://impactclient.net/releases.json");
            } catch (Throwable th) {
                System.out.println("Unable to fetch from epic site");
                th.printStackTrace();
            }
        }
        return getFromURL("https://api.github.com/repos/" + repo + "/releases?per_page=100");
    }

    private static GithubRelease[] getFromURL(String url) {
        GithubRelease[] response = Installer.gson.fromJson(Fetcher.fetch(url), GithubRelease[].class);
        if (response.length == 0) {
            throw new RuntimeException("Empty response");
        }
        return response;
    }
}
