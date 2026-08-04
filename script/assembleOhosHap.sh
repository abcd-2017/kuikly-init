#!/bin/sh

# ============================================================
#  Kuikly OHOS 打包脚本 (Unix / macOS)
#  职责: ohpm install → hvigor sync → assembleHap → 安装启动
#
#  前置: 需先执行 ./script/syncOhosArm64Shared.sh 完成构建
#
#  用法:
#    ./script/package-ohos.sh                        # 使用默认 SDK 路径
#    ./script/package-ohos.sh /path/to/DevEco-Studio # 指定 SDK 路径
#    ./script/package-ohos.sh --no-install          # 仅打包，不安装到设备
# ============================================================

set -e

# --- 解析参数 ---
SDK_HOME=""
SKIP_INSTALL=false

while [ $# -gt 0 ]; do
    case "$1" in
        --no-install) SKIP_INSTALL=true; shift ;;
        -h|--help)
            echo "用法: $0 [--no-install] [DevEco Studio 路径]"
            exit 0
            ;;
        *) SDK_HOME="$1"; shift ;;
    esac
done

# --- 猜测 DevEco Studio 默认路径 ---
if [ -z "$SDK_HOME" ]; then
    case "$(uname -s)" in
        Darwin) DEFAULT_SDK="/Applications/DevEco-Studio.app/Contents" ;;
        Linux)  DEFAULT_SDK="$HOME/DevEco-Studio" ;;
        *)      DEFAULT_SDK="" ;;
    esac
    SDK_HOME="$DEFAULT_SDK"
fi

if [ ! -d "$SDK_HOME" ]; then
    echo "[ERROR] DevEco Studio 路径不存在: $SDK_HOME"
    echo "用法: $0 [DevEco Studio 安装路径]"
    echo "macOS 示例: $0 /Applications/DevEco-Studio.app/Contents"
    exit 1
fi

echo ""
echo "=========================================="
echo "  Kuikly OHOS 打包"
echo "  SDK: $SDK_HOME"
echo "=========================================="

# --- 设置环境变量 ---
export DEVECO_SDK_HOME="$SDK_HOME/sdk"
export PATH="$DEVECO_SDK_HOME:$SDK_HOME/jbr/Contents/Home/bin:$SDK_HOME/tools/node/bin:$SDK_HOME/tools/ohpm/bin:$SDK_HOME/tools/hvigor/bin:$PATH"

# --- 切换到 ohosApp 目录 ---
cd "$(dirname "$0")/../ohosApp"
echo "[Kuikly] 工作目录: $(pwd)"

# --- 第一步: 安装依赖 ---
echo ""
echo "[1/4] 安装 ohosApp 依赖 (ohpm install)..."
"$SDK_HOME/tools/ohpm/bin/ohpm" install --all
echo "  ✓ 依赖安装完成"

# --- 第二步: Hvigor 同步 ---
echo ""
echo "[2/4] Hvigor 同步项目..."
"$SDK_HOME/tools/node/bin/node" "$SDK_HOME/tools/hvigor/bin/hvigorw.js" \
    --sync -p product=default --analyze=normal --parallel
echo "  ✓ 项目同步完成"

# --- 第三步: 打包 HAP ---
echo ""
echo "[3/4] 打包 HAP..."
"$SDK_HOME/tools/node/bin/node" "$SDK_HOME/tools/hvigor/bin/hvigorw.js" \
    --mode module -p module=entry@default -p product=default -p requiredDeviceType=phone \
    assembleHap --analyze=normal --parallel

HAP_PATH="$(pwd)/entry/build/default/outputs/default"
echo ""
echo "  ✓ HAP 打包完成"
echo "  输出路径: $HAP_PATH"

# --- 第四步: 安装到设备 ---
if [ "$SKIP_INSTALL" = true ]; then
    echo ""
    echo "[4/4] 跳过安装 (--no-install)"
    echo ""
    echo "=========================================="
    echo "  ✓ 打包流程结束"
    echo "=========================================="
    exit 0
fi

echo ""
echo "[4/4] 安装到设备..."

HDC_BIN="$SDK_HOME/sdk/default/openharmony/toolchains/hdc"
if [ ! -f "$HDC_BIN" ]; then
    echo "  ✗ 未找到 hdc 工具: $HDC_BIN"
    exit 1
fi

targets=$("$HDC_BIN" list targets 2>/dev/null || true)

if [ -z "$targets" ]; then
    echo "  ! 未检测到设备/模拟器，跳过安装"
    echo ""
    echo "=========================================="
    echo "  ✓ 打包流程结束（未安装）"
    echo "=========================================="
    exit 0
fi

# 签名检查
if [ -e "$HAP_PATH/entry-default-unsigned.hap" ] && [ ! -e "$HAP_PATH/entry-default-signed.hap" ]; then
    echo "  ! 存在未签名 HAP，请先配置签名"
    echo "   参考: https://developer.huawei.com/consumer/cn/doc/HMSCore-Guides/harmonyos-java-config-app-signing-0000001199536987"
    exit 0
fi

# 安装并启动
for target_id in $targets; do
    echo "   -> 安装到设备: $target_id"
    "$HDC_BIN" -t "$target_id" shell aa force-stop com.kuikly.init 2>/dev/null || true
    "$HDC_BIN" -t "$target_id" install "$HAP_PATH/entry-default-signed.hap"
    "$HDC_BIN" -t "$target_id" shell aa start -a EntryAbility -b com.kuikly.init
done

echo ""
echo "=========================================="
echo "  ✓ 全部完成"
echo "=========================================="
