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
        System.out.println("DOWNLOADING " + url);
        try {
            return IOUtils.toString(new URL(url).openStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Fetching " + url, e);
        }
    }
}
