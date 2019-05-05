package io.github.ImpactDevelopment.installer;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GithubReleases {

    public static GithubRelease[] getReleases(String repo) throws IOException {
        return Installer.gson.fromJson(new InputStreamReader(new URL("https://api.github.com/repos/" + repo + "/releases").openStream()), GithubRelease[].class);
    }

    public class GithubRelease {
        public String tag_name;
        public ReleaseAsset[] assets;
    }

    public class ReleaseAsset {
        public String name;
        public String browser_download_url;

        public String fetch() throws IOException {
            return IOUtils.toString(new URL(browser_download_url).openStream(), StandardCharsets.UTF_8);
        }
    }
}
