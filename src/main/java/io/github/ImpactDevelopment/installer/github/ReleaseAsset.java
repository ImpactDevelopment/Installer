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
