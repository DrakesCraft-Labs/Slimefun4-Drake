$ErrorActionPreference = "Stop"

mvn clean package "-Dmaven.test.skip=true"

$jar = Get-ChildItem -Path (Join-Path $PSScriptRoot "target") -Filter "*.jar" |
    Where-Object { $_.Name -notlike "*sources.jar" -and $_.Name -notlike "*original-*" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if ($null -eq $jar) {
    throw "No se encontro el jar final en target"
}

Write-Host "Build listo:" $jar.FullName
