package io.github.ImpactDevelopment.installer.libraries;

/**
 * A library that can be added to the final launcher json
 */
public interface ILibrary {
    String getName(); // e.g. "com.github.ImpactDevelopment:Impact:4.6-1.13.2"

    String getSHA1(); // e.g. 6dc748bbc1cabac3dbbabd8abce0b0859162ca85

    int getSize(); // e.g. 5335535

    String getURL();
}
