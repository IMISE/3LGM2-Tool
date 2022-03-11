#!/bin/bash

SELF=$(realpath $0)
APPDIR=$(dirname $SELF)

echo '#############################################'
echo '###   B U I L D  -  U M G E B U N G       ###'
echo '#############################################'
#Test outputs
echo [INFO] SHELL=$SHELL
echo [INFO] PATH=$PATH
echo [INFO] SELF=$SELF
echo [INFO] APPDIR=$APPDIR
echo -n "[INFO] "
java -version
echo -n "[INFO] "
mvn -version

cd $APPDIR

echo
echo '#############################################'
echo '###   A X S U T I L S  -  P R O J E K T   ###'
echo '#############################################'
cd ../axsutils/
wget -N http://svn.apache.org/repos/asf/servicemix/m2-repo/com/bea/xml/jsr173-ri/1.0/jsr173-ri-1.0.jar
mvn install:install-file -Dfile=jsr173-ri-1.0.jar -DpomFile=jsr173.pom
mvn -B -Dmaven.test.skip=true clean install

echo
echo '#############################################'
echo '###   T O O L 3 L G M  -  P R O J E K T   ###'
echo '#############################################'
cd $APPDIR
mvn -B -Dmaven.test.skip=true clean install

echo
echo '##################################################################'
echo '###   M E T A M O D E L    O R I G I N A L  -  P R O J E K T   ###'
echo '##################################################################'
cd ../de.imise.metamodel.original/
mvn -B -Dmaven.test.skip=true clean install

echo
echo '################################################################'
echo '###   M E T A M O D E L    S E R V I C E  -  P R O J E K T   ###'
echo '################################################################'
cd ../de.imise.metamodel.service/
mvn -B -Dmaven.test.skip=true clean install

echo
echo '#######################################################'
echo '###   T E M P L A T E    I H E   -  P R O J E K T   ###'
echo '#######################################################'
cd ../de.imise.tool3lgm.template.ihe/
mvn -B -Dmaven.test.skip=true clean install

cd $APPDIR

# Versionsinfo ausgeben
cat ../de.imise.tool3lgm.deploy/Tool3lgm/version.info
