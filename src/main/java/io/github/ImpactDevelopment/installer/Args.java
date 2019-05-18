package io.github.ImpactDevelopment.installer;

import com.beust.jcommander.Parameter;
import io.github.ImpactDevelopment.installer.setting.InstallationConfig;

public class Args {

    @Parameter(names = { "--no-gpg", "--disable-gpg" }, description = "Disable checking the release signature for testing purposes")
    public boolean gpg = true;

    @Parameter(names = { "-i", "--impact-version" }, description = "The Impact version to default to")
    public String impactVersion;

    @Parameter(names = { "-m", "--minecraft-version" }, description = "The Minecraft version to default to")
    public String minecraftVersion;

    public Args() {
        // TODO populate somme default values from gh releases
        // This happens _before_ JCommander parses argv and overrides our defaults.
    }

    public void apply(InstallationConfig config) {
        // TODO Apply args defined here to the config
        // Alternatively we could try and replace the InstallationConfig system with this class
    }
}
