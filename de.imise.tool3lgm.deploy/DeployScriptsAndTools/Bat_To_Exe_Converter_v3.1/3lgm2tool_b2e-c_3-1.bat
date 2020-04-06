::[Bat To Exe Converter]
::
::YAwzoRdxOk+EWAjk
::fBw5plQjdCyDJGyX8VAjFAhdXBSHAGyzEolPxM/W0/6IrUFQB+44dd3n3rHAKe0a+UCqYZki2ilWn8ZCCB5Ich2yUisxuWJNr1eGJc6MpxzAREy96UQ8CFldhGzenxcvb9xks8AM3W63/0Kf
::YAwzuBVtJxjWCl3EqQJgSA==
::ZR4luwNxJguZRRnk
::Yhs/ulQjdF+5
::cxAkpRVqdFKZSTk=
::cBs/ulQjdF+5
::ZR41oxFsdFKZSDk=
::eBoioBt6dFKZSDk=
::cRo6pxp7LAbNWATEpCI=
::egkzugNsPRvcWATEpCI=
::dAsiuh18IRvcCxnZtBJQ
::cRYluBh/LU+EWAnk
::YxY4rhs+aU+IeA==
::cxY6rQJ7JhzQF1fEqQJkZkoaHUrSXA==
::ZQ05rAF9IBncCkqN+0xwdVsAAlXMbSXqZg==
::ZQ05rAF9IAHYFVzEqQJjBDx5HkS2M2S2Zg==
::eg0/rx1wNQPfEVWB+kM9LVsJDGQ=
::fBEirQZwNQPfEVWB+kM9LVsJDGQ=
::cRolqwZ3JBvQF1fEqQJQ
::dhA7uBVwLU+EWDk=
::YQ03rBFzNR3SWATElA==
::dhAmsQZ3MwfNWATElA==
::ZQ0/vhVqMQ3MEVWAtB9wSA==
::Zg8zqx1/OA3MEVWAtB9wSA==
::dhA7pRFwIByZRRnk
::Zh4grVQjdCyDJGyX8VAjFAhdXBSHAGyzEolPxM/W0/6IrUFQB+44dd3n3rHAKe0a+UCqYZki2ilWn8ZCCB5Ich2yUjs7pmIRumOIC9OZoBuyG0DH41M1ew==
::YB416Ek+Zm8=
::
::
::978f952a14a936cc963da21a135fa983
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
