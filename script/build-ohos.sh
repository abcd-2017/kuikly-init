#!/bin/sh

# ============================================================
#  Kuikly OHOS 编译打包脚本 (Unix / macOS)
#  流程: Gradle 构建 shared 模块(libshared.so) → Hvigor 打包 HAP
#
#  用法:
#    ./script/build-ohos.sh                        # 使用默认 SDK 路径
#    ./script/build-ohos.sh /path/to/DevEco-Studio # 指定 SDK 路径
# ============================================================

set -e

# --- 解析参数: 可选的 DevEco Studio 安装路径 ---
SDK_HOME="$1"

if [ -z "$SDK_HOME" ]; then
    # 按操作系统猜测默认路径
    case "$(uname -s)" in
        Darwin)
            DEFAULT_SDK="/Applications/DevEco-Studio.app/Contents"
            ;;
        Linux)
            DEFAULT_SDK="$HOME/DevEco-Studio"
            ;;
        *)
            echo "[ERROR] 无法识别的操作系统，请通过参数指定 DevEco Studio 路径"
            echo "用法: $0 /path/to/DevEco-Studio"
            exit 1
            ;;
    esac
    SDK_HOME="$DEFAULT_SDK"
fi

if [ ! -d "$SDK_HOME" ]; then
    echo "[ERROR] DevEco Studio 路径不存在: $SDK_HOME"
    echo "用法: $0 [DevEco Studio 安装路径]"
    echo "macOS 示例: $0 /Applications/DevEco-Studio.app/Contents"
    echo "Linux 示例: $0 \$HOME/DevEco-Studio"
    exit 1
fi

echo ""
echo "[Kuikly-OHOS] SDK_HOME = $SDK_HOME"

# --- 设置环境变量 ---
export DEVECO_SDK_HOME="$SDK_HOME/sdk"
export PATH="$DEVECO_SDK_HOME:$SDK_HOME/jbr/Contents/Home/bin:$SDK_HOME/tools/node/bin:$SDK_HOME/tools/ohpm/bin:$SDK_HOME/tools/hvigor/bin:$PATH"

# --- 切换到项目根目录 ---
cd "$(dirname "$0")/.."
echo "[Kuikly-OHOS] 工作目录: $(pwd)"

# --- 第一步: Gradle 构建 shared 模块 (OHOS) ---
echo ""
echo "[1/4] Gradle 构建 shared 模块 (ohosArm64)..."
./gradlew :shared:build -PohosGradleSettings=settings.ohos.gradle.kts --no-daemon
echo "[OK] shared 模块构建完成"

# --- 第二步: 安装 ohosApp 依赖 ---
echo ""
echo "[2/4] 安装 ohosApp 依赖 (ohpm install)..."
cd ohosApp
"$SDK_HOME/tools/ohpm/bin/ohpm" install --all
echo "[OK] 依赖安装完成"

# --- 第三步: Hvigor 同步项目 ---
echo ""
echo "[3/4] Hvigor 同步项目..."
"$SDK_HOME/tools/node/bin/node" "$SDK_HOME/tools/hvigor/bin/hvigorw.js" \
    --sync -p product=default --analyze=normal --parallel
echo "[OK] 项目同步完成"

# --- 第四步: 打包 HAP ---
echo ""
echo "[4/4] 打包 HAP..."
"$SDK_HOME/tools/node/bin/node" "$SDK_HOME/tools/hvigor/bin/hvigorw.js" \
    --mode module -p module=entry@default -p product=default -p requiredDeviceType=phone \
    assembleHap --analyze=normal --parallel

echo ""
echo "[OK] HAP 打包完成"
echo "[Kuikly-OHOS] 输出路径: $(pwd)/entry/build/default/outputs/default"

# --- 可选: 安装到设备 ---
HDC_BIN="$SDK_HOME/sdk/default/openharmony/toolchains/hdc"
HAP_PATH="entry/build/default/outputs/default"

targets=$("$HDC_BIN" list targets 2>/dev/null || true)

if [ -z "$targets" ]; then
    echo ""
    echo "[WARN] 未检测到设备/模拟器，跳过安装"
    echo "[Kuikly-OHOS] 构建流程结束"
    exit 0
fi

if [ -e "$HAP_PATH/entry-default-unsigned.hap" ] && [ ! -e "$HAP_PATH/entry-default-signed.hap" ]; then
    echo ""
    echo "[WARN] 存在未签名 HAP，请先配置签名"
    echo "       参考: https://developer.huawei.com/consumer/cn/doc/HMSCore-Guides/harmonyos-java-config-app-signing-0000001199536987"
    exit 0
fi

echo ""
echo "[Install] 安装并启动 HAP..."
for target_id in $targets; do
    echo "   -> 安装到设备: $target_id"
    "$HDC_BIN" -t "$target_id" shell aa force-stop com.kuikly.init
    "$HDC_BIN" -t "$target_id" install "$HAP_PATH/entry-default-signed.hap"
    "$HDC_BIN" -t "$target_id" shell aa start -a EntryAbility -b com.kuikly.init
done

echo ""
echo "[Kuikly-OHOS] 全部完成"
