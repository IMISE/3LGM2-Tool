#!/bin/sh

### [Debug] Java stuff
### [ToDo] implement a better way to detect/select JRE
## OpenJDK 11
#JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
## Oracle Java 8
#JAVA_HOME=/usr/lib/jvm/java-8-oracle
#JAVA_HOME=/opt/java/jdk1.8.0_251
#export JAVA_HOME
## java executable (default)
java=java
## java executable (specific to JAVA_HOME)
#java=$JAVA_HOME/bin/java
## print JAVA_HOME and java version
#echo JAVA_HOME=$JAVA_HOME
#$java -version

# Max Memory for 32-Bit Systems
MEMORY32="-Xmx512m -Xss20m"
# Max Memory for 64-Bit Systems
MEMORY64="-Xmx768m -Xss32m"
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

$java -classpath ./lib/*:./Plugins/* -splash:splash.gif $MEMORY de.imise.tool3lgm.Tool3lgmMain
