Launch4J ist dazu da eine ausführbare jar-Datei mit einer Exe zu wrappen.
Das geht beim 3LGM2-Tool aber im Moment nicht, da man alle Bibliotheken, die
das Tool braucht in die Manifest-Datei dieser Jar schreiben muss - auch
die Plugins!
Das kann man erst machen, wenn das Laden der Plugins auf den ServiceLoader von
Java umgestellt ist, weil man mit dem aktuellen Plugin-Mechanismus nicht mehr
an zur Startzeit unbekannte Jars kommt. D.h. man kommt ran, aber die Klassen
aus diesen Jars werden mit einem andere ClassLoader geladen und damit kann man
nicht mehr auf Zuwesiungskompatibilität testen.
Denn dieselbe Klasse geladen mit 2 unterscheidlichen ClassLoadern ist niemals
zuweisungskompatibel. Daher können die geladenen jars nicht nach Unterklassen
der Plugin-Klassen durchsucht werden.

Das ganze funktioniert aber wieder, wenn man einfach die Batch-Datei als Exe
wrappt, da man im normalen java-Befehl anders als in der Manifest-Datei auch
Pfade zu Ordnern mit jars angeben kann, ohne die jars explizit zu nennen. Diese
werden dann auch mit demselben Classloader wie der Rest des Programms geladen und
sind somit auf Zuseisungskompatibilität vergleichbar. 