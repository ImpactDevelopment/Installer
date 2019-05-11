package io.github.ImpactDevelopment.installer.gui.pages;

import io.github.ImpactDevelopment.installer.gui.Wizard;
import io.github.ImpactDevelopment.installer.gui.WizardPage;

import javax.swing.*;
import java.awt.*;


public class OptionsPage extends WizardPage {

    private JPanel root;
    private JLabel mcDir;
    private JCheckBox optifineCheckBox;
    private JLabel noOptiFineLabel;
    private JButton install;

    public OptionsPage(Wizard owner) {
        super(owner);
        setRoot(root);

        //String optifineVersion = getInstalledOptiFineVersion("1.12.2").orElse("");

        mcDir.setText("hewwo");
        if (true) {
            optifineCheckBox.setVisible(false);
            noOptiFineLabel.setVisible(true);
            noOptiFineLabel.setText("No OptiFine installed for " + "1.12.2");
        } else {
            optifineCheckBox.setVisible(true);
            noOptiFineLabel.setVisible(false);
            noOptiFineLabel.setText("OptiFine ");
        }
        install.addActionListener(event -> { //TODO move install logic into it's own page or controller or something
            try {
                /*if (!optifineVersion.isEmpty() && optifineCheckBox.isSelected()) {
                    Installer.install(optifineVersion);
                } else {
                    Installer.install("");
                }*/
                //Installer.install(ImpactJsonVersion.fetchImpactVersionsForMCVersion("1.13.2")[0]);
                //JOptionPane.showMessageDialog(owner, "Impact has been successfully installed to\n" + getMinecraftDirectory());
            } catch (Throwable e) {
                JOptionPane.showMessageDialog(owner, "Error " + e);
            }
        });
    }

}
