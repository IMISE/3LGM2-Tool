@ECHO ON &SETLOCAL

::mit der Variable %~dp0 kann man den Pfad der aktuell ausgeführten Batchdatei ermitteln.
::Dabei steht %0 für die Datei selbst, d steht für Laufwerk (drive) und p für den Dateipfad.
::#########!!!######### Alle relativen Pfade gehen von diesem Pfad aus #########!!!#########
SET "SCRIPT_LOCATION=%~dp0"
CD /D %SCRIPT_LOCATION%

::########################################################################################
::###                                                                                  ###
::###                      Diese Pfade wenn nötig anpassen                             ###
::###                                                                                  ###
::###           jeweils absolut angeben oder relativ zur %SCRIPT_LOCATION%             ###
::###                                                                                  ###
::########################################################################################

::Pfad zu Inno Setup
SET "INNOSETUP_PROGRAM_DIR=Inno Setup 5"
::Pfad zu 7zip
SET "SEVENZIP_PROGRAM_DIR=7zip"
::Pfad zu Cygwin bin-Verzeichnis
SET "CYGWIN_BIN_DIR=cygwin64\bin"
::Pfad zum Bat To Exe Converter
SET "BAT_TO_EXE_CONVERTER_PROGRAM_DIR=Bat_To_Exe_Converter\Windows (32 bit)"


::Pfad zum externen axsutils-Projekt
SET "AXSUTILS_PROJECT_DIR=..\..\..\axsutils_2\axsutils"
::Zielverzeichnis, in dem die Deploy-Artefakte (exe, zip und tar.gz) landen
SET "DEPLOY_DESTINATION_DIR=..\DeployResults"
::Basisname der von Innosetup erzeugten Exe-Installationsdatei. Diese wird noch um die Version ergänzt
SET "WINDOWS_INSTALLER_EXE_BASE_NAME=setup3lgm_V" 
::Basisname der zip und tar.gz-Dateien, die am Ende enstehen. Diese wird noch um die Version ergänzt
SET "ZIP_AND_TARGZ_RESULT_BASE_NAME=Tool3lgm_V" 


::Pfad zum Deploy-Projekt
SET "DEPLOY_PROJECT_DIR=.."
::Pfad zum Deploy-Ordner des Tools
SET "DEPLOY_PROJECT_TOOL3LGM_DIR=..\Tool3lgm"
::voller Name der Batch-Datei zum Starten des Tools (diese wird in eine exe umgewandelt)
SET "DEPLOY_PROJECT_TOOL3LGM_START_BAT=%DEPLOY_PROJECT_TOOL3LGM_DIR%\3lgm2tool.bat"
::einfacher Name der und voller Name zur Exe-Datei zum Starten des Tools (diese wird aus der Batch-Datei erzeugt)
SET "DEPLOY_PROJECT_TOOL3LGM_START_EXE_FILENAME=3lgm2tool.exe"
SET "DEPLOY_PROJECT_TOOL3LGM_START_EXE=%DEPLOY_PROJECT_TOOL3LGM_DIR%\%DEPLOY_PROJECT_TOOL3LGM_START_EXE_FILENAME%"
::voller Name der Icon-Datei, mit der die Exe und der Innosetup-Installer versehen wird
SET "DEPLOY_TOOL3LGM_ICON=..\DeployScriptsAndTools\Icons\toolIcon_gross.ico"

::Pfad zum Tool3lgm-Projekt
SET "TOOL3LGM_PROJECT_DIR=..\..\de.imise.tool3lgm"
::Pfad zum Original-Metamodell-Plugin
SET "METAMODEL_ORIGINAL_PROJECT_DIR=..\..\de.imise.metamodel.original"
::Pfad zum Service-Metamodell-Plugin
SET "METAMODEL_SERVICE_PROJECT_DIR=..\..\de.imise.metamodel.service"
::Pfad zum IHE-Template-Plugin
SET "IHE_TEMPLATE_PROJECT_DIR=..\..\de.imise.tool3lgm.template.ihe"

::Tool3LGMConstants.java gibt die Version vor. Das ist die Stelle, die vorher angepasst
::werden muss. Die Zeile mit diesem String wird über BATCH-Kommandos ausgelesen.
SET "JAVA_FILE_WITH_TOOL_VERSION=%TOOL3LGM_PROJECT_DIR%\src\main\java\de\imise\tool3lgm\Tool3lgmConstants.java"
SET "JAVA_FILE_WITH_TOOL_VERSION_LINE=public static final String TOOL_VERSION = " 

