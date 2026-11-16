# QUINTAVERSO v0.6: crear app-image Windows con JDK 21 y JavaFX desde Maven.
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

$mvnCmd = (Get-Command mvn.cmd -ErrorAction SilentlyContinue).Source
if (-not $mvnCmd) {
    $mvnCmd = 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd'
}
if (-not (Test-Path $mvnCmd)) {
    throw 'No se encontró Maven. Usa IntelliJ IDEA > Maven > Lifecycle > package o instala Maven.'
}
if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    throw 'jpackage no está en PATH. Selecciona un JDK 21 completo.'
}
Write-Host '=== Compilando QUINTAVERSO v0.6 ==='
& $mvnCmd clean package
if ($LASTEXITCODE -ne 0) { throw 'Falló Maven package. Revisa los tests.' }

& $mvnCmd dependency:copy-dependencies '-DincludeScope=runtime' '-DoutputDirectory=target/dependency'
if ($LASTEXITCODE -ne 0) { throw 'Falló Maven dependency:copy-dependencies.' }

$paquete = Join-Path $PSScriptRoot 'target\paquete'
$destino = Join-Path $PSScriptRoot 'distribucion'
New-Item -ItemType Directory -Force $destino | Out-Null
if (Test-Path $paquete) { Remove-Item -Recurse -Force $paquete }
New-Item -ItemType Directory -Force $paquete | Out-Null
Copy-Item 'target\QUINTAVERSO-0.6.0.jar' $paquete
Get-ChildItem 'target\dependency' -Filter '*.jar' |
    Where-Object { $_.Name -notmatch '^javafx-(base|controls|fxml|graphics|media)-[0-9.]+\.jar$' } |
    Copy-Item -Destination $paquete

$appDir = Join-Path $destino 'QUINTAVERSO'
if (Test-Path $appDir) { Remove-Item -Recurse -Force $appDir }
jpackage --type app-image --name QUINTAVERSO --app-version 0.6.0 `
    --dest $destino --module-path $paquete `
    --module com.example.quintaverso/com.example.quintaverso.HelloApplication --win-console
if ($LASTEXITCODE -ne 0) { throw 'Falló jpackage.' }

$archivoZip = Join-Path $destino 'QUINTAVERSO-v0.6-Windows.zip'
Compress-Archive -Path $appDir -DestinationPath $archivoZip -Force
Write-Host "Juego listo en $archivoZip"
Write-Host 'Prueba QUINTAVERSO.exe antes de publicar la nueva Release.'
