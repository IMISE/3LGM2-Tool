#!/bin/sh

### Java stuff
## java default
#java=java
#JAVA_HOME=$JAVA_HOME
## OpenJDK 11
#java=/usr/lib/jvm/java-11-openjdk-amd64/bin/java
#JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
## Oracle Java 8
#java=/usr/lib/jvm/java-8-oracle/bin/java
#JAVA_HOME=/usr/lib/jvm/java-8-oracle
#
#export JAVA_HOME

# Max Memory for 32-Bit Systems
MEMORY32="-Xmx512m -Xss20m"
# Max Memory for 64-Bit Systems
MEMORY64="-Xmx1024m -Xss64m"
# Set Default Memory to 32-Bit
MEMORY=$MEMORY32

APPDIR=$(dirname $0)
cd $APPDIR

# detect 64bit and set memory variable accordingly
if [ `uname -m` = "x86_64" ]
	then 
		echo "64bit environment detected. -> Setting memory parameter to 64bit environment: MEMORY=\""$MEMORY64"\""
		MEMORY=$MEMORY64
	else
		echo "No 64bit environment detected. -> Setting memory parameter to 32bit environment."
fi

# Debug
if [ $# -gt 0 ]; then
	if [ $1 = "debug" ]; then
		echo MEMORY32=$MEMORY32
		echo MEMORY64=$MEMORY64
		echo MEMORY=$MEMORY
		echo APPDIR=$APPDIR
		echo pwd=`pwd`
		echo java=$java
		echo -n java Version=
		$java -version
		echo JAVA_HOME=$JAVA_HOME
		exit 0
	fi
fi

java -classpath ./lib/*:./Plugins/* -splash:splash.gif $MEMORY de.imise.tool3lgm.Tool3lgmMain
