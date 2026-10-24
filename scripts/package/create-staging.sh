#!/usr/bin/env bash
set -euo pipefail

# Create a clean, platform-neutral application payload from Maven's deploy output.
# Usage: create-staging.sh <destination> <version>
DESTINATION=${1:?"destination is required"}
VERSION=${2:?"version is required"}
SOURCE=de.imise.tool3lgm.deploy/Tool3lgm

rm -rf "$DESTINATION"
mkdir -p "$DESTINATION"
cp -a "$SOURCE/." "$DESTINATION/"

# These files are old, checked-in build products. Every platform job creates its
# own launcher/runtime and must never accidentally publish them.
rm -rf "$DESTINATION/jre"
rm -f "$DESTINATION/3lgm2tool.exe" "$DESTINATION/3lgm2tool_jre.exe"

printf '%s\n' "$VERSION" > "$DESTINATION/RELEASE_VERSION"
chmod +x "$DESTINATION/3lgm2tool.sh"
