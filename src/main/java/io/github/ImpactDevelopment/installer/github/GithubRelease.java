package io.github.ImpactDevelopment.installer.github;

import com.google.gson.annotations.SerializedName;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GithubRelease {
    @SerializedName("tag_name")
    public String tagName;
    public ReleaseAsset[] assets;

    public Optional<ReleaseAsset> byName(String fileName) {
        return byName(fileName::equals);
    }

    public Optional<ReleaseAsset> byName(Predicate<String> filter) {
        return Stream.of(assets).filter(asset -> filter.test(asset.name)).findFirst();
    }
}
