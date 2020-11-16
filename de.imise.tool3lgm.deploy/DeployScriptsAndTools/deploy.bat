@ECHO OFF &SETLOCAL

::Default Variablen setzen
::  Per Default launch4j ausfuehren
::  Alternativ: CMD-Line Parameter "-skip-launch4j" verwenden, um den Aufruf von launch4j zu unterdrücken
SET "SKIPLAUNCH4J=no" 
::  Per Default Maven Jobs ausfuehren
::  Alternativ: CMD-Line Parameter "-skip-mvn" verwenden
SET "SKIPMVN=no" 
::  Per Default create Install/Zip packages
::  Alternativ: CMD-Line Parameter "-skip-ipackages" verwenden
SET "SKIPIPACKAGES=no" 


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
SET "AXSUTILS_PROJECT_DIR=..\..\axsutils"
::Pfad zum meta-model-3lgm2 Repository
SET "METAMODEL3LGM2_DIR=..\..\..\meta-model-3lgm2"
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
SET "INNOSETUP_PROGRAM_DIR=InnoSetup6"
::Pfad zur 7zip-Exe
SET "SEVENZIP_PROGRAM_FILE=7zip\7z.exe"
::Pfad zu launch4j (erzeugt aus einer Jar-Datei eine Exe-Datei)
SET "LAUNCH4J_DIR=launch4j"

::Basisname der von Innosetup erzeugten Exe-Installationsdatei. Diese wird noch um die Version ergänzt
SET "WINDOWS_INSTALLER_EXE_BASE_NAME=setup3lgm_V" 
::Basisname der zip und tar.gz-Dateien, die am Ende enstehen. Diese wird noch um die Version ergänzt
SET "ZIP_AND_TARGZ_RESULT_BASE_NAME=Tool3lgm_V" 


::Name des Ordners mit dem zu deployenden Tools. Das ist auch der Name des Ordners, in dem das Tool
::in der zip und tar.gz-Datei liegt.
SET "DEPLOY_PROJECT_TOOL3LGM_DIR_NAME=Tool3lgm"
::Pfad zum Ordner mit dem zu deployenden Tools
SET "DEPLOY_PROJECT_TOOL3LGM_DIR=..\%DEPLOY_PROJECT_TOOL3LGM_DIR_NAME%"

::einfacher Name der und voller Name zur Exe-Datei zum Starten des Tools im Ordner mit dem zu
::deployenden Tool (diese wird aus der Batch-Datei erzeugt)
SET "DEPLOY_PROJECT_TOOL3LGM_START_EXE_FILENAME=3lgm2tool.exe"
::Pfad dieser Exe
SET "DEPLOY_PROJECT_TOOL3LGM_START_EXE=%DEPLOY_PROJECT_TOOL3LGM_DIR%\%DEPLOY_PROJECT_TOOL3LGM_START_EXE_FILENAME%"

::voller Name der Icon-Datei, mit der die Exe und der Innosetup-Installer versehen wird
SET "DEPLOY_TOOL3LGM_ICON=..\DeployScriptsAndTools\Icons\toolIcon_gross.ico"

::[ToDo] Versionsermittlung anpassen
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
::muss die pom.xml-Datei auf jeden Fall formatiert sein (bzw. für die Tab-Version muss vor dem <version>-Tag muss ein Tab stehen!)
::Hinweis: auch spitze Klammern müsses gequotet werden: "^<"
SET "POM_FILE=%TOOL3LGM_PROJECT_DIR%\pom.xml"
SET "POM_FILE_LINE_SRT=  ^<version^>"
::"Tab"-Version. Sicherheitshalber sollten keine "Tab"s verwendet werden. In der POM-Datei wurden alle Tabs durch "  " (2 Leerzeichen) ersetzt.
::SET "POM_FILE_LINE_SRT=	^<version^>"
SET "POM_FILE_LINE_END=^</version^>"

::update version in 3lgm2tool.cfg.xml und 3lgm2tool_jre-bundled.cfg.xml
::Zeile mit der Version in der pom.xml des Tools wird ebenfalls vor dem Compilieren geupdatet. Damit das klappt
::Hinweise: auch spitze Klammern müsses gequotet werden: "^<"
::  Die Formatierung der Datei ist wichtig (Leerzeichen)!
SET "LAUNCH4J_FILE=%LAUNCH4J_DIR%\3lgm2tool.cfg.xml"
SET "LAUNCH4J_JRE_FILE=%LAUNCH4J_DIR%\3lgm2tool_jre-bundled.cfg.xml"
SET "LAUNCH4J_FILE_LINE_SRT=    ^<txtProductVersion^>"
SET "LAUNCH4J_FILE_LINE_END=^</txtProductVersion^>"


