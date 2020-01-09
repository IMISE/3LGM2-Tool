#ins Home-Verzeichnis wechseln. Das ist bei CygWin das Home-Verzeichnis, das
#denselben Namen wie der Windows-Benutzer hat
cd $HOME
#das Start-Script des Tools ausführbar machen
chmod u+x Tool3lgm/3lgm2tool.sh
#das Tool inkl. dieses Scripts als tar.gz packen
tar cfvz Tool3lgm.tar.gz Tool3lgm/