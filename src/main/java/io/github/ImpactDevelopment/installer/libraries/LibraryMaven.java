package io.github.ImpactDevelopment.installer.libraries;

import io.github.ImpactDevelopment.installer.impact.ImpactJsonLibrary;

public class LibraryMaven implements ILibrary {

    private final String name;
    private final String sha1;
    private final int size;

    public LibraryMaven(String name, String sha1, int size) {
        this.name = name;
        this.sha1 = sha1;
        this.size = size;
    }

    public LibraryMaven(ImpactJsonLibrary lib) {
        this(lib.name, lib.sha1, lib.size);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSHA1() {
        return sha1;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getURL() {
        return MavenResolver.getFullURL(name);
    }
}
