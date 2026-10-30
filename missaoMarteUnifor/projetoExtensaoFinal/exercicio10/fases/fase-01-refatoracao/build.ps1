param(
    [switch]$Run
)

$ErrorActionPreference = 'Stop'

if (Test-Path out) {
    Remove-Item out -Recurse -Force
}
New-Item -ItemType Directory -Path out | Out-Null

$javaFiles = Get-ChildItem -Recurse src -Filter '*.java' | ForEach-Object { $_.FullName }
if (-not $javaFiles -or $javaFiles.Count -eq 0) {
    Write-Error 'Nenhum arquivo .java encontrado em src.'
    exit 1
}

Write-Output '[COMPILE] javac -encoding UTF-8 -d out <lista_arquivos>'
javac -encoding UTF-8 -d out $javaFiles
$compileExit = $LASTEXITCODE
Write-Output "[COMPILE_EXIT_CODE] $compileExit"
if ($compileExit -ne 0) {
    exit $compileExit
}

if ($Run) {
    Write-Output '[RUN] java -cp out br.edu.unifor.missaomarte.Main'
    java -cp out br.edu.unifor.missaomarte.Main
    $runExit = $LASTEXITCODE
    Write-Output "[RUN_EXIT_CODE] $runExit"
    exit $runExit
}
