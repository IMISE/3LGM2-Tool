DEL ..\..\..\..\Tool3lgm\3lgm2tool.exe /s /q
CALL Bat_To_Exe_Converter.exe -bat ..\..\..\Tool3lgm\3lgm2tool.bat -save ..\..\..\Tool3lgm\3lgm2tool.exe -icon ..\..\Icons\toolIcon_gross.ico -invisible

Zur Info (Unterschiede 1.6 vs. 3.0):

"Bat_To_Exe_Converter_v1.6\Windows (32 bit)"\Bat_To_Exe_Converter.exe -bat ..\Tool3lgm\3lgm2tool.bat -save ..\Tool3lgm\test3.exe -icon Icons\toolIcon_gross.ico -invisble -overide -attributes -display -fileversion "1,0,0,0" -productversion "1,0,0,0" -productname "3lgm2-tool"

Bat_To_Exe_Converter_v3.0.10\Bat_To_Exe_Converter_(x64).exe /bat 3lgm2tool.bat /exe test3.exe /icon ..\DeployScriptsAndTools\Icons\toolIcon_gross.ico /invisble /overide /attributes /display /fileversion "1.0.0.0" /productversion "4.1.1dev" /productname "3lgm2-tool"