package io.github.ImpactDevelopment.installer.gui;

import io.github.ImpactDevelopment.installer.gui.pages.OptionsPage;
import io.github.ImpactDevelopment.installer.gui.pages.WelcomePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

public class Wizard extends JDialog {
    private final List<WizardPage> pages;

    private JPanel wizardPanel;
    private JButton backButton;
    private JButton cancelButton;
    private JButton nextButton;
    private JPanel cards;
    private int currentPage;

    public Wizard() throws Throwable {
        super((Dialog) null, true);//Trick JDialog into showing in the taskbar

        currentPage = 0;
        pages = Arrays.asList(
                new WelcomePage(this),
                new OptionsPage(this)
        );
        pages.forEach(comp -> cards.add(comp.getRootPanel(), comp.getClass().getSimpleName()));

        setContentPane(wizardPanel);
        getRootPane().setDefaultButton(nextButton);

        cancelButton.addActionListener(this::onCancel);
        backButton.addActionListener(event -> updatePageIndex(-1));
        nextButton.addActionListener(event -> updatePageIndex(1));

        // call onCancel() when window closes
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel(e);
            }
        });

        // call onCancel() on ESCAPE
        wizardPanel.registerKeyboardAction(this::onCancel, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        updatePageIndex();
        pack();
        setLocationRelativeTo(null);// center on screen
    }

    private void onCancel(AWTEvent event) {
        // add your code here if necessary
        // TODO add confirmation dialogue
        dispose();
    }

    public void updatePageIndex() {
        updatePageIndex(0);
    }

    private void updatePageIndex(int increment) {
        // Clamp between 0 and size()-1
        currentPage = Math.max(Math.min(currentPage + increment, pages.size() - 1), 0);

        // Show the current page in the CardLayout JPane
        CardLayout cardLayout = (CardLayout) cards.getLayout();
        cardLayout.show(cards, getCurrentPage().getClass().getSimpleName());

        // When on the final page, rename cancel to finish
        // TODO: consider having a separate cancel and finish button? Maybe there are times it makes sense to finish early?
        cancelButton.setText(
                currentPage < pages.size() - 1
                        ? "Cancel"
                        : "Finish");

        // Update enabled state of buttons
        backButton.setEnabled(!isFirstPage() && getCurrentPage().canGoBackward());
        nextButton.setEnabled(!isLastPage() && getCurrentPage().canGoForward());
    }

    private boolean isFirstPage() {
        return currentPage == 0;
    }

    private boolean isLastPage() {
        return currentPage == pages.size() - 1;
    }

    public WizardPage getCurrentPage() {
        return pages.get(currentPage);
    }

}
