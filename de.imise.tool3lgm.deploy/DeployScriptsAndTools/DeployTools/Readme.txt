Dieses Projekt bietet über eine jar-Datei 2 Funktionen:
A.) in einer Text-Datei eine Zeile mit einem bestimmten Präfix auszutauschen
B.) Dateien im Namen mit einer Version zu ergänzen, die vorher aus einer
    anderen Datei ausgelesen wurde.

Das Ganze ist nur notwendig, weil es unter Windows mit Batch-Befehlen keine
einfache Möglichkeit dazu gab und das so damals (2013) schneller ging. Eventuell
geht das jetzt auch mit Boardmitteln. 

Funktion A.) wird an 2 Stellen gebraucht:
1.) Die aktuelle Versionnummer wird in den Tool3LGMConstants vor dem Kompilieren
    ausgetauscht, so dass sie im Splash/Screen korrekt angezeigt wird.
2.) Dasselebe passiert in Innosetup-Script, so dass dort auch die korrekte Nummer
    steht.
	
Die Funktion B.) wird ebenfalls 2 Mal gebraucht:
1.) Umbenennen der ZIP-Datei, die als Deploy-Artefakt ensteht (Tool3lgm2.zip ->
	Tool3lgm2_V_4.0.1.zip)
2.) Dasselebe mit der tar.gz-Datei