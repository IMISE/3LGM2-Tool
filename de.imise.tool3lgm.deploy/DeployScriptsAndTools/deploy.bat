@ECHO ON &SETLOCAL

SET "INITIAL_DIR=%CD%"

::mit der Variable %~dp0 kann man den Pfad der aktuell ausgeführten Batchdatei ermitteln.
::Dabei steht %0 für die Datei selbst, d steht für Laufwerk (drive) und p für den Dateipfad.
::#########!!!######### Alle relativen Pfade gehen von diesem Pfad aus #########!!!#########
SET "SCRIPT_LOCATION=%~dp0"
CD /D %SCRIPT_LOCATION%

::########################################################################################
::###                                                                                  ###
::###              A N P A S S E N   A U F   D E R   Z I E L M A S C H I N E           ###
::###                                                                                  ###
::###                      Diese Pfade wenn nötig anpassen                             ###
::###                                                                                  ###
::###           jeweils absolut angeben oder relativ zur %SCRIPT_LOCATION%             ###
::###                                                                                  ###
::########################################################################################
::Zielverzeichnis, in dem die Deploy-Artefakte (exe, zip und tar.gz) landen
SET "DEPLOY_DESTINATION_DIR=..\DeployResults"
::Pfad zum externen axsutils-Projekt
SET "AXSUTILS_PROJECT_DIR=..\..\..\axsutils_2\axsutils"
::Pfad zum Tool3lgm-Projekt
SET "TOOL3LGM_PROJECT_DIR=..\..\de.imise.tool3lgm"
::Pfad zum Original-Metamodell-Plugin
SET "METAMODEL_ORIGINAL_PROJECT_DIR=..\..\de.imise.metamodel.original"
::Pfad zum Service-Metamodell-Plugin
SET "METAMODEL_SERVICE_PROJECT_DIR=..\..\de.imise.metamodel.service"
::Pfad zum IHE-Template-Plugin
SET "IHE_TEMPLATE_PROJECT_DIR=..\..\de.imise.tool3lgm.template.ihe"


::########################################################################################
::###                                                                                  ###
::###   Die folgenden Ordner und Pfade sind auch alle relativ zur %SCRIPT_LOCATION%    ###
::###                                                                                  ###
::###   Sie müssen aber nur angepasst werden, wenn die Struktur oder Abläufe           ###
::###               innerhalb des Deploy-Projektes geändert wird.                      ###
::###                                                                                  ###
::########################################################################################
::Pfad zu Inno Setup
SET "INNOSETUP_PROGRAM_DIR=Inno Setup 5"
::Pfad zur 7zip-Exe
SET "SEVENZIP_PROGRAM_FILE=7zip\7z.exe"
::Pfad zum Bat To Exe Converter
SET "BAT_TO_EXE_CONVERTER_PROGRAM_DIR=Bat_To_Exe_Converter\Windows (32 bit)"


::Basisname der von Innosetup erzeugten Exe-Installationsdatei. Diese wird noch um die Version ergänzt
SET "WINDOWS_INSTALLER_EXE_BASE_NAME=setup3lgm_V" 
::Basisname der zip und tar.gz-Dateien, die am Ende enstehen. Diese wird noch um die Version ergänzt
SET "ZIP_AND_TARGZ_RESULT_BASE_NAME=Tool3lgm_V" 


::Name des Ordners mit dem zu deployenden Tools. Das ist auch der Name des Ordners, in dem das Tool
::in der zip und tar.gz-Datei liegt.
SET "DEPLOY_PROJECT_TOOL3LGM_DIR_NAME=Tool3lgm"
::Pfad zum Ordner mit dem zu deployenden Tools
SET "DEPLOY_PROJECT_TOOL3LGM_DIR=..\%DEPLOY_PROJECT_TOOL3LGM_DIR_NAME%"
::voller Name der Batch-Datei zum Starten des Tools im Ordner mit dem zu deployenden Tool (diese wird
::in eine exe umgewandelt)
SET "DEPLOY_PROJECT_TOOL3LGM_START_BAT=%DEPLOY_PROJECT_TOOL3LGM_DIR%\3lgm2tool.bat"
::einfacher Name der und voller Name zur Exe-Datei zum Starten des Tools im Ordner mit dem zu
::deployenden Tool (diese wird aus der Batch-Datei erzeugt)
SET "DEPLOY_PROJECT_TOOL3LGM_START_EXE_FILENAME=3lgm2tool.exe"
::Pfad dieser Exe
SET "DEPLOY_PROJECT_TOOL3LGM_START_EXE=%DEPLOY_PROJECT_TOOL3LGM_DIR%\%DEPLOY_PROJECT_TOOL3LGM_START_EXE_FILENAME%"
::voller Name der Icon-Datei, mit der die Exe und der Innosetup-Installer versehen wird
SET "DEPLOY_TOOL3LGM_ICON=..\DeployScriptsAndTools\Icons\toolIcon_gross.ico"

