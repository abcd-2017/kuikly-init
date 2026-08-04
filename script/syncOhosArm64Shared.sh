#!/bin/sh

# ============================================================
#  Kuikly OHOS 构建脚本 (Unix / macOS)
#  职责: clean → 通过 Gradle harmony 任务编译 + 复制产物
#
#  用法:
#    ./script/build-ohos.sh              # debug 构建
#    ./script/build-ohos.sh --release    # release 构建
#    ./script/build-ohos.sh --clean      # 仅清理
# ============================================================

set -e

# --- 解析参数 ---
BUILD_TYPE="debug"
DO_CLEAN=false
ORIGINAL_ARGC=$#

while [ $# -gt 0 ]; do
    case "$1" in
        --release) BUILD_TYPE="release"; shift ;;
        --debug)   BUILD_TYPE="debug"; shift ;;
        --clean)   DO_CLEAN=true; shift ;;
        -h|--help)
            echo "用法: $0 [--debug|--release|--clean]"
            exit 0
            ;;
        *) echo "未知参数: $1"; exit 1 ;;
    esac
done

# --- 切换到项目根目录 ---
cd "$(dirname "$0")/.."

echo ""
echo "=========================================="
echo "  Kuikly OHOS 同步 ohosArm64 Shared (${BUILD_TYPE})"
echo "=========================================="
echo "[Kuikly] 工作目录: $(pwd)"

# --- 清理 (可选) ---
if [ "$DO_CLEAN" = true ]; then
    echo ""
    echo "[clean] 清理 OHOS 产物..."
    ./gradlew :harmonyClean --no-daemon
    echo "  ✓ 清理完成"
    # 仅传入 --clean 时，清理后退出（不构建）
    if [ "$ORIGINAL_ARGC" -eq 1 ]; then
        exit 0
    fi
fi

# --- 通过 Gradle 任务构建 ---
echo ""
echo "[build] 执行 Gradle harmony 任务..."
if [ "$BUILD_TYPE" = "release" ]; then
    ./gradlew :harmonySyncRelease --no-daemon
else
    ./gradlew :harmonySyncDebug --no-daemon
fi

echo ""
echo "=========================================="
echo "  ✓ OHOS 构建完成"
echo "  产物:"
echo "    libshared.so → ohosApp/entry/libs/arm64-v8a/"
echo "    assets/*     → ohosApp/entry/src/main/resources/resfile/"
echo "=========================================="
echo ""
echo "下一步: ./script/assembleOhosHap.sh"
