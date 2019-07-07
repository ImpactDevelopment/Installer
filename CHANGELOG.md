# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Upcoming

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

[Unreleased]: https://github.com/ImpactDevelopment/Installer/compare/0.3.0...HEAD
[0.3.0]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.3.0
[0.2.0]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.2.0
[0.1.1]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.1.1
[0.1.0]: https://github.com/ImpactDevelopment/Installer/releases/tag/0.1.0