::Tool3LGMConstants.java gibt die Version vor. Das ist die Stelle, die vorher im Code angepasst
::werden muss. Die Zeile mit diesem String wird über BATCH-Kommandos ausgelesen.
SET "JAVA_FILE_WITH_TOOL_VERSION=%TOOL3LGM_PROJECT_DIR%\src\main\java\de\imise\tool3lgm\Tool3lgmConstants.java"
SET "JAVA_FILE_WITH_TOOL_VERSION_LINE=public static final String TOOL_VERSION = " 


::update ApplicationVersion in Tool3LGMConstants and the Innosetup ISS-File
::Pfad zur Innosetup-Scriptdatei zum Erzeugen des Windows-Installers des Tools
SET "ISS_FILE=%cd%\Innosetup_Tool3lgm.iss"
::jeweils Anfang ("_SRT") und Ende ("_END") der Zeilen in der Innosetup-Scriptdatei, die vor dem Generieren des
::Installers aktualisert werden müssen
::Zeile mit der Tool-Version, die auch der Installer kennen muss
SET "ISS_FILE_LINE_VERSION_SRT=#define MyAppVersion \""
SET "ISS_FILE_LINE_VERSION_END=\""
::Name der Exe-Datei zum Starten des Tools (das ist die, die aus der bat-Datei zum Starten des Tools erzeugt wird)
SET "ISS_FILE_LINE_EXENAME_SRT=#define MyAppExeName \""
SET "ISS_FILE_LINE_EXENAME_END=\""
::Zielverzeichnis in das der Installer geschrieben wird (%DEPLOY_DESTINATION_DIR%)
SET "ISS_FILE_LINE_OUTDIR__SRT=OutputDir="
SET "ISS_FILE_LINE_OUTDIR__END="
::Dateiname des zu bauenden Installers
SET "ISS_FILE_LINE_OUTEXE__SRT=OutputBaseFilename="
SET "ISS_FILE_LINE_OUTEXE__END="
::Icon des Installers
SET "ISS_FILE_LINE_ICON____SRT=SetupIconFile="
SET "ISS_FILE_LINE_ICON____END="
::zu installierende Dateien (dass die Exe hier extra steht, war schon so bei den mit dem Innosetup-Wizard erzeugten
::ISS-Dateien)
SET "ISS_FILE_LINE_FILEEXE_SRT=Source: \""
SET "ISS_FILE_LINE_FILEEXE_END=\"; DestDir: \"{app}\"; Flags: ignoreversion"
SET "ISS_FILE_LINE_FILEALL_SRT=Source: \""
SET "ISS_FILE_LINE_FILEALL_END=\"; DestDir: \"{app}\"; Flags: ignoreversion recursesubdirs createallsubdirs"

::update version in pom.xml of the tool
::Zeile mit der Version in der pom.xml des Tools wird ebenfalls vor dem Compilieren geupdatet. Damit das klappt
::muss die pom.xml-Datei auf jeden Fall formatiert sein (bzw. vor dem <version>-Tag muss ein Tab stehen!)
SET "POM_FILE=%TOOL3LGM_PROJECT_DIR%\pom.xml"
SET "POM_FILE_LINE_SRT=\\t<version>"
SET "POM_FILE_LINE_END=</version>"

::Pfad zu den Java/Deploy-Tools. Diese sind extra für dieses Projekt geschrieben und befinden sich ebenfalls in
::diesem Deply-Projekt. Sie bieten im Grunde 2 Funktionen:
:: (1.) Austausch von Zeilen in einer Datei mit einem bestimmten Anfang und Ende
:: (2.) Auskommentieren und Wiederreinnehmen von PAUSE-Anweisungen in Batch-Dateien
SET "DEPLOY_TOOLS_PROJECT_DIR=DeployTools\de.axs.deploytools" 
::create deploytools per Maven und speichere Pfad zur jar in einer Variable 
CD /D %DEPLOY_TOOLS_PROJECT_DIR%
CALL mvn -B clean install
::suche die jar-Datei der Deploy-Tools im target-Ordner des deploy-tools-Projektes. Falls mal jemand die Version
::der Deploy-Tools in deren pom.xml ändert, dann heißt die generierte jar-Datei anders. Indem man sie hier
::automatisch heraussucht, funktioniert das hier dann trotzdem noch.
FOR %%F IN (target\*.jar) DO (
 set DEPLOY_TOOLS_JAR=%DEPLOY_TOOLS_PROJECT_DIR%\%%F
 goto deploy_tools_jar_found
)
:deploy_tools_jar_found
CD /D %SCRIPT_LOCATION%

@ECHO on
ECHO.

