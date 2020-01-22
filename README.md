# Impact Installer

Intended to simplify the process of installing [Impact] versions. Note that the installer is still very much unfinished, we would greatly appreciate bug reports and feature requests.

## Usage

Dowload the [latest Installer][releases], run the download with Java (version 1.8 or higher), select the version and options you want to install and click `Install`.

## Building

You can build the installer using the included gradle wrapper. Open a command prompt/terminal inside the installer directory and type `gradlew build` on Windows or `./gradlew build` on OSX or Linux.

The finished jar will end up under `build/libs/`. You want the one that looks like `Installer-1.0-all.jar`.

## Setting up a development environment

You can set the installer up as a Gradle project in Intellij and other IDEs such as Eclipse.

In Intellij, choose "Import project", "Gradle project", select "Use included gradle wrapper" and continue.

Once the IDE opens the project, be sure to refresh the project, type `Ctrl`+`Shift`+`A`, type "refresh" and select "Refresh all Gradle projects"*[]:

You probably want to be able to run the installer without having to build a jar and open it with Java. Intellij can run the project if you create a Run Configuration. Type `Ctrl`+`Shift`+`A`, type "run config" and select "Edit configurations". Click the `+` icon and choose "Application". For "Main class", choose "Installer" and for "Use classpath of module" choose "installer.main". You probably want to name your run config something like "Run" or "Installer".

You can now run or debug the installer with `Shift`+`F10` and `Shift`+`F9`.  

## I have an issue, idea, question, patch

Please see [Contributing]

[Impact]: https://impactdevelopment.github.io/
[releases]: https://github.com/ImpactDevelopment/Installer/releases
[Contributing]: /CONTRIBUTING.md
