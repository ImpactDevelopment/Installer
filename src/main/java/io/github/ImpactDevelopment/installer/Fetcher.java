package io.github.ImpactDevelopment.installer;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Put all the URL fetching in one place so that it can be logged
 */
public class Fetcher {
    public static String fetch(String url) {
        return new String(fetchBytes(url), StandardCharsets.UTF_8);
    }

    public static byte[] fetchBytes(String url) {
        System.out.println("DOWNLOADING " + url);
        try {
            return IOUtils.toByteArray(new URL(url).openStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to fetch " + url, e);
        }
    }
}
