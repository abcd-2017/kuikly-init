@echo off
chcp 65001 >nul 2>&1
setlocal enabledelayedexpansion

REM ============================================================
REM  Kuikly OHOS 编译打包脚本 (Windows)
REM  流程: Gradle 构建 shared 模块(libshared.so) → Hvigor 打包 HAP
REM ============================================================

echo.
echo [Kuikly-OHOS] 开始构建...

REM --- 配置 DevEco Studio SDK 路径 ---
REM 默认安装路径，按需修改或通过命令行参数传入
setSDK_HOME=C:\Program Files\Huawei\DevEco Studio

if not "%~1"=="" set SDK_HOME=%~1
if not exist "%SDK_HOME%" (
    echo [ERROR] DevEco Studio 路径不存在: %SDK_HOME%
    echo 用法: %~nx0 [DevEco Studio 安装路径]
    echo 示例: %~nx0 "C:\Program Files\Huawei\DevEco Studio"
    exit /b 1
)

echo [Kuikly-OHOS] SDK_HOME = %SDK_HOME%

REM --- 设置环境变量 ---
set DEVECO_SDK_HOME=%SDK_HOME%\sdk
set PATH=%DEVECO_SDK_HOME%;%SDK_HOME%\jbr\Contents\Home\bin;%SDK_HOME%\tools\node\bin;%SDK_HOME%\tools\ohpm\bin;%SDK_HOME%\tools\hvigor\bin;%PATH%

REM --- 记录根目录 ---
set ROOT_DIR=%~dp0..
cd /d "%ROOT_DIR%"

echo [Kuikly-OHOS] 工作目录: %CD%

REM --- 第一步: Gradle 构建 shared 模块 (OHOS) ---
echo.
echo [1/4] Gradle 构建 shared 模块 (ohosArm64)...
call gradlew.bat :shared:build -PohosGradleSettings=settings.ohos.gradle.kts --no-daemon
if errorlevel 1 (
    echo [ERROR] Gradle 构建失败
    exit /b 1
)
echo [OK] shared 模块构建完成

REM --- 第二步: 进入 ohosApp 安装依赖 ---
echo.
echo [2/4] 安装 ohosApp 依赖 (ohpm install)...
cd ohosApp
call ohpm install --all
if errorlevel 1 (
    echo [ERROR] ohpm install 失败
    exit /b 1
)
echo [OK] 依赖安装完成

REM --- 第三步: Hvigor 同步项目 ---
echo.
echo [3/4] Hvigor 同步项目...
call node "%SDK_HOME%\tools\hvigor\bin\hvigorw.js" --sync -p product=default --analyze=normal --parallel
if errorlevel 1 (
    echo [ERROR] Hvigor 同步失败
    exit /b 1
)
echo [OK] 项目同步完成

REM --- 第四步: 打包 HAP ---
echo.
echo [4/4] 打包 HAP...
call node "%SDK_HOME%\tools\hvigor\bin\hvigorw.js" --mode module -p module=entry@default -p product=default -p requiredDeviceType=phone assembleHap --analyze=normal --parallel
if errorlevel 1 (
    echo [ERROR] HAP 打包失败
    exit /b 1
)
echo.
echo [OK] HAP 打包完成
echo [Kuikly-OHOS] 输出路径: %CD%\entry\build\default\outputs\default

REM --- 可选: 安装到设备 ---
echo.
set HDC_BIN=%SDK_HOME%\sdk\default\openharmony\toolchains\hdc
set HAP_PATH=entry\build\default\outputs\default

for /f "tokens=*" %%i in ('"%HDC_BIN%" list targets 2^>nul') do (
    set "TARGETS=%%i"
)

if not defined TARGETS (
    echo [WARN] 未检测到设备/模拟器，跳过安装
    echo [Kuikly-OHOS] 构建流程结束
    goto :eof
)

if exist "%HAP_PATH%\entry-default-unsigned.hap" (
    if not exist "%HAP_PATH%\entry-default-signed.hap" (
        echo [WARN] 存在未签名 HAP，请先配置签名
        echo        参考: https://developer.huawei.com/consumer/cn/doc/HMSCore-Guides/harmonyos-java-config-app-signing-0000001199536987
        goto :eof
    )
)

echo.
echo [Install] 安装并启动 HAP...
for /f "tokens=*" %%t in ('"%HDC_BIN%" list targets') do (
    echo    -^> 安装到设备: %%t
    "%HDC_BIN%" -t "%%t" shell aa force-stop com.kuikly.init
    "%HDC_BIN%" -t "%%t" install "%HAP_PATH%\entry-default-signed.hap"
    "%HDC_BIN%" -t "%%t" shell aa start -a EntryAbility -b com.kuikly.init
)

echo.
echo [Kuikly-OHOS] 全部完成
endlocal
