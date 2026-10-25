# README #

[![GitHub[+] Release](https://img.shields.io/github/v/release/IMISE/3LGM2-Tool?sort=date&display_name=tag&style=flat&logo=github&label=current)]() [![Docs](https://img.shields.io/badge/Docs-green.svg)](https://imise.github.io/3LGM2-Tool/)

This README would normally document whatever steps are necessary to get your application up and running.

call: java -Xmx512m -Xss20m -splash:splash.gif -jar lib/tool3lgm.jar %1 %2 %3 %4 %5 %6 %7 %8 %9

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Build and release packages ###

`Java build` runs for pull requests and pushes to `main` and `develop`. It builds the Maven
projects with Temurin 25 and validates a clean, platform-neutral application
payload.

`Package releases` runs when a tag named `vX.Y.Z` is pushed. It publishes a
GitHub Release with these artifacts:

* `3LGM2-Tool_vX.Y.Z.tar.gz` for Linux (a locally installed Java runtime is
  required).
* `3LGM2-Tool_vX.Y.Z_setup.exe` for Windows with an externally installed Java
  runtime.
* `3LGM2-Tool_vX.Y.Z_JRE_setup.exe` and `3LGM2-Tool_vX.Y.Z_JRE.zip` for
  Windows with an embedded Temurin 25 runtime.
* A `.pkg` installer and `.app.zip` bundle for each of `macos-x64` and
  `macos-arm64`, each with an embedded Temurin 25 runtime.

The same workflow can be started manually from GitHub Actions. It requires a
numeric version and creates the complete set of packages as workflow artifacts
without publishing a release. macOS packages are intentionally unsigned;
signing and notarization are handled separately in issue #694.

#### Notes on macOS packages ####

Because the macOS packages are unsigned, the macOS Gatekeeper warns that the
downloaded package could not be verified. To run the tool anyway, use one of
these options:

* Right-click the app and choose "Open" (confirm twice), or
* Open "System Settings" → "Privacy & Security" and click "Open Anyway" for
  the app, or
* Remove the quarantine attribute in a terminal:
  `xattr -dr com.apple.quarantine /path/to/3LGM2-Tool.app`

This applies to both the `.pkg` installer and the `.app.zip` bundle. If the
macOS "app cannot be opened because it is from an unidentified developer"
dialog appears for the built-in app, the same procedure applies.

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact
