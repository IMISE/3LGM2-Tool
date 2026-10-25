#!/usr/bin/env bash
set -euo pipefail

# Must run on the target macOS architecture. jpackage does not cross-compile.
# Usage: package-macos.sh <staging-dir> <output-dir> <version> <architecture>
STAGING=${1:?"staging directory is required"}
OUTPUT=${2:?"output directory is required"}
VERSION=${3:?"version is required"}
ARCH=${4:?"architecture is required"}
NAME="3LGM2-Tool_v${VERSION}_macos-${ARCH}"
APP_VERSION="${VERSION%%-*}"
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

mkdir -p "$WORK/input" "$OUTPUT"
cp "$STAGING/lib/tool3lgm.jar" "$WORK/input/tool3lgm.jar"
cp "$STAGING/splash.gif" "$WORK/input/splash.gif"
cp -R "$STAGING/lib" "$STAGING/Plugins" "$STAGING/Templates" "$STAGING/Examples" \
  "$STAGING/Symbols" "$STAGING/Dok_Auswertungen" "$STAGING/XSLT-Beispiele" \
  "$STAGING/thirdPartyLicense.html" "$STAGING/LICENSE-2.0.txt" "$WORK/input/"

# The legacy application expects its libraries and data below the application
# directory. $APPDIR is expanded by the jpackage launcher at runtime.
jpackage \
  --type app-image \
  --dest "$WORK/image" \
  --name "3LGM2-Tool" \
  --app-version "$APP_VERSION" \
  --vendor "University Leipzig - IMISE" \
  --input "$WORK/input" \
  --main-jar tool3lgm.jar \
  --main-class de.imise.tool3lgm.Tool3lgmMain \
  --java-options '-cp $APPDIR/lib/*:$APPDIR/Plugins/*' \
  --java-options '-splash:$APPDIR/splash.gif' \
  --java-options '-Xmx768m' \
  --java-options '-Xss32m'

APP="$WORK/image/3LGM2-Tool.app"
ditto -c -k --sequesterRsrc --keepParent "$APP" "$OUTPUT/${NAME}.app.zip"
jpackage --type pkg --dest "$OUTPUT" --name "$NAME" --app-image "$APP"
GENERATED_PKG=$(find "$OUTPUT" -maxdepth 1 -name '*.pkg' -type f -print -quit)
test -n "$GENERATED_PKG"
mv "$GENERATED_PKG" "$OUTPUT/${NAME}.pkg"
