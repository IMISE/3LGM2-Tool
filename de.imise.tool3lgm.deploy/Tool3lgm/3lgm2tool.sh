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

SCALE=""

APPDIR=$(dirname $0)
cd $APPDIR

# detect 64bit and set memory variable accordingly
if [ `uname -m` = "x86_64" -o `uname -m` = "arm64" ]
	then 
		echo "64bit environment detected. -> Setting memory parameter to 64bit environment: MEMORY=\""$MEMORY64"\""
		MEMORY=$MEMORY64
	else
		echo "No 64bit environment detected. -> Setting memory parameter to 32bit environment."
fi

# Debug
# $@ contains all parameters passed to this script
if [ $# -gt 0 ]; then
for param in "$@";
	do
		if [ $param = "--debug" -o $param = "-debug" ]; then
			echo Parameters=$@
			echo MEMORY32=$MEMORY32
			echo MEMORY64=$MEMORY64
			echo MEMORY=$MEMORY
			echo APPDIR=$APPDIR
			echo pwd=`pwd`
			echo java=$java
			echo -n java Version=
			$java -version
			echo JAVA_HOME=$JAVA_HOME
		fi
		if [ $param = "--scale=2" -o $param = "-scale=2" ]; then
			SCALE=-Dsun.java2d.uiScale='2'
			# does not work
			# SCALE=-Dsun.java2d.win.uiScaleX=120dpi -Dsun.java2d.win.uiScaleY=120dpi
			echo SCALE=$SCALE
		fi
		if [ $param = "--help" -o $param = "-help" ]; then
			echo Call: ./3lgm2tool.sh [Parameters]
			echo ""
			echo Parameters handled by this script:
			echo "-help, --help			show this List and exit"
			echo "-scale=2, --scale=2		scales the 3LGM² tool by factor of 2, which is useful for high resolution monitors, e.g. 32 zoll 4k monitors"
			echo ""
			echo Parameters handled by 3glm java process:
			echo "-n				allow multiple instances of 3LGM² tool (default: only one instance is allowed)"
			echo "-i				start 3LGM² tool invisible"
			echo "-debug, --debug			shows debugging information, e.g. environment variables or java version"
			echo "-log_rdf, -log_all		activates logging (all)"
			echo "-log_rdf_err			activates logging (only errors and warnings of RDF import process)"
			echo ""
			echo Please report bugs and feature request via https://bitbucket.org/imise/tool-3lgm2/issues/new
			exit 0
		fi
	done
fi

$java $SCALE -classpath ./lib/*:./Plugins/* -splash:splash.gif $MEMORY de.imise.tool3lgm.Tool3lgmMain $*