::update ApplicationVersion in Tool3LGMConstants and the Innosetup ISS-File
SET "ISS_FILE=%cd%\Innosetup_Tool3lgm.iss"
SET "ISS_FILE_LINE_VERSION_SRT=#define MyAppVersion \""
SET "ISS_FILE_LINE_VERSION_END=\""
SET "ISS_FILE_LINE_EXENAME_SRT=#define MyAppExeName \""
SET "ISS_FILE_LINE_EXENAME_END=\""
SET "ISS_FILE_LINE_OUTDIR__SRT=OutputDir="
SET "ISS_FILE_LINE_OUTDIR__END="
SET "ISS_FILE_LINE_OUTEXE__SRT=OutputBaseFilename="
SET "ISS_FILE_LINE_OUTEXE__END="
SET "ISS_FILE_LINE_ICON____SRT=SetupIconFile="
SET "ISS_FILE_LINE_ICON____END="
SET "ISS_FILE_LINE_FILEEXE_SRT=Source: \""
SET "ISS_FILE_LINE_FILEEXE_END=\"; DestDir: \"{app}\"; Flags: ignoreversion"
SET "ISS_FILE_LINE_FILEALL_SRT=Source: \""
SET "ISS_FILE_LINE_FILEALL_END=\"; DestDir: \"{app}\"; Flags: ignoreversion recursesubdirs createallsubdirs"

::update version in pom.xml of the tool
SET "POM_FILE=%TOOL3LGM_PROJECT_DIR%\pom.xml"
SET "POM_FILE_LINE_SRT=\\t<version>"
SET "POM_FILE_LINE_END=</version>"

SET "DEPLOY_TOOLS_PROJECT_DIR=DeployTools\de.axs.deploytools" 
::create deploytools per Maven und speichere Pfad zur jar in einer Variable 
CD /D %DEPLOY_TOOLS_PROJECT_DIR%
CALL mvn -B clean install
::suche die jar-Datei im target-Ordner des deploy-tools-Projektes
FOR %%F IN (target\*.jar) DO (
 set DEPLOY_TOOLS_JAR=%DEPLOY_TOOLS_PROJECT_DIR%\%%F
 goto deploy_tools_jar_found
)
:deploy_tools_jar_found
CD /D %SCRIPT_LOCATION%


@ECHO on
ECHO.

::Zeile mit der Version aus der Tool3lgmConstants-Datei lesen
SETLOCAL EnableDelayedExpansion
FOR /f "tokens=* usebackq" %%a IN (`FINDSTR /C:"%JAVA_FILE_WITH_TOOL_VERSION_LINE%" "%JAVA_FILE_WITH_TOOL_VERSION%"`) DO (
    SET z=%%a
    SET z=!z:"=?!
    FOR /f "tokens=1-3 delims=?" %%a IN ("!z!") DO SET lgmVersion=%%b
)
::3LGM-Verson ausgeben
ECHO Current version %lgmVersion%

::voller Name der von Innosetup erzeugten Exe-Installationsdateimit Version un Untertrichen statt Leerzeichen
SET "WINDOWS_INSTALLER_EXE_BASE_NAME=%WINDOWS_INSTALLER_EXE_BASE_NAME%%lgmVersion%" 
SET WINDOWS_INSTALLER_EXE_BASE_NAME=!WINDOWS_INSTALLER_EXE_BASE_NAME: =_!

::Abkürzung für den Aufruf des Java-ChangeLineHandlers
::Er ersetzt in allen Zeilen (i.d.R. genau eine) einer Datei, die mit einem bestimmten String beginnen und enden, den Mittelteil 
::Syntax: START %CLH% FILE_NAME LINE_START_STRING LINE_END_STRING REPLACE_STRING
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

