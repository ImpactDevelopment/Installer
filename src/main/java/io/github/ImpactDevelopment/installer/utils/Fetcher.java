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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
            return IOUtils.toByteArray(new URI(url));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Unable to fetch " + url, e);
        }
    }
}
