@echo off
chcp 65001 >nul 2>&1
setlocal enabledelayedexpansion

REM ============================================================
REM  Kuikly OHOS 同步 ohosArm64 Shared 库脚本 (Windows)
REM  职责: clean(可选) -> 通过 Gradle harmony 任务编译 + 复制产物
REM
REM  用法:
REM    script\syncOhosArm64Shared.bat              debug 构建
REM    script\syncOhosArm64Shared.bat --release    release 构建
REM    script\syncOhosArm64Shared.bat --clean      仅清理
REM    script\syncOhosArm64Shared.bat --clean --release  清理后构建 release
REM ============================================================

echo.
echo ==========================================
echo   Kuikly OHOS 同步 ohosArm64 Shared
echo ==========================================

REM --- 解析参数 ---
set BUILD_TYPE=debug
set DO_CLEAN=false
set ORIGINAL_ARGS=0

:parse_args
if "%~1"=="" goto args_done
set /a ORIGINAL_ARGS=ORIGINAL_ARGS+1
if /i "%~1"=="--release" (set BUILD_TYPE=release& shift& goto parse_args)
if /i "%~1"=="--debug"   (set BUILD_TYPE=debug& shift& goto parse_args)
if /i "%~1"=="--clean"   (set DO_CLEAN=true& shift& goto parse_args)
if /i "%~1"=="-h" goto usage
if /i "%~1"=="--help" goto usage
echo 未知参数: %~1
exit /b 1
:parse_args

echo [Kuikly] 构建类型: %BUILD_TYPE%
echo [Kuikly] 工作目录: %~dp0..
cd /d "%~dp0.."

REM --- 清理 ---
if "%DO_CLEAN%"=="true" (
    echo.
    echo [clean] 清理 OHOS 产物...
    call gradlew.bat :harmonyClean --no-daemon
    if errorlevel 1 exit /b 1
    echo   ✓ 清理完成
    REM 仅传入 --clean 时，清理后退出
    if "%ORIGINAL_ARGS%"=="1" (
        endlocal
        exit /b 0
    )
)

REM --- 通过 Gradle 任务构建 ---
echo.
echo [build] 执行 Gradle harmony 任务...
if /i "%BUILD_TYPE%"=="release" (
    call gradlew.bat :harmonySyncRelease --no-daemon
) else (
    call gradlew.bat :harmonySyncDebug --no-daemon
)
if errorlevel 1 exit /b 1

echo.
echo ==========================================
echo   ✓ OHOS 构建完成
echo   产物:
echo     libshared.so → ohosApp\entry\libs\arm64-v8a\
echo     assets/*     → ohosApp\entry\src\main\resources\resfile\
echo ==========================================
echo.
echo 下一步: script\assembleOhosHap.bat
endlocal
exit /b 0

:usage
echo 用法: %~nx0 [--debug^|--release^|--clean]
echo.
echo 示例:
echo   script\syncOhosArm64Shared.bat                debug 构建
echo   script\syncOhosArm64Shared.bat --release      release 构建
echo   script\syncOhosArm64Shared.bat --clean        仅清理
echo   script\syncOhosArm64Shared.bat --clean --release  清理后 release 构建
exit /b 0