::alte exe löschen (eigentlich gibt es eine Option, dass Bat_To_Exe_Converter.exe eine
::vorhandene Datei überschreibt, aber das funktioniert nicht!
DEL %DEPLOY_PROJECT_TOOL3LGM_START_EXE% /s /q
::tool3lgm2.exe neu bauen aus der aktuellen tool3lgm2.bat. Aber ohne das PAUSE am Ende,
::da die exe sonst nie beendet wird. Das Minus (-) vor dem Dateinamen ist der Marker für
::das disablen von PAUSE.
START /wait javaw -classpath "%DEPLOY_TOOLS_JAR%" de.axs.deploytools.BatchPauseEnabledSwitch "-%DEPLOY_PROJECT_TOOL3LGM_START_BAT%"
::Siehe https://documentation.help/bat-to-exe-converter/de.html#cmd
CALL "%BAT_TO_EXE_CONVERTER_PROGRAM_DIR%\Bat_To_Exe_Converter.exe" -bat "%DEPLOY_PROJECT_TOOL3LGM_START_BAT%" -save "%DEPLOY_PROJECT_TOOL3LGM_START_EXE%" -icon "%DEPLOY_TOOL3LGM_ICON%" -invisible
::Das PAUSE wieder aktivieren Plus (+) als Marker vor dem Dateinamen
START /wait javaw -classpath "%DEPLOY_TOOLS_JAR%" de.axs.deploytools.BatchPauseEnabledSwitch "+%DEPLOY_PROJECT_TOOL3LGM_START_BAT%"


::run Inno Setup
CD /d "%INNOSETUP_PROGRAM_DIR%"
START /wait Compil32 /cc "%ISS_FILE%"
CD /D %SCRIPT_LOCATION%

SET "ZIP_AND_TARGZ_RESULT_NAME=%ZIP_AND_TARGZ_RESULT_BASE_NAME%%lgmVersion%"
SET ZIP_AND_TARGZ_RESULT_NAME=%ZIP_AND_TARGZ_RESULT_NAME: =_%
ECHO %ZIP_AND_TARGZ_RESULT_BASE_NAME%
:: ZIP the deploy Files with 7zip
FOR /d %%X IN ("%DEPLOY_PROJECT_TOOL3LGM_DIR%") DO "%SEVENZIP_PROGRAM_DIR%\7z.exe" a "%DEPLOY_DESTINATION_DIR%\%ZIP_AND_TARGZ_RESULT_NAME%.zip" "%%X\"

CD /D %SCRIPT_LOCATION%

::copy deploy dir to CYGWIN dir and make tar.gz-File per batch Script
::CYGWIN_BIN_DIR
::XCOPY "D:\Work\3LGM_Deploy\3LGM2Tool2Deploy" "C:\Program Files\cygwin64\home\Ich\3LGM2Tool\" /S /E
::CALL "C:\Program Files\cygwin64\bin\bash.exe" -l -c "/home/Ich/DeployTool3LGMTarZip.sh"
::RD "C:\Program Files\cygwin64\home\Ich\3LGM2Tool" /s /q

::rename tar-gz filewith version and copy it to destination dir
::SET "ORIGINAL_FILE_NAME=C:\Program Files\cygwin64\home\Ich\3LGM2Tool.tar.gz"
::den Dateinamen mit der Version ergänzen
::START /wait javaw -classpath "%DEPLOY_TOOLS_JAR%" de.axs.deploytools.FileVersionHandler "%ISS_FILE%" "%ISS_FILE_LINE_SRT%" "%ISS_FILE_LINE_END%" "%ORIGINAL_FILE_NAME%" ".tar.gz" "%DEPLOY_DESTINATION_DIR%"




::copy deploy dir to CYGWIN dir and make tar.gz-File per batch Script
::CYGWIN_BIN_DIR
::XCOPY "D:\Work\3LGM_Deploy\3LGM2Tool2Deploy" "C:\Program Files\cygwin64\home\Ich\3LGM2Tool\" /S /E
::CALL "C:\Program Files\cygwin64\bin\bash.exe" -l -c "/home/Ich/DeployTool3LGMTarZip.sh"
::RD "C:\Program Files\cygwin64\home\Ich\3LGM2Tool" /s /q

::rename tar-gz filewith version and copy it to destination dir
::SET "ORIGINAL_FILE_NAME=C:\Program Files\cygwin64\home\Ich\3LGM2Tool.tar.gz"
::den Dateinamen mit der Version ergänzen
::START /wait javaw -classpath "%DEPLOY_TOOLS_JAR%" de.axs.deploytools.FileVersionHandler "%ISS_FILE%" "%ISS_FILE_LINE_SRT%" "%ISS_FILE_LINE_END%" "%ORIGINAL_FILE_NAME%" ".tar.gz" "%DEPLOY_DESTINATION_DIR%"






::PAUSE

:: ========== FUNCTIONS ==========
EXIT /B
::relativer Pfad zu absolut
:NORMALIZEPATH
  SET ABSOLUTEPATH=%~dpfn1
  EXIT /B