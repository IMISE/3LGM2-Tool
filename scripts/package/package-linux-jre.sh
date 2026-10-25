#!/usr/bin/env bash
set -euo pipefail

# Usage: package-linux-jre.sh <staging-dir> <output-dir> <version>
STAGING=${1:?"staging directory is required"}
OUTPUT=${2:?"output directory is required"}
VERSION=${3:?"version is required"}
NAME="3LGM2-Tool_v${VERSION}_JRE"
APP_VERSION="${VERSION%%-*}"
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT

mkdir -p "$WORK/input" "$OUTPUT"
cp "$STAGING/lib/tool3lgm.jar" "$WORK/input/tool3lgm.jar"
cp "$STAGING/splash.gif" "$WORK/input/splash.gif"
cp "$STAGING/version.info" "$WORK/input/version.info"
cp -R "$STAGING/lib" "$STAGING/Plugins" "$STAGING/Templates" "$STAGING/Examples" \
  "$STAGING/Symbols" "$STAGING/Dok_Auswertungen" "$STAGING/XSLT-Beispiele" \
  "$STAGING/thirdPartyLicense.html" "$STAGING/LICENSE-2.0.txt" "$WORK/input/"

# The launcher bundles a private runtime, so no system Java is required.
# $APPDIR is expanded by the launcher to the directory holding the staged
# content.
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

# The application resolves its data directories relative to the working
# directory, so the wrapper enters the staged content below lib/app before
# launching.
cat > "$WORK/image/3LGM2-Tool/3lgm2tool.sh" <<'EOF'
#!/usr/bin/env bash
DIR=$(dirname "$(readlink -f "$0")")
cd "$DIR/lib/app" || exit 1
exec "$DIR/bin/3LGM2-Tool"
EOF
chmod +x "$WORK/image/3LGM2-Tool/3lgm2tool.sh"

tar -C "$WORK/image" -czf "$OUTPUT/${NAME}.tar.gz" "3LGM2-Tool"
