package io.github.ImpactDevelopment.installer.impact;

import io.github.ImpactDevelopment.installer.github.Github;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImpactVersions {
    private static final List<ImpactVersion> VERSIONS = Stream.of(Github.getReleases("ImpactDevelopment/ImpactReleases"))
            .map(ImpactVersion::new)
            .filter(ImpactVersion::possiblySigned)
            .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

    public static List<ImpactVersion> getAllVersions() {
        return VERSIONS;
    }
}
