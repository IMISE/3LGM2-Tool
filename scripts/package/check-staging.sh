#!/usr/bin/env bash
set -euo pipefail

STAGING=${1:?"staging directory is required"}
test -f "$STAGING/lib/tool3lgm.jar"
test -f "$STAGING/3lgm2tool.sh"
test -d "$STAGING/Plugins"
test -d "$STAGING/Templates"
test ! -e "$STAGING/jre"
test ! -e "$STAGING/3lgm2tool.exe"
test ! -e "$STAGING/3lgm2tool_jre.exe"