::Zeile mit der Version aus der Tool3lgmConstants.java-Datei lesen
SETLOCAL EnableDelayedExpansion
FOR /f "tokens=* usebackq" %%a IN (`FINDSTR /C:"%JAVA_FILE_WITH_TOOL_VERSION_LINE%" "%JAVA_FILE_WITH_TOOL_VERSION%"`) DO (
    SET z=%%a
    SET z=!z:"=?!
    FOR /f "tokens=1-3 delims=?" %%a IN ("!z!") DO SET lgmVersion=%%b
)
::3LGM-Verson ausgeben
ECHO Current version %lgmVersion%

::voller Name der von Innosetup erzeugten Exe-Installationsdatei mit Version und Untertrichen statt Leerzeichen
SET "WINDOWS_INSTALLER_EXE_BASE_NAME=%WINDOWS_INSTALLER_EXE_BASE_NAME%%lgmVersion%" 
SET WINDOWS_INSTALLER_EXE_BASE_NAME=!WINDOWS_INSTALLER_EXE_BASE_NAME: =_!

::Abkürzung für den Aufruf des Java-ChangeLineHandlers aus den Java-Deploy-Tools
::Syntax: START %CLH% FILE_NAME LINE_START_STRING LINE_END_STRING REPLACE_STRING
::Er ersetzt in allen Zeilen (i.d.R. genau eine) einer Datei, die mit einem bestimmten String beginnen und enden,
::den Mittelteil.
::Wenn man sehen will, was genau ersetzt wurde, dann muss man in der folgenden Batch-Code-Zeile das 'javaw' durch
::'java' ersetzen. Ist die Anzeigezeit zu kurz, muss man diese in der Datei ChangeLineHandler.java hochsetzen.
SET "CLH=/wait javaw -classpath ^"%DEPLOY_TOOLS_JAR%^" de.axs.deploytools.ChangeLineHandler"

::Version in die pom.xml-Datei schreiben
START %CLH% "%POM_FILE%" "%POM_FILE_LINE_SRT%" "%POM_FILE_LINE_END%" "%lgmVersion%"
::Version in die ISS-Datei schreiben
START %CLH% "%ISS_FILE%" "%ISS_FILE_LINE_VERSION_SRT%" "%ISS_FILE_LINE_VERSION_END%" "%lgmVersion%"
::ExeName in die ISS-Datei schreiben
START %CLH% "%ISS_FILE%" "%ISS_FILE_LINE_EXENAME_SRT%" "%ISS_FILE_LINE_EXENAME_END%" "%DEPLOY_PROJECT_TOOL3LGM_START_EXE_FILENAME%"
::Zielverzeichnis in die ISS-Datei schreiben
CALL :NORMALIZEPATH "%DEPLOY_DESTINATION_DIR%"
START %CLH% "%ISS_FILE%" "%ISS_FILE_LINE_OUTDIR__SRT%" "%ISS_FILE_LINE_OUTDIR__END%" "%ABSOLUTEPATH%"
::Basename der von Innosetup gebauten Installations-Exe-Datei in die ISS-Datei schreiben
START %CLH% "%ISS_FILE%" "%ISS_FILE_LINE_OUTEXE__SRT%" "%ISS_FILE_LINE_OUTEXE__END%" "%WINDOWS_INSTALLER_EXE_BASE_NAME%"
::Icon in die ISS-Datei schreiben
CALL :NORMALIZEPATH "%DEPLOY_TOOL3LGM_ICON%"
START %CLH% "%ISS_FILE%" "%ISS_FILE_LINE_ICON____SRT%" "%ISS_FILE_LINE_ICON____END%" "%ABSOLUTEPATH%"
::Exe in die ISS-Datei schreiben
CALL :NORMALIZEPATH "%DEPLOY_PROJECT_TOOL3LGM_START_EXE%"
START %CLH% "%ISS_FILE%" "%ISS_FILE_LINE_FILEEXE_SRT%" "%ISS_FILE_LINE_FILEEXE_END%" "%ABSOLUTEPATH%"
::Alle Files in die ISS-Datei schreiben
CALL :NORMALIZEPATH "%DEPLOY_PROJECT_TOOL3LGM_DIR%"
START %CLH% "%ISS_FILE%" "%ISS_FILE_LINE_FILEALL_SRT%" "%ISS_FILE_LINE_FILEALL_END%" "%ABSOLUTEPATH%\*"

::Jetzt alle Teilprojekte per Maven bauen. Die pom.xml des tool3lgm-Projektes kopiert am Ende die
::axutils.jar und die tool3lgm.jar sowie alle anderen benötigten Bibliotheken ins lib-Verzeichnis
::des zu deployenden Tools im %DEPLOY_PROJECT_TOOL3LGM_DIR%.
::Die Plugin-Projekte kopieren jeweils ihr jar-Artefakt auch innerhalb der pom.xml in das
::Plugins-Verzeichnis des zu deployenden Tools.

