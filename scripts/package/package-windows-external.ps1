[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$StagingDirectory,
    [Parameter(Mandatory = $true)][string]$OutputDirectory,
    [Parameter(Mandatory = $true)][string]$Version,
    [Parameter(Mandatory = $true)][string]$RepositoryRoot
)

# Legacy compatibility package: it intentionally requires a locally installed
# Java runtime. JRE-bearing Windows packages are built with jpackage instead.
$ErrorActionPreference = 'Stop'
$work = Join-Path ([System.IO.Path]::GetTempPath()) "3lgm2-external-$([guid]::NewGuid())"
New-Item -ItemType Directory -Force -Path $work, $OutputDirectory | Out-Null
try {
    $deployTools = Join-Path $work 'DeployScriptsAndTools'
    $launch4j = Join-Path $deployTools 'launch4j'
    $app = Join-Path $work 'Tool3lgm'
    Copy-Item -Recurse -Force (Join-Path $RepositoryRoot 'de.imise.tool3lgm.deploy\DeployScriptsAndTools\launch4j') $launch4j
    Copy-Item -Recurse -Force (Join-Path $RepositoryRoot 'de.imise.tool3lgm.deploy\DeployScriptsAndTools\Icons') (Join-Path $deployTools 'Icons')
    Copy-Item -Recurse -Force $StagingDirectory $app

    $config = Join-Path $launch4j '3lgm2tool.cfg.xml'
    (Get-Content -Raw $config) -replace '<txtProductVersion>.*?</txtProductVersion>', "<txtProductVersion>$Version</txtProductVersion>" |
        Set-Content -NoNewline -Encoding utf8 $config
    Push-Location $launch4j
    try { & java -jar launch4j.jar 3lgm2tool.cfg.xml } finally { Pop-Location }
    if ($LASTEXITCODE -ne 0 -or -not (Test-Path (Join-Path $app '3lgm2tool.exe'))) { throw 'Launch4j could not create the external-Java launcher.' }

    $icon = Join-Path $deployTools 'Icons\toolIcon_gross.ico'
    $iss = @"
#define MyAppName "3LGM² Tool"
#define MyAppVersion "$Version"
#define MyAppExeName "3lgm2tool.exe"
[Setup]
AppId={{88B62F13-55E9-4E6B-96B2-9F2D0217F0FB}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher=University Leipzig - IMISE
AppPublisherURL=https://www.3lgm2.de
DefaultDirName={autopf}\3LGM2-Tool
DefaultGroupName={#MyAppName}
OutputDir=$OutputDirectory
OutputBaseFilename=3LGM2-Tool_v${Version}_setup
SetupIconFile=$icon
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
Compression=lzma
SolidCompression=yes
[Files]
Source: "$app\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon
[Tasks]
Name: "desktopicon"; Description: "Create a desktop icon"; Flags: unchecked
[Registry]
Root: "HKCR"; Subkey: ".3lgm"; ValueType: string; ValueData: "tool3lgm"; Flags: uninsdeletekey
Root: "HKCR"; Subkey: ".z3lgm"; ValueType: string; ValueData: "tool3lgm"; Flags: uninsdeletekey
Root: "HKCR"; Subkey: "tool3lgm\shell\open\command"; ValueType: string; ValueData: """{app}\{#MyAppExeName}"" ""%1"""
"@
    $issPath = Join-Path $work '3lgm2tool.iss'
    Set-Content -Path $issPath -Value $iss -Encoding utf8
    $iscc = (Get-Command ISCC.exe -ErrorAction SilentlyContinue).Source
    if (-not $iscc) { throw 'ISCC.exe is not on PATH. Install Inno Setup before running this script.' }
    & $iscc $issPath
    if ($LASTEXITCODE -ne 0) { throw 'Inno Setup could not create the external-Java installer.' }
}
finally {
    Remove-Item -Recurse -Force -ErrorAction SilentlyContinue $work
}
