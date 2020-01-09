::mit der Variable %~dp0 kann man den Pfad der aktuell ausgeführten Batchdatei ermitteln.
::Dabei steht %0 für die Datei selbst, d steht für Laufwerk (drive) und p für den Dateipfad.
::########!!!######## Alle relativen Pfade gehen von diesem Pfad aus ########!!!########
::Nur für die Maven-Builds wird in das jeweilge Projektverzeichnis gewechselt und danach
::wieder zurück in dieses Verzeichnis.
SET "SCRIPT_LOCATION=%~dp0"
CD /D %SCRIPT_LOCATION%

::Pfad zu 7zip
SET "SEVENZIPZIP_PROGRAM_DIR=DeployScriptsAndTools\7zip"

SET "lgmVersion=3.4.0.1"

:: ZIP the deploy Files with 7zip
FOR /d %%X IN ("Tool3lgm") DO "%SEVENZIPZIP_PROGRAM_DIR%\7z.exe" a "%%X_V%lgmVersion%.zip" "%%X\"