::Da die Pfade immer relativ zur %SCRIPT_LOCATION% sind, muss am Ende immer zur %SCRIPT_LOCATION%
::zurück gewechselt werden. Das ist auf jeden Fall einfacher, als erst nochmal die ganzen Pfade
::absolut zu machen.

::create axsutils per Maven
CD /D %AXSUTILS_PROJECT_DIR%
CALL mvn -B clean install
CD /D %SCRIPT_LOCATION%

::create Tool3lgm per Maven
CD /D %TOOL3LGM_PROJECT_DIR%
CALL mvn -B clean install
CD /D %SCRIPT_LOCATION%

::create Original-Metamodel per Maven
CD /D %METAMODEL_ORIGINAL_PROJECT_DIR%
CALL mvn -B clean install
CD /D %SCRIPT_LOCATION%

::create Service-Metamodel per Maven
CD /D %METAMODEL_SERVICE_PROJECT_DIR%
CALL mvn -B clean install
CD /D %SCRIPT_LOCATION%

::create IHE-Template Plugin per Maven
CD /D %IHE_TEMPLATE_PROJECT_DIR%
CALL mvn -B clean install
CD /D %SCRIPT_LOCATION%

::Jetzt im zu deployenden Tool die exe-Datei zum Starten des Tools aus der bat-Datei neu erzeugen.
::Vorher alte exe löschen (eigentlich gibt es eine Option, so dass Bat_To_Exe_Converter.exe eine
::vorhandene Datei überschreibt, aber das funktioniert nicht!)
DEL %DEPLOY_PROJECT_TOOL3LGM_START_EXE% /s /q
::tool3lgm2.exe neu bauen aus der aktuellen tool3lgm2.bat. Aber ohne das PAUSE am Ende, da die exe
::sonst nie beendet wird. Das macht der BatchPauseEnabledSwitch aus den Java-Deploy-Tools.
SET "BATCH_PAUSE_ENABLED_SWITCH=/wait javaw -classpath ^"%DEPLOY_TOOLS_JAR%^" de.axs.deploytools.BatchPauseEnabledSwitch"
::Das Minus (-) vor dem Dateinamen ist der Marker für das disablen von PAUSE.
START %BATCH_PAUSE_ENABLED_SWITCH% "-%DEPLOY_PROJECT_TOOL3LGM_START_BAT%"
::Siehe https://documentation.help/bat-to-exe-converter/de.html#cmd
CALL "%BAT_TO_EXE_CONVERTER_PROGRAM_DIR%\Bat_To_Exe_Converter.exe" -bat "%DEPLOY_PROJECT_TOOL3LGM_START_BAT%" -save "%DEPLOY_PROJECT_TOOL3LGM_START_EXE%" -icon "%DEPLOY_TOOL3LGM_ICON%" -invisible
::Das PAUSE wieder aktivieren. Plus (+) als Marker vor dem Dateinamen
START %BATCH_PAUSE_ENABLED_SWITCH% "+%DEPLOY_PROJECT_TOOL3LGM_START_BAT%"


::run Inno Setup -> Erzeuge Windows-Installer
CD /D "%INNOSETUP_PROGRAM_DIR%"
START /wait Compil32 /cc "%ISS_FILE%"
CD /D %SCRIPT_LOCATION%

::Name der zu erstellenden zip und tar.gz-Dateien
SET "ZIP_AND_TARGZ_RESULT_NAME=%ZIP_AND_TARGZ_RESULT_BASE_NAME%%lgmVersion%"
SET ZIP_AND_TARGZ_RESULT_NAME=%ZIP_AND_TARGZ_RESULT_NAME: =_%
ECHO %ZIP_AND_TARGZ_RESULT_BASE_NAME%

:: ZIP the deploy Files with 7zip
FOR /d %%X IN ("%DEPLOY_PROJECT_TOOL3LGM_DIR%") DO "%SEVENZIP_PROGRAM_FILE%" a "%DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.zip" "%%X\"

::mit 7zip auch die tar.gz erstellen. In dieser sind dann alle Dateien mit dem Linux-Attribut "ausführbar" (Group + User) versehen
"%SEVENZIP_PROGRAM_FILE%" a -ttar "%DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.tar" "%DEPLOY_PROJECT_TOOL3LGM_DIR%"
"%SEVENZIP_PROGRAM_FILE%" a -tgzip "%DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.tar.gz" "%DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.tar"
DEL %DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.tar

CD /D %INITIAL_DIR%
::PAUSE

:: ========== FUNCTIONS ==========
EXIT /B
::relativer Pfad zu absolut
:NORMALIZEPATH
  SET ABSOLUTEPATH=%~dpfn1
  EXIT /B