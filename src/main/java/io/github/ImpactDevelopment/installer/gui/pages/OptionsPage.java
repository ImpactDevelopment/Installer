package io.github.ImpactDevelopment.installer.gui.pages;

import io.github.ImpactDevelopment.installer.Installer;
import io.github.ImpactDevelopment.installer.gui.Wizard;
import io.github.ImpactDevelopment.installer.gui.WizardPage;

import javax.swing.*;

import static io.github.ImpactDevelopment.installer.OperatingSystem.getMinecraftDirectory;
import static io.github.ImpactDevelopment.installer.OptiFine.getInstalledOptiFineVersion;

public class OptionsPage extends WizardPage {

    private JPanel root;
    private JLabel mcDir;
    private JCheckBox optifineCheckBox;
    private JLabel noOptiFineLabel;
    private JButton install;

    public OptionsPage(Wizard owner) {
        super(owner);
        setRoot(root);

        String minecraftVersion = Installer.getId().split("-")[0];
        String optifineVersion = getInstalledOptiFineVersion(minecraftVersion).orElse("");

        mcDir.setText(getMinecraftDirectory().toString());
        if (optifineVersion.isEmpty()) {
            optifineCheckBox.setVisible(false);
            noOptiFineLabel.setVisible(true);
            noOptiFineLabel.setText("No OptiFine installed for " + minecraftVersion);
        } else {
            optifineCheckBox.setVisible(true);
            noOptiFineLabel.setVisible(false);
            noOptiFineLabel.setText("OptiFine " + optifineVersion);
        }
        install.addActionListener(event -> { //TODO move install logic into it's own page or controller or something
            try {
                if (!optifineVersion.isEmpty() && optifineCheckBox.isSelected()) {
                    Installer.install(optifineVersion);
                } else {
                    Installer.install("");
                }
                JOptionPane.showMessageDialog(owner, "Impact has been successfully installed to\n" + getMinecraftDirectory());
            } catch (Throwable e) {
                JOptionPane.showMessageDialog(owner, "Error " + e);
            }
        });
    }
}
