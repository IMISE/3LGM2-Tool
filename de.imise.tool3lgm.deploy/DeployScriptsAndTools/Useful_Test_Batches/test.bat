@ECHO ON
setlocal enabledelayedexpansion
:: get the wohle line with the registry entry "   rootdir   REG_SZ    <path_to_tool3lgm.exe>"
:: different Windows versions have a different number and kind of whitespaces (whitepsaces and tabs) between the key parts
::FOR /f "tokens=* delims=*" %%A IN ('REG QUERY "HKLM\SOFTWARE\3LGM2Tool" /v rootdir') DO SET TOOLDIR=%%A
FOR /f "tokens=* delims=*" %%A IN ('REG QUERY "HKEY_CLASSES_ROOT\tool3lgm\shell\open\command" /s') DO SET TOOLDIR=%%A
ECHO %TOOLDIR%
pause
set TOOLDIR=%TOOLDIR:"=}%
set TOOLDIR=%TOOLDIR: }%1}=%
ECHO %TOOLDIR%
pause
for /f "tokens=2* delims=}" %%A in ("%TOOLDIR%") do set TOOLDIR=%%A
ECHO %TOOLDIR%
pause
SET "str1=%TOOLDIR%"
pause
SET "sstr=:"
pause
set /a position=0
pause
Set "sst0=!str1:*%sstr%=!"
pause
if '%sst0%'=='%str1%' echo '%sstr%' not found in '%str1%'&goto :eof
pause
Set 'sst1=!str1:%sstr%%sst0%=!'
pause
if '%sst1%' neq '' for /l %%i in (0,1,8189) do if '!sst1:~%%i,1!' neq '' set /a position+=1
pause
echo.Position of %sstr% is %position%
pause

