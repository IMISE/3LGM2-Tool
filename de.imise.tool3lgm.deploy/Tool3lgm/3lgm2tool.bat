@echo off
::change to script directory
PushD "%~dp0"
::Max Memory for 32-Bit Systems
SET MEMORY32=-Xmx512m -Xss20m 
::Max Memory for 64-Bit Systems
SET MEMORY64=-Xmx768m -Xss32m
::Set Max Memory to 32-Bit
SET MEMORY=%MEMORY32%
::wmic os get osarchitecture -> if return String contains "64" -> Set Max Memory to 64-Bit
wmic os get osarchitecture | FINDSTR /IL "64" > NUL
IF %ERRORLEVEL% EQU 0 SET MEMORY=%MEMORY64%
set PATH=%PATH%;.;lib;Plugins
@echo on
::java %MEMORY% -jar ./lib/tool3lgm.jar
java %MEMORY% -classpath ".;./lib/*;./Plugins/*" -splash:splash.gif de.imise.tool3lgm.Tool3lgmMain
pause
::change to last directory before changing to script directory 
PopD