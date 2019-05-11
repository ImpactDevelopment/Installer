package io.github.ImpactDevelopment.installer.impact;

import javax.annotation.Nullable;

/**
 * A library, as read from the impact json
 */
public class ImpactJsonLibrary {
    public String name;
    @Nullable
    public String sha1;
    @Nullable
    public Integer size;
}
