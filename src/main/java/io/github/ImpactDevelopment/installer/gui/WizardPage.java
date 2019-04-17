package io.github.ImpactDevelopment.installer.gui;

import javax.swing.*;

// FIXME store property keys in an enum or something somewhere sensible
public abstract class WizardPage {

    private final Wizard wizard;
    private JPanel root;
    private boolean canGoForward = true;
    private boolean canGoBackward = true;

    public WizardPage(Wizard owner) {
        wizard = owner;
    }

    protected void setCanGoForward(boolean value) {
        canGoForward = value;
        wizard.updatePageIndex();
    }

    public boolean canGoForward() {
        return canGoForward;
    }

    protected void setCanGoBackward(boolean value) {
        canGoBackward = value;
        wizard.updatePageIndex();
    }

    public boolean canGoBackward() {
        return canGoBackward;
    }

    // Must be set, can't be in constructor signature since the subclass can't pass fields to super()
    protected void setRoot(JPanel rootPanel) {
        root = rootPanel;
    }

    JPanel getRootPanel() {
        if (root == null) {
            throw new RuntimeException(this.getClass().getSimpleName() + ".getRootPanel() - root is not initialised. You must call setRoot().");
        }
        return root;
    }
}
