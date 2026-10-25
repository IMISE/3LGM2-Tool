[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$StagingDirectory,
    [Parameter(Mandatory = $true)][string]$OutputDirectory,
    [Parameter(Mandatory = $true)][string]$Version
)

$ErrorActionPreference = 'Stop'
$name = "3LGM2-Tool_v$Version"
$numericVersion = ($Version -split '-')[0]
$work = Join-Path ([System.IO.Path]::GetTempPath()) "3lgm2-jpackage-$([guid]::NewGuid())"
New-Item -ItemType Directory -Force -Path $work, $OutputDirectory | Out-Null
try {
    $input = Join-Path $work 'input'
    New-Item -ItemType Directory -Force -Path $input | Out-Null
    Copy-Item "$StagingDirectory\lib\tool3lgm.jar" "$input\tool3lgm.jar"
    Copy-Item "$StagingDirectory\splash.gif" "$input\splash.gif"
    foreach ($item in @('lib', 'Plugins', 'Templates', 'Examples', 'Symbols', 'Dok_Auswertungen', 'XSLT-Beispiele', 'thirdPartyLicense.html', 'LICENSE-2.0.txt', 'version.info')) {
        Copy-Item (Join-Path $StagingDirectory $item) $input -Recurse
    }

    $appImage = Join-Path $work 'image'
    $common = @(
        '--name', '3LGM2-Tool', '--app-version', $numericVersion,
        '--vendor', 'University Leipzig - IMISE',
        '--input', $input, '--main-jar', 'tool3lgm.jar',
        '--main-class', 'de.imise.tool3lgm.Tool3lgmMain',
        '--java-options', '-cp $APPDIR\lib\*;$APPDIR\Plugins\*',
        '--java-options', '-splash:$APPDIR\splash.gif',
        '--java-options', '-Xmx768m', '--java-options', '-Xss32m'
    )
    & jpackage --type app-image --dest $appImage @common
    if ($LASTEXITCODE -ne 0) { throw 'jpackage could not create the Windows application image.' }

    Compress-Archive -Path (Join-Path $appImage '3LGM2-Tool') -DestinationPath (Join-Path $OutputDirectory "${name}_JRE.zip") -Force
    & jpackage --type exe --dest $OutputDirectory --name "${name}_JRE" --app-image (Join-Path $appImage '3LGM2-Tool')
    if ($LASTEXITCODE -ne 0) { throw 'jpackage could not create the Windows installer.' }
    $generatedInstaller = Get-ChildItem -Path $OutputDirectory -Filter '*.exe' | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if (-not $generatedInstaller) { throw 'jpackage did not produce a Windows installer.' }
    Move-Item $generatedInstaller.FullName (Join-Path $OutputDirectory "${name}_JRE_setup.exe") -Force
}
finally {
    Remove-Item -Recurse -Force -ErrorAction SilentlyContinue $work
}