::Pfad zu den Java/Deploy-Tools. Diese sind extra für dieses Projekt geschrieben und befinden sich ebenfalls in
::diesem Deply-Projekt. Sie bieten im Grunde 2 Funktionen:
:: (1.) Austausch von Zeilen in einer Datei mit einem bestimmten Anfang und Ende
:: (2.) Auskommentieren und Wiederreinnehmen von PAUSE-Anweisungen in Batch-Dateien
SET "DEPLOY_TOOLS_PROJECT_DIR=DeployTools\de.axs.deploytools" 

::Maven Build Job ueberspringen
IF "%SKIPMVN%"=="yes" (
  ECHO "### Skipping Maven Job (SKIPMVN=yes)"
  GOTO MVN_NEXT1
)
FOR %%A IN (%*) DO (
  IF "%%A"=="-skip-mvn" (
    ECHO "### Skipping Maven Job (-skip-mvn)"
    GOTO MVN_NEXT1
  )
)

::create deploytools per Maven und speichere Pfad zur jar in einer Variable 
CD /D %DEPLOY_TOOLS_PROJECT_DIR%
CALL mvn -B clean install

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

:MVN_NEXT1


::suche die jar-Datei der Deploy-Tools im target-Ordner des deploy-tools-Projektes. Falls mal jemand die Version
::der Deploy-Tools in deren pom.xml ändert, dann heißt die generierte jar-Datei anders. Indem man sie hier
::automatisch heraussucht, funktioniert das hier dann trotzdem noch.
FOR %%F IN (target\*.jar) DO (
 set "DEPLOY_TOOLS_JAR=%DEPLOY_TOOLS_PROJECT_DIR%^\%%F"
 goto deploy_tools_jar_found
)
:deploy_tools_jar_found
CD /D %SCRIPT_LOCATION%

::@ECHO on
ECHO.

::[ToDo] Versionsermittlung anpassen:
::  - maven-Jobs an dieser Stelle ausführen
::  - anschließend Version aus version.info auslesen
::
::Zeile mit der Version aus der Tool3lgmConstants.java-Datei lesen
SETLOCAL EnableDelayedExpansion
FOR /f "tokens=* usebackq" %%a IN (`FINDSTR /C:"%JAVA_FILE_WITH_TOOL_VERSION_LINE%" "%JAVA_FILE_WITH_TOOL_VERSION%"`) DO (
    SET z=%%a
    SET z=!z:"=?!
    FOR /f "tokens=1-3 delims=?" %%a IN ("!z!") DO SET lgmVersion=%%b
)
::3LGM-Version ausgeben
ECHO "### Current version %lgmVersion%"
ECHO.

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
::Debug:
::  ECHO %CLH% 
::  ECHO "%POM_FILE%" "%POM_FILE_LINE_SRT%" "%POM_FILE_LINE_END%" "%lgmVersion%"
::  START javaw -classpath "%DEPLOY_TOOLS_JAR%" de.axs.deploytools.ChangeLineHandler "%POM_FILE%" "%POM_FILE_LINE_SRT%" "%POM_FILE_LINE_END%" "%lgmVersion%"
::Hinweis: Aktuell darf die POM-Datei nicht geändert werden, da es Abhängigkeiten zu anderen POM Dateien (Plugins) gibt.
::  START %CLH% "%POM_FILE%" "%POM_FILE_LINE_SRT%" "%POM_FILE_LINE_END%" "%lgmVersion%"
::Debug:
::  EXIT /B
::Version in die launch4j Config-Dateien schreiben
START %CLH% "%LAUNCH4J_FILE%" "%LAUNCH4J_FILE_LINE_SRT%" "%LAUNCH4J_FILE_LINE_END%" "%lgmVersion%"
START %CLH% "%LAUNCH4J_JRE_FILE%" "%LAUNCH4J_FILE_LINE_SRT%" "%LAUNCH4J_FILE_LINE_END%" "%lgmVersion%"
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



::IHE Domain Ontology pull
::ToDo: Repo pullen
::CD /D %METAMODEL3LGM2_DIR%
::git pull
::CD /D %SCRIPT_LOCATION%
::IHE Domain Ontology aus git Repository aktualisieren (kopieren)
ECHO "### Copy IHE Domain Ontology %METAMODEL3LGM2_DIR%\IHE\iheDomain_Ontology_straight-forward_v2.rdf -> %SCRIPT_LOCATION%\..\Tool3lgm\Templates\IHE\"
COPY /Y %METAMODEL3LGM2_DIR%\IHE\iheDomain_Ontology_straight-forward_v2.rdf %SCRIPT_LOCATION%\..\Tool3lgm\Templates\IHE\


