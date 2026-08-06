# ============================================================
#  Kuikly OHOS Sync ohosArm64 Shared Library (PowerShell)
#
#  Usage:
#    .\script\syncOhosArm64Shared.ps1              debug build
#    .\script\syncOhosArm64Shared.ps1 -Release     release build
#    .\script\syncOhosArm64Shared.ps1 -Clean       clean only
#    .\script\syncOhosArm64Shared.ps1 -Clean -Release  clean then release
# ============================================================

param(
    [switch]$Debug = $false,
    [switch]$Release = $false,
    [switch]$Clean = $false
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path $PSScriptRoot -Parent

# --- Setup OHOS environment variables (if not already set) ---
if (-not $env:OHOS_SDK_HOME) {
    $sdkGuess = "E:\IDE\DevEco Studio\sdk\default\openharmony"
    if (Test-Path $sdkGuess) {
        $env:OHOS_SDK_HOME = $sdkGuess
        $env:DEVECO_STUDIO_HOME = "E:\IDE\DevEco Studio"
    }
}

$BuildType = if ($Release) { "release" } else { "debug" }
$OhosSettingsFile = "$ProjectRoot\settings.ohos.gradle.kts"
$Gradlew = "$ProjectRoot\gradlew.bat"
$SoName = "libshared.so"
$SoOutputBase = "$ProjectRoot\shared\build\bin\ohosArm64"
$SoVariant = if ($Release) { "releaseShared" } else { "debugShared" }
$SoSource = "$SoOutputBase\$SoVariant\$SoName"
$SoDestDir = "$ProjectRoot\ohosApp\entry\libs\arm64-v8a"
$AssetsSrc = "$ProjectRoot\shared\src\commonMain\assets"
$AssetsDest = "$ProjectRoot\ohosApp\entry\src\main\resources\resfile"

function Invoke-GradleSync {
    param([string]$Task)
    Write-Host "[harmony] gradlew -c $OhosSettingsFile $Task" -ForegroundColor Cyan
    & $Gradlew "-c" $OhosSettingsFile $Task "--no-daemon"
    if ($LASTEXITCODE -ne 0) { throw "Gradle task failed: $Task (exit $LASTEXITCODE)" }
}

Write-Host ""
Write-Host "=========================================="
Write-Host "  Kuikly OHOS Sync ohosArm64 Shared ($BuildType)"
Write-Host "=========================================="
Write-Host "[Kuikly] Working dir: $ProjectRoot"

# --- Clean (optional) ---
if ($Clean) {
    Write-Host ""
    Write-Host "[clean] Cleaning OHOS artifacts..." -ForegroundColor Yellow
    Invoke-GradleSync ":harmonyClean"
    Write-Host "  [OK] Clean complete" -ForegroundColor Green
    if (-not $Release -and -not $Debug) { exit 0 }
}

# --- Build OHOS ---
Write-Host ""
Write-Host "[build] Compiling ohosArm64 ($BuildType)..." -ForegroundColor Yellow
$LinkTask = if ($Release) { ":shared:linkReleaseSharedOhosArm64" } else { ":shared:linkDebugSharedOhosArm64" }
Invoke-GradleSync $LinkTask
Write-Host "  [OK] Compile complete" -ForegroundColor Green

# --- Copy libshared.so ---
if (Test-Path $SoSource) {
    if (-not (Test-Path $SoDestDir)) { New-Item -ItemType Directory -Path $SoDestDir -Force | Out-Null }
    Copy-Item $SoSource $SoDestDir -Force
    Write-Host "  [OK] $SoName -> $SoDestDir" -ForegroundColor Green
} else {
    throw "Artifact not found: $SoSource"
}

# --- Copy libshared_api.h (KMP C 互操作头文件) ---
$HeaderSource = Join-Path (Split-Path $SoSource) "libshared_api.h"
$HeaderDestDir = "$ProjectRoot\ohosApp\entry\src\main\cpp\include"
if (Test-Path $HeaderSource) {
    if (-not (Test-Path $HeaderDestDir)) { New-Item -ItemType Directory -Path $HeaderDestDir -Force | Out-Null }
    Copy-Item $HeaderSource $HeaderDestDir -Force
    Write-Host "  [OK] libshared_api.h -> $HeaderDestDir" -ForegroundColor Green
} else {
    Write-Host "  [WARN] 未找到头文件: $HeaderSource（跳过）" -ForegroundColor Yellow
}

# --- Copy assets ---
if ((Test-Path $AssetsSrc) -and (Get-ChildItem $AssetsSrc -ErrorAction SilentlyContinue)) {
    if (-not (Test-Path $AssetsDest)) { New-Item -ItemType Directory -Path $AssetsDest -Force | Out-Null }
    Copy-Item "$AssetsSrc\*" $AssetsDest -Recurse -Force
    Write-Host "  [OK] assets -> $AssetsDest" -ForegroundColor Green
}

Write-Host ""
Write-Host "=========================================="
Write-Host "  [OK] OHOS Sync Complete" -ForegroundColor Green
Write-Host "=========================================="
Write-Host ""
Write-Host "Next: .\script\assembleOhosHap.ps1"
