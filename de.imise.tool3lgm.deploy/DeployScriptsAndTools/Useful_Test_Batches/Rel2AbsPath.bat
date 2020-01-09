CALL :NORMALIZEPATH "..\..\..\foo\bar.txt"
SET BLAH=%RETVAL%

ECHO "%BLAH%"

:: ========== FUNCTIONS ==========
EXIT /B

:NORMALIZEPATH
  SET RETVAL=%~dpfn1
  EXIT /B