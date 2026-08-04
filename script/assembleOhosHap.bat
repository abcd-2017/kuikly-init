@echo off
chcp 65001 >nul 2>&1
setlocal enabledelayedexpansion

REM ============================================================
REM  Kuikly OHOS HAP 打包脚本 (Windows)
REM  职责: ohpm install -> hvigor sync -> assembleHap -> 安装启动
REM
REM  前置: 需先执行 script\syncOhosArm64Shared.bat 完成构建
REM ============================================================

echo.
echo ==========================================
echo   Kuikly OHOS HAP 打包
echo ==========================================

REM --- 解析参数 ---
set SDK_HOME=
set SKIP_INSTALL=false

:parse_args
if "%~1"=="" goto args_done
if /i "%~1"=="--no-install" (set SKIP_INSTALL=true& shift& goto parse_args)
if /i "%~1"=="-h" goto usage
if /i "%~1"=="--help" goto usage
if not defined SDK_HOME (
    set SDK_HOME=%~1
)
shift
goto parse_args
:args_done

REM --- 猜测 SDK 路径 (若未通过参数传入) ---
if not defined SDK_HOME (
    if defined DEVECO_SDK_HOME (
        set SDK_HOME=%DEVECO_SDK_HOME%
    ) else if exist "C:\Program Files\Huawei\DevEco Studio" (
        set SDK_HOME=C:\Program Files\Huawei\DevEco Studio
    ) else (
        echo [ERROR] 未找到 DevEco Studio SDK
        echo 用法: %~nx0 [--no-install] [DevEco Studio 路径]
        echo 示例: %~nx0 "C:\Program Files\Huawei\DevEco Studio"
        exit /b 1
    )
)

if not exist "%SDK_HOME%" (
    echo [ERROR] DevEco Studio 路径不存在: %SDK_HOME%
    exit /b 1
)

echo [Kuikly] SDK_HOME = %SDK_HOME%

REM --- 设置环境变量 ---
set DEVECO_SDK_HOME=%SDK_HOME%\sdk
set PATH=%DEVECO_SDK_HOME%;%SDK_HOME%\jbr\Contents\Home\bin;%SDK_HOME%\tools\node\bin;%SDK_HOME%\tools\ohpm\bin;%SDK_HOME%\tools\hvigor\bin;%PATH%

REM --- 切换到 ohosApp 目录 ---
cd /d "%~dp0..\ohosApp"
echo [Kuikly] 工作目录: %CD%

REM --- 第一步: 安装依赖 ---
echo.
echo [1/4] 安装 ohosApp 依赖 (ohpm install)...
call ohpm install --all
if errorlevel 1 (
    echo [ERROR] ohpm install 失败
    exit /b 1
)
echo   ✓ 依赖安装完成

REM --- 第二步: Hvigor 同步 ---
echo.
echo [2/4] Hvigor 同步项目...
call node "%SDK_HOME%\tools\hvigor\bin\hvigorw.js" --sync -p product=default --analyze=normal --parallel
if errorlevel 1 (
    echo [ERROR] Hvigor 同步失败
    exit /b 1
)
echo   ✓ 项目同步完成

REM --- 第三步: 打包 HAP ---
echo.
echo [3/4] 打包 HAP...
call node "%SDK_HOME%\tools\hvigor\bin\hvigorw.js" --mode module -p module=entry@default -p product=default -p requiredDeviceType=phone assembleHap --analyze=normal --parallel
if errorlevel 1 (
    echo [ERROR] HAP 打包失败
    exit /b 1
)
echo.
echo   ✓ HAP 打包完成
echo   输出路径: %CD%\entry\build\default\outputs\default

REM --- 第四步: 安装到设备 ---
if "%SKIP_INSTALL%"=="true" (
    echo.
    echo [4/4] 跳过安装 (--no-install)
    goto :done
)

echo.
echo [4/4] 安装到设备...
set HDC_BIN=%SDK_HOME%\sdk\default\openharmony\toolchains\hdc
set HAP_PATH=%CD%\entry\build\default\outputs\default

if not exist "%HDC_BIN%" (
    echo   ✗ 未找到 hdc 工具: %HDC_BIN%
    exit /b 1
)

REM 检查是否有设备连接
set "TARGETS="
for /f "usebackq tokens=*" %%i in (`"%HDC_BIN%" list targets 2^>nul`) do (
    set "TARGETS=%%i"
    goto :have_targets
)
:have_targets

if not defined TARGETS (
    echo   ! 未检测到设备/模拟器，跳过安装
    goto :done
)

REM 签名检查
if exist "%HAP_PATH%\entry-default-unsigned.hap" (
    if not exist "%HAP_PATH%\entry-default-signed.hap" (
        echo   ! 存在未签名 HAP，请先配置签名
        echo    参考: https://developer.huawei.com/consumer/cn/doc/HMSCore-Guides/harmonyos-java-config-app-signing-0000001199536987
        exit /b 1
    )
)

REM 安装并启动
for /f "usebackq tokens=*" %%t in (`"%HDC_BIN%" list targets`) do (
    echo    -^> 安装到设备: %%t
    "%HDC_BIN%" -t "%%t" shell aa force-stop com.kuikly.init 2>nul
    "%HDC_BIN%" -t "%%t" install "%HAP_PATH%\entry-default-signed.hap"
    "%HDC_BIN%" -t "%%t" shell aa start -a EntryAbility -b com.kuikly.init
)

:done
echo.
echo ==========================================
echo   ✓ 全部完成
echo ==========================================
endlocal
exit /b 0

:usage
echo 用法: %~nx0 [--no-install] [DevEco Studio 路径]
echo 示例: %~nx0 "C:\Program Files\Huawei\DevEco Studio"
exit /b 0
