package io.github.ImpactDevelopment.installer;

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
