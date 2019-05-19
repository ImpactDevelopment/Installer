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

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class Markdown {

    private static final Parser MD_PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();

    public static String parse(String markdown) throws IOException {
        return parse(new StringReader(markdown));
    }

    public static String parse(InputStream markdown) throws IOException {
        return parse(new InputStreamReader(markdown));
    }

    public static String parse(Path markdown) throws IOException {
        return parse(Files.newBufferedReader(markdown, Charset.forName("UTF-8")));
    }

    public static String parse(Reader markdown) throws IOException {
        return HTML_RENDERER.render(MD_PARSER.parseReader(markdown));
    }
}
