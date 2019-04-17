package io.github.ImpactDevelopment.installer.versions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

public class Library {

    @Expose public final String name;
    @Expose @Nullable public final String url;

    public Library(String name) {
        this(name, null);
    }

    public Library(String name, @Nullable String url) {
        this.name = name;
        this.url = url;
    }

    public class MultiMCLibrary extends Library {

        @Expose public final String insert = "append";
        @Expose @SerializedName("MMC-depend") public final String MMCdepend = "hard";

        public MultiMCLibrary(String name) {
            super(name);
        }

        public MultiMCLibrary(String name, @Nullable String url) {
            super(name, url);
        }
    }
}
