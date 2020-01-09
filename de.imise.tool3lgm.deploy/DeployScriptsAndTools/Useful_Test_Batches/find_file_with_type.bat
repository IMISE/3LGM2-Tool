@ECHO ON &SETLOCAL

::mit der Variable %~dp0 kann man den Pfad der aktuell ausgeführten Batchdatei ermitteln.
::Dabei steht %0 für die Datei selbst, d steht für Laufwerk (drive) und p für den Dateipfad.
::alle weiteren Pfade sind relativ zu diesem Pfad
SET "SCRIPT_LOCATION=%~dp0"
CD /D %SCRIPT_LOCATION%

::create deploytools.jar per Maven und speichere Pfad zur jar in einer Variable 
SET "DEPLOY_TOOLS_PROJECT_DIR=DeployTools\de.axs.deploytools" 
CD /D %DEPLOY_TOOLS_PROJECT_DIR%
::CALL mvn -B clean install
::SET "DEPLOY_TOOLS_JAR=%DEPLOY_TOOLS_PROJECT_DIR%\target\deploytools-0.0.1.jar"
FOR %%F IN (target\*.jar) DO (
 set DEPLOY_TOOLS_JAR=%DEPLOY_TOOLS_PROJECT_DIR%\%%F
 goto deploy_tools_jar_found
)
:deploy_tools_jar_found
echo "%DEPLOY_TOOLS_JAR%"
CD /D %SCRIPT_LOCATION%
PAUSE