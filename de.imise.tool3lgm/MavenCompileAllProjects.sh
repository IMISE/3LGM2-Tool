#!/bin/bash

#Test outputs
#echo $SHELL
#echo $PATH
#mvn -version
#exit 0

APPDIR=$(dirname $0)
cd $APPDIR

echo '#############################################'
echo '###   A X S U T I L S  -  P R O J E K T   ###'
echo '#############################################'
cd ../axsutils/
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
