package io.github.ImpactDevelopment.installer.gui.pages;

import io.github.ImpactDevelopment.installer.Markdown;
import io.github.ImpactDevelopment.installer.gui.Wizard;
import io.github.ImpactDevelopment.installer.gui.WizardPage;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.io.StringReader;

public class WelcomePage extends WizardPage {

    private JPanel root;
    private JTextPane textPane;

    public WelcomePage(Wizard owner) throws IOException, BadLocationException {
        super(owner);
        setRoot(root);

        // Load and parse welcome.md
        String html = Markdown.parse(ClassLoader.getSystemResourceAsStream("welcome.md"));
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
        htmlKit.read(new StringReader(html), htmlDoc, 0);

        textPane.setDocument(htmlDoc);

        // Make the background transparent
        // The form builder only supports setting the rgb, not alpha
        textPane.setBackground(new Color(0, 0, 0, 0));
    }
}
