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
