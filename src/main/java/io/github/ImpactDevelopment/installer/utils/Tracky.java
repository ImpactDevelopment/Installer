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

package io.github.ImpactDevelopment.installer.utils;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.GoogleAnalyticsBuilder;
import com.brsanthu.googleanalytics.GoogleAnalyticsConfig;
import com.brsanthu.googleanalytics.discovery.AwtRequestParameterDiscoverer;
import com.brsanthu.googleanalytics.request.DefaultRequest;
import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;
import io.github.ImpactDevelopment.installer.setting.settings.MinecraftDirectorySetting;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Tracky {

    private static final String FILENAME_IN_JAR = "impact_cid.txt";
    private static final String FILENAME_IN_INSTALL = "cid.txt";
    private static final String TRACKY_ID = "UA-143397381-1";
    private static final String CID;
    public static GoogleAnalytics ANALYTICS;

    static {
        if (Installer.args.noAnalytics) {
            CID = null;
        } else {
            String cid = "";
            try {
                cid = IOUtils.toString(Tracky.class.getResourceAsStream("/" + FILENAME_IN_JAR), StandardCharsets.UTF_8);
            } catch (Throwable th) {
            }
            if (cid.trim().isEmpty()) { // rare, bordering on impossible
                try {
                    cid = new String(Files.readAllBytes(mcDir().resolve("Impact").resolve(FILENAME_IN_INSTALL)));
                    if (cid.isEmpty()) {
                        // ok this is big brain time
                        // they have previously disabled analytics!
                        Installer.args.noAnalytics = true; // respect this
                        cid = null;
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                    cid = UUID.randomUUID().toString();
                }
            }
            CID = cid;
        }
        ANALYTICS = analytics(false);
    }

    private static Path mcDir() {
        String mcPath = Installer.args.mcPath;
        if (mcPath != null && !mcPath.isEmpty()) {
            return Paths.get(mcPath);
        }
        return new InstallationConfig().getSettingValue(MinecraftDirectorySetting.INSTANCE);
    }

    public static void awtEnabled() {
        ANALYTICS = analytics(true);
    }

    private static GoogleAnalytics analytics(boolean awt) {
        if (Installer.args.noAnalytics) {
            return null;
        }
        if (CID == null) {
            throw new IllegalArgumentException();
        }
        GoogleAnalyticsBuilder build = GoogleAnalytics
                .builder()
                .withDefaultRequest(new DefaultRequest().clientId(CID))
                .withTrackingId(TRACKY_ID);
        if (awt) {
            build = build.withConfig(new GoogleAnalyticsConfig().setRequestParameterDiscoverer(new AwtRequestParameterDiscoverer()));
        }
        return build.build();
    }

    public static void event(String category, String action, String label) {
        if (Installer.args.noAnalytics) {
            System.out.println("NOT sending event due to analytics being disabled!");
            return;
        }
        System.out.println("Sending event category=" + category + " action=" + action + " label=" + label + " cid=" + CID);
        try {
            ANALYTICS.event().eventCategory(category).eventAction(action).eventLabel(label).sendAsync().get(100L, TimeUnit.MILLISECONDS);
        } catch (Throwable th) {}
    }

    public static void persist(Path mcDir) {
        try {
            if (!Files.exists(mcDir)) {
                System.out.println("Not persisting to nonexistent directory");
                return;
            }
            Path impactDir = mcDir.resolve("Impact");
            Files.createDirectories(impactDir);
            Path cid = impactDir.resolve(FILENAME_IN_INSTALL);
            if (Installer.args.noAnalytics) {
                // write empty file
                Files.write(cid, new byte[0]);
            } else {
                // only write if it doesn't already exist
                // in other words, don't turn off a no-analytics that was previously set!
                Files.write(cid, CID.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
            }
        } catch (Throwable th) {
        }
    }
}
