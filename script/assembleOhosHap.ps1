# ============================================================
#  Kuikly OHOS HAP Package Script (PowerShell)
#
#  Usage:
#    .\script\assembleOhosHap.ps1                           use default SDK path
#    .\script\assembleOhosHap.ps1 -SdkPath "E:\IDE\..."     specify SDK path
#    .\script\assembleOhosHap.ps1 -NoInstall                build only, do not install
# ============================================================

param(
    [string]$SdkPath = "",
    [switch]$NoInstall = $false
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path $PSScriptRoot -Parent
$OhosAppDir = "$ProjectRoot\ohosApp"

# --- Resolve SDK path ---
# 优先使用 DEVECO_STUDIO_HOME（DevEco Studio 安装根目录）
# 其次从 DEVECO_SDK_HOME 反推（它指向 studio/sdk/）
if (-not $SdkPath) {
    if ($env:DEVECO_STUDIO_HOME) {
        $SdkPath = $env:DEVECO_STUDIO_HOME
    } elseif ($env:DEVECO_SDK_HOME) {
        # DEVECO_SDK_HOME 指向 studio/sdk/，退一级得到 studio/
        $SdkPath = Split-Path $env:DEVECO_SDK_HOME -Parent
    } elseif (Test-Path "C:\Program Files\Huawei\DevEco Studio") {
        $SdkPath = "C:\Program Files\Huawei\DevEco Studio"
    } else {
        throw @"
DevEco Studio SDK not found.
Usage: $($MyInvocation.MyCommand.Name) -SdkPath "E:\IDE\DevEco Studio"
Or set env: `$env:DEVECO_STUDIO_HOME
"@
    }
}

if (-not (Test-Path $SdkPath)) { throw "DevEco Studio path not found: $SdkPath" }

# --- Setup environment ---
$env:DEVECO_SDK_HOME = "$SdkPath\sdk"
$SdkTools = @(
    "$env:DEVECO_SDK_HOME"
    "$SdkPath\tools\node\bin"
    "$SdkPath\tools\ohpm\bin"
    "$SdkPath\tools\hvigor\bin"
    "$env:DEVECO_SDK_HOME\default\openharmony\toolchains"
)
$env:PATH = ($SdkTools -join ";") + ";" + $env:PATH

$Ohpm = "$SdkPath\tools\ohpm\bin\ohpm"
$Hvigor = "$SdkPath\tools\node\bin\node"
$HvigorScript = "$SdkPath\tools\hvigor\bin\hvigorw.js"
$Hdc = "$env:DEVECO_SDK_HOME\default\openharmony\toolchains\hdc.exe"

function Invoke-HvigorCommand {
    param([string[]]$Args)
    & $Hvigor $HvigorScript @Args
    if ($LASTEXITCODE -ne 0) { throw "hvigor failed (exit $LASTEXITCODE)" }
}

Write-Host ""
Write-Host "=========================================="
Write-Host "  Kuikly OHOS HAP Package"
Write-Host "  SDK: $SdkPath"
Write-Host "=========================================="
Write-Host "[Kuikly] Working dir: $OhosAppDir"
Set-Location $OhosAppDir

# --- Step 1: Install deps ---
Write-Host ""
Write-Host "[1/4] Installing ohosApp deps (ohpm install)..." -ForegroundColor Yellow
& $Ohpm install --all
if ($LASTEXITCODE -ne 0) { throw "ohpm install failed" }
Write-Host "  [OK] Deps installed" -ForegroundColor Green

# --- Step 2: Hvigor sync ---
Write-Host ""
Write-Host "[2/4] Hvigor sync..." -ForegroundColor Yellow
Invoke-HvigorCommand @("--sync", "-p", "product=default", "--analyze=normal", "--parallel")
Write-Host "  [OK] Sync complete" -ForegroundColor Green

# --- Step 3: Build HAP ---
Write-Host ""
Write-Host "[3/4] Building HAP..." -ForegroundColor Yellow
Invoke-HvigorCommand @("--mode", "module", "-p", "module=entry@default", "-p", "product=default", "-p", "requiredDeviceType=phone", "assembleHap", "--analyze=normal", "--parallel")

$HAP_PATH = "$OhosAppDir\entry\build\default\outputs\default"
Write-Host ""
Write-Host "  [OK] HAP built" -ForegroundColor Green
Write-Host "  Output: $HAP_PATH"

# --- Step 4: Install to device ---
if ($NoInstall) {
    Write-Host ""
    Write-Host "[4/4] Skipping install (--no-install)" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "=========================================="
    Write-Host "  [OK] Package complete" -ForegroundColor Green
    Write-Host "=========================================="
    exit 0
}

Write-Host ""
Write-Host "[4/4] Installing to device..." -ForegroundColor Yellow

if (-not (Test-Path $Hdc)) { throw "hdc not found: $Hdc" }

$targets = & $Hdc list targets 2>$null
if (-not $targets) {
    Write-Host "  [WARN] No device/emulator detected, skipping install" -ForegroundColor Yellow
    exit 0
}

# Signature check
if ((Test-Path "$HAP_PATH\entry-default-unsigned.hap") -and -not (Test-Path "$HAP_PATH\entry-default-signed.hap")) {
    Write-Host "  [ERROR] Unsigned HAP found, configure signing first" -ForegroundColor Red
    Write-Host "   Ref: https://developer.huawei.com/consumer/cn/doc/HMSCore-Guides/harmonyos-java-config-app-signing-0000001199536987"
    exit 1
}

foreach ($target in $targets) {
    Write-Host "   -> Installing to: $target" -ForegroundColor Cyan
    & $Hdc -t $target shell aa force-stop com.kuikly.init 2>$null
    & $Hdc -t $target install "$HAP_PATH\entry-default-signed.hap"
    & $Hdc -t $target shell aa start -a EntryAbility -b com.kuikly.init
}

Write-Host ""
Write-Host "=========================================="
Write-Host "  [OK] All done" -ForegroundColor Green
Write-Host "=========================================="
