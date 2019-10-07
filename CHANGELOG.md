# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.7.2] - 2019-10-06

### Changed
- Changed event processing relating to successful installs vs errors

## [0.7.1] - 2019-09-27

### Fixed
- Fixed a windows only crash caused by some weird manipulation of paths that ended up containing a colon

## [0.7.0] - 2019-09-27

### Added
- `--help` argument to print command line usage
- `--version` argument to print the installer version
- Analytics, and an option `--no-analytics` to disable them

### Fixed
- Fixed launcher profile name with new nightly format (`nighty-DATE-1.12.2` now becomes `Impact nightly for 1.12.2` instead of `Impact nightly-DATE for 1.12.2`), which matches how it used to behave in the old format (which became `Impact 4.8-nightly for 1.12.2`). This is so that each day's new nightly doesn't spam your launcher with a new profile each time.
- Fixed Forge installs going into `.minecraft/mods` instead of `.minecraft/mods/[version]` ([#71](https://github.com/ImpactDevelopment/Installer/issues/71))

## [0.6.0] - 2019-08-17

### Fixed
- Don't completely overwrite Impact's `META-INF/MANIFEST.MF` when building a Forge jar

## [0.5.6] - 2019-08-17

### Fixed
- Fixed installing unreleased Minecraft versions from a JSON file
- Fixed OptiFine compatibility for 1.14.4

## [0.5.5] - 2019-07-29

### Fixed
- Fixed pagination on GitHub releases API response

### Changed
- Changed the primary GitHub releases URL to [here](http://impactclient.net/releases.json) (http is safe because gpg). Cloudflare proxy is 4 to 7 times faster (because cached) than GitHub's actual API. Fallback to GitHub's API (as previously) if this fails for any reason.

### Removed
- Removed the option to select a baritone version, as no version of Impact uses it anymore

## [0.5.4] - 2019-07-23

### Fixed
- Fixed failure to launch when self-installed in a Forge jar

## [0.5.3] - 2019-07-16

### Fixed
- Fixed parsing of version with multiple dashes

## [0.5.2] - 2019-07-16

### Changed
- The version profile added to the launcher no longer has the patch included in the name

## [0.5.1] - 2019-07-14

### Changed
- The EXE now uses Mojang's JRE if installed (on 64bit Windows) before falling back to "normally" installed JREs and JDKs.

## [0.5.0] - 2019-07-13

### Added
- Added command line options to specify OptiFine and minecraft directory
- Added mode "Show JSON" that, uh, shows you the version json
- Added warning to Forge when a previous version of Impact is detected in mods

## [0.4.0] - 2019-07-10

### Added
- Added a command line interface
- Added the ability to install a local .json
- Added a way to validate a release and all its dependencies without installing it

## [0.3.1] - 2019-07-10

### Fixed
- Fixed the installer not setting a required property in the launcher profile that made it impossible to delete

## [0.3.0] whats this - 2019-07-06

### Changed
- Make OptiFine option default to None due to visual glitches
- Have the message on successfully installing Forge tell you where the jar was placed
- Improve error message displayed when the wrong directory is selected for Minecraft
- Filter out releases marked as a draft or prerelease on GitHub
- Slightly different wording in the error when vanilla has not yet been downloaded
- Include OptiFine version in name and id of resulting version json
- Warn that OptiFine can cause glitches in Impact

## [0.2.0] - 2019-05-31

### Added
- An EXE version of the installer to simplify running on Windows
- Link to OptiFine download page
- An error message when vanilla has not yet been launched for the selected version

### Changed
- Make text field in the directory choosing widget always 15 characters wide
- Significantly reduce the size of the jar

## [0.1.1] - 2019-05-23

### Fixed
- The installer not creating a profile in the vanilla installer

## [0.1.0] - 2019-05-19

### Added
- A GUI to simplify installation of Impact
- The option to install for forge or vanilla
- The ability to select different Baritone versions to be used
- The ability to select different OptiFine versions to be used
- GPG signature checking of Impact and Baritone release artifacts
- Initial documentation including a README and this CHANGELOG

[Unreleased]: https://github.com/ImpactDevelopment/Installer/compare/0.7.2...HEAD
[0.7.2]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.7.2
[0.7.1]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.7.1
[0.7.0]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.7.0
[0.6.0]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.6.0
[0.5.5]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.5.5
[0.5.4]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.5.4
[0.5.3]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.5.3
[0.5.2]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.5.2
[0.5.1]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.5.1
[0.5.0]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.5.0
[0.4.0]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.4.0
[0.3.1]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.3.1
[0.3.0]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.3.0
[0.2.0]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.2.0
[0.1.1]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.1.1
[0.1.0]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.1.0