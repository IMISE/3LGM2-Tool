#!/usr/bin/env bash
set -euo pipefail

# Usage: package-linux.sh <staging-dir> <output-dir> <version>
STAGING=${1:?"staging directory is required"}
OUTPUT=${2:?"output directory is required"}
VERSION=${3:?"version is required"}
NAME="3LGM2-Tool_v${VERSION}"

mkdir -p "$OUTPUT"
PARENT=$(mktemp -d)
trap 'rm -rf "$PARENT"' EXIT
cp -a "$STAGING" "$PARENT/$NAME"
chmod +x "$PARENT/$NAME/3lgm2tool.sh"
tar -C "$PARENT" -czf "$OUTPUT/${NAME}.tar.gz" "$NAME"
