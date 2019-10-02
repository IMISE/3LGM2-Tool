#!/bin/sh
APPDIR=$(dirname $0)
cd $APPDIR
#export PATH=$PATH:./lib/
#export PATH=$PATH:./Plugins/
#echo $PATH
#java -Xmx1024m -Xss64m -jar ./lib/tool3lgm.jar

java --class-path ./lib/*:./Plugins/* -splash:splash.gif -Xmx1024m -Xss64m de.imise.tool3lgm.Tool3lgmMain
#java --class-path ./lib/*:./Plugins/* -Xmx1024m -Xss64m -jar ./lib/tool3lgm.jar
#java -Xmx1024m -Xss64m -jar ./lib/tool3lgm.jar
