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

package io.github.ImpactDevelopment.installer.utils;

import org.apache.commons.io.IOUtils;

import javax.net.ssl.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

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
            return IOUtils.toByteArray(new URI(url));
        } catch (Throwable th) {
            th.printStackTrace();
        }
        SSLSocketFactory originalSSL = HttpsURLConnection.getDefaultSSLSocketFactory(); // for restoring later
        HostnameVerifier originalHost = HttpsURLConnection.getDefaultHostnameVerifier();
        String originalIPv4 = System.getProperty("java.net.preferIPv4Stack");
        try {
            System.out.println("Trying some hacks to get this to load!");
            System.setProperty("java.net.preferIPv4Stack", "true");
            try {
                return IOUtils.toByteArray(new URI(url));
            } catch (Throwable th) {
                th.printStackTrace();
            }
            disableHTTPSPart1();
            try {
                return IOUtils.toByteArray(new URI(url));
            } catch (Throwable th) {
                th.printStackTrace();
            }
            disableHTTPSPart2();
            try {
                return IOUtils.toByteArray(new URI(url));
            } catch (Throwable th) {
                th.printStackTrace();
                throw new RuntimeException("Unable to fetch " + url, th);
            }
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Undoing hacks!");
            System.setProperty("java.net.preferIPv4Stack", originalIPv4);
            try {
                HttpsURLConnection.setDefaultSSLSocketFactory(originalSSL); // restore to full https verification
                HttpsURLConnection.setDefaultHostnameVerifier(originalHost);
            } catch (Throwable th) {
                System.out.println("Unable to restore https! This could actually be a problem!");
            }
        }
    }

    public static void disableHTTPSPart1() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    public static void disableHTTPSPart2() {
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}
