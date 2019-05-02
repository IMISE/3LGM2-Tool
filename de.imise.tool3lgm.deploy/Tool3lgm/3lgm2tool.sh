#!/bin/sh
APPDIR=$(dirname $0)
cd $APPDIR
java -Xmx1024m -Xss64m -jar ./lib/tool3lgm.jar