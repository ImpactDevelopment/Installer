package io.github.ImpactDevelopment.installer.github;

import com.google.gson.annotations.SerializedName;
import io.github.ImpactDevelopment.installer.Fetcher;

public class ReleaseAsset {

    public String name;

    public int size;

    @SerializedName("browser_download_url")
    public String browserDownloadUrl;

    private String fetchedData;

    public synchronized String fetch() {
        if (fetchedData == null) {
            fetchedData = Fetcher.fetch(browserDownloadUrl);
        }
        return fetchedData;
    }
}