::launch4j: erzeugt aus einer Jar-Datei eine Exe-Datei
::  es ist entweder Bat-to-Exe oder launch4j erforerlich, um die Exe-Datei zu erzeugen.
IF "%SKIPLAUNCH4J%"=="yes" (
  ECHO "### Skipping launch4j (SKIPLAUNCH4J=yes)"
  GOTO LAUNCH4J_NEXT
)
FOR %%A IN (%*) DO (
  IF "%%A"=="-skip-launch4j" (
    ECHO "### Skipping launch4j (-skip-launch4j)"
    GOTO LAUNCH4J_NEXT
  )
)

ECHO "### Execute launch4j to compile exe files"
CD /D %SCRIPT_LOCATION%
CALL "%LAUNCH4J_DIR%\launch4jc.exe" %LAUNCH4J_FILE%
CALL "%LAUNCH4J_DIR%\launch4jc.exe" %LAUNCH4J_JRE_FILE%

:LAUNCH4J_NEXT


::create Install/Zip packages
IF "%SKIPIPACKAGES%"=="yes" (
  ECHO "### Skipping the creation von Install resp. Zip packages (SKIPIPACKAGES=yes)"
  GOTO SKIPIPACKAGES_NEXT
)
FOR %%A IN (%*) DO (
  IF "%%A"=="-skip-ipackages" (
    ECHO "### Skipping the creation von Install resp. Zip packages (-skip-ipackages)"
    GOTO SKIPIPACKAGES_NEXT
  )
)
::Bestehende Dateien im Verzeichnis %DEPLOY_DESTINATION_DIR% löschen
ECHO "### Entferne vorhandene Dateien im Verzeichnis %DEPLOY_DESTINATION_DIR%"
DEL %DEPLOY_DESTINATION_DIR%\*  /s /q


::[ToDo]
::-> 2 Versionen bauen: 1) inkl. JRE, 2.) ohne JRE
:: 1) inkl. JRE
:: - exe ohne JRE verschieben: 3lgm2tool.exe -> ../3lgm2tool_nojre.exe
:: - exe inkl. JRE umbenennen: 3lgm2tool_jre.exe -> 3lgm2tool.exe
:: - Inno Setup aufrufen; Setup-Datei %WINDOWS_INSTALLER_EXE_BASE_NAME%.exe in %WINDOWS_INSTALLER_EXE_BASE_NAME%_JRE.exe
:: - Zip aufrufen und Zip-Datei mit "_JRE" im Namen erzeugen
:: 2) ohne JRE
:: - exe inkl. JRE zurück umbenennen und verschieben: 3lgm2tool.exe -> ../3lgm2tool_jre.exe
:: - exe ohne JRE zurück verschieben: ../3lgm2tool_nojre.exe -> 3lgm2tool.exe
:: - JRE verschieben: jre -> ../
:: - Inno Setup aufrufen
:: - Zip aufrufen
:: - tar.gz aufrufen
:: - JRE zurück verschieben: ../jre -> jre
:: - exe inkl. JRE zurück verschieben: ../3lgm2tool_jre.exe -> 3lgm2tool_jre.exe


::run Inno Setup -> Erzeuge Windows-Installer
CD /D "%INNOSETUP_PROGRAM_DIR%"
START /wait Compil32 /cc "%ISS_FILE%"
CD /D %SCRIPT_LOCATION%

::Name der zu erstellenden zip und tar.gz-Dateien
SET "ZIP_AND_TARGZ_RESULT_NAME=%ZIP_AND_TARGZ_RESULT_BASE_NAME%%lgmVersion%"
SET ZIP_AND_TARGZ_RESULT_NAME=%ZIP_AND_TARGZ_RESULT_NAME: =_%
ECHO "### ZIP_AND_TARGZ_RESULT_NAME = %ZIP_AND_TARGZ_RESULT_NAME% "

:: ZIP the deploy Files with 7zip
FOR /d %%X IN ("%DEPLOY_PROJECT_TOOL3LGM_DIR%") DO "%SEVENZIP_PROGRAM_FILE%" a "%DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.zip" "%%X\"

::mit 7zip auch die tar.gz erstellen. In dieser sind dann alle Dateien mit dem Linux-Attribut "ausführbar" (Group + User) versehen
"%SEVENZIP_PROGRAM_FILE%" a -ttar "%DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.tar" "%DEPLOY_PROJECT_TOOL3LGM_DIR%"
"%SEVENZIP_PROGRAM_FILE%" a -tgzip "%DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.tar.gz" "%DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.tar"
DEL %DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.tar

:SKIPIPACKAGES_NEXT

CD /D %INITIAL_DIR%
::PAUSE

:: ========== FUNCTIONS ==========
EXIT /B
::relativer Pfad zu absolut
:NORMALIZEPATH
  SET ABSOLUTEPATH=%~dpfn1
  EXIT /B
