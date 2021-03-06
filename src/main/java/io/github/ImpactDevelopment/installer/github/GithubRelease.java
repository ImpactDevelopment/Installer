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

import com.google.gson.annotations.SerializedName;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GithubRelease {
    @SerializedName("tag_name")
    public String tagName;
    public boolean draft;
    public boolean prerelease;
    public ReleaseAsset[] assets;

    public Optional<ReleaseAsset> byName(String fileName) {
        return byName(fileName::equals);
    }

    public Optional<ReleaseAsset> byName(Predicate<String> filter) {
        return Stream.of(assets).filter(asset -> filter.test(asset.name)).findFirst();
    }
}
