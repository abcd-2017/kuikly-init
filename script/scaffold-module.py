#!/usr/bin/env python3
"""
============================================================
  Kuikly 脚手架 - 模块脚手架脚本
用法:
    python script/scaffold-module.py --type common --name util
    python script/scaffold-module.py --type business --name login
    python script/scaffold-module.py --type page --name settings
    python script/scaffold-module.py --type common --name util --delete
    python script/scaffold-module.py --type business --name login --delete
    python script/scaffold-module.py --type page --name settings --delete

说明:
    --type common   创建扁平结构模块，源码直接放在模块根目录下
    --type business 创建 api/impl 分离结构，适用于业务功能模块
    --type page     创建包含 Composable 页面的模块（全平台 + Kuikly + Compose）
    --delete        删除指定模块（取消注册 + 删除目录 + git rm）
============================================================
"""

import argparse
import os
import re
import shutil
import subprocess
import sys
from pathlib import Path

# ============================================================
#  全局配置
# ============================================================

# 项目根目录（脚本位于 script/，向上退一级）
PROJECT_ROOT = Path(__file__).resolve().parent.parent

# 关键文件路径
GRADLE_PROPERTIES = PROJECT_ROOT / "gradle.properties"
SETTINGS_KTS = PROJECT_ROOT / "settings.gradle.kts"
SETTINGS_OHOS_KTS = PROJECT_ROOT / "settings.ohos.gradle.kts"

# Koin 版本
KOIN_VERSION = "4.0.1"

# 创建的文件记录（用于 git add）
CREATED_FILES: list[Path] = []


# ============================================================
#  工具函数
# ============================================================

def read_package_name() -> str:
    """从 gradle.properties 读取包名"""
    if not GRADLE_PROPERTIES.exists():
        print(f"[ERROR] 找不到 {GRADLE_PROPERTIES}")
        sys.exit(1)

    with open(GRADLE_PROPERTIES, "r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if line.startswith("package.name="):
                return line.split("=", 1)[1].strip()

    print("[ERROR] gradle.properties 中未找到 package.name 配置")
    sys.exit(1)


def package_to_path(package: str) -> str:
    """将包名转换为路径，如 com.kuikly.init -> com/kuikly/init"""
    return package.replace(".", "/")


def make_dirs(path: Path) -> None:
    """递归创建目录"""
    path.mkdir(parents=True, exist_ok=True)


def write_file(path: Path, content: str) -> None:
    """写入文件（覆盖），并记录到 CREATED_FILES"""
    make_dirs(path.parent)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    CREATED_FILES.append(path)
    print(f"  创建: {path.relative_to(PROJECT_ROOT)}")


def write_if_not_exists(path: Path, content: str) -> None:
    """仅在文件不存在时写入"""
    if path.exists():
        print(f"  跳过（已存在）: {path.relative_to(PROJECT_ROOT)}")
        return
    write_file(path, content)


def git_add_files(files: list[Path]) -> None:
    """将文件添加到 git 暂存区"""
    if not files:
        return

    # 过滤掉不存在的文件
    existing = [str(f.relative_to(PROJECT_ROOT)) for f in files if f.exists()]
    if not existing:
        return

    try:
        subprocess.run(
            ["git", "add"] + existing,
            cwd=str(PROJECT_ROOT),
            check=True,
            capture_output=True,
        )
        print(f"  git add: {len(existing)} 个文件已暂存")
    except subprocess.CalledProcessError as e:
        print(f"  [WARN] git add 失败: {e.stderr.decode().strip()}")


def git_rm_files(paths: list[Path]) -> None:
    """从 git 和工作树中删除文件/目录"""
    if not paths:
        return

    # 过滤出仍存在于 git 中的路径
    existing = [str(p.relative_to(PROJECT_ROOT)) for p in paths if p.exists()]
    if not existing:
        return

    try:
        subprocess.run(
            ["git", "rm", "-r"] + existing,
            cwd=str(PROJECT_ROOT),
            check=True,
            capture_output=True,
        )
        print(f"  git rm: {len(existing)} 个路径已删除并暂存")
    except subprocess.CalledProcessError as e:
        print(f"  [WARN] git rm 失败: {e.stderr.decode().strip()}")


# ============================================================
#  模板: build.gradle.kts (标准构建)
# ============================================================

TEMPLATE_COMMON_BUILD = """\
plugins {{
    kotlin("multiplatform")
    id("com.android.library")
    id("com.tencent.kuiklybase.resource.generator")
}}

kotlin {{
    androidTarget {{
        compilations.all {{
            kotlinOptions {{
                jvmTarget = "1.8"
            }}
        }}
    }}

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {{
        val commonMain by getting {{
            dependencies {{
                implementation("io.insert-koin:koin-core:{koin_version}")
                implementation("com.tencent.kuiklybase:resource-core:0.0.1")
                implementation("com.tencent.kuiklybase:resource-compose:0.0.1")
            }}
        }}
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }}
    }}
}}

multiplatformResources {{
    multiplatformResourcesPackage = "{namespace}"
    multiplatformResourcesClassName = "{class_name}MR"
    multiplatformResourcesPrefix = "{module}_"
}}

android {{
    namespace = "{namespace}"
    compileSdk = 34
    defaultConfig {{
        minSdk = 21
    }}
}}
"""

TEMPLATE_BUSINESS_API_BUILD = """\
plugins {{
    kotlin("multiplatform")
    id("com.android.library")
    id("com.tencent.kuiklybase.resource.generator")
}}

kotlin {{
    androidTarget {{
        compilations.all {{
            kotlinOptions {{
                jvmTarget = "1.8"
            }}
        }}
    }}

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {{
        val commonMain by getting {{
            dependencies {{
                implementation("io.insert-koin:koin-core:{koin_version}")
                implementation("com.tencent.kuiklybase:resource-core:0.0.1")
                implementation("com.tencent.kuiklybase:resource-compose:0.0.1")
            }}
        }}
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }}
    }}
}}

multiplatformResources {{
    multiplatformResourcesPackage = "{namespace}"
    multiplatformResourcesClassName = "{class_name}MR"
    multiplatformResourcesPrefix = "{module}_"
}}

android {{
    namespace = "{namespace}"
    compileSdk = 34
    defaultConfig {{
        minSdk = 21
    }}
}}
"""

TEMPLATE_BUSINESS_IMPL_BUILD = """\
plugins {{
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.tencent.kuikly-open.kuikly")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
    id("com.tencent.kuiklybase.resource.generator")
}}

val KEY_PAGE_NAME = "pageName"

kotlin {{
    androidTarget {{
        compilations.all {{
            kotlinOptions {{
                jvmTarget = "1.8"
            }}
        }}
    }}

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {{
        val commonMain by getting {{
            dependencies {{
                api("com.tencent.kuikly-open:core:${{Version.getKuiklyVersion()}}")
                api("com.tencent.kuikly-open:core-annotations:${{Version.getKuiklyVersion()}}")
                api("com.tencent.kuikly-open:compose:${{Version.getKuiklyVersion()}}")
                implementation(project("{api_project_path}"))
                implementation(project(":common:base"))
                implementation(project(":common:widget"))
                implementation("io.insert-koin:koin-core:{koin_version}")
                implementation("com.tencent.kuiklybase:resource-core:0.0.1")
                implementation("com.tencent.kuiklybase:resource-compose:0.0.1")
            }}
        }}
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }}
    }}
}}

ksp {{
    arg(KEY_PAGE_NAME, getPageName())
    // 多模块配置：{module}_impl 是子模块（moduleId 不能包含冒号）
    arg("moduleId", "{module}_impl")
    arg("isMainModule", "false")
    arg("enableMultiModule", "true")
}}

dependencies {{
    compileOnly("com.tencent.kuikly-open:core-ksp:${{Version.getKuiklyVersion()}}") {{
        add("kspAndroid", this)
        add("kspIosArm64", this)
        add("kspIosX64", this)
        add("kspIosSimulatorArm64", this)
    }}
}}

multiplatformResources {{
    multiplatformResourcesPackage = "{namespace}"
    multiplatformResourcesClassName = "{class_name}MR"
    multiplatformResourcesPrefix = "{module}_"
}}

android {{
    namespace = "{namespace}"
    compileSdk = 34
    defaultConfig {{
        minSdk = 21
    }}
}}

fun getPageName(): String {{
    return (project.properties[KEY_PAGE_NAME] as? String) ?: ""
}}
"""

TEMPLATE_PAGE_BUILD = """\
plugins {{
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.tencent.kuikly-open.kuikly")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
    id("com.tencent.kuiklybase.resource.generator")
}}

val KEY_PAGE_NAME = "pageName"

kotlin {{
    androidTarget {{
        compilations.all {{
            kotlinOptions {{
                jvmTarget = "1.8"
            }}
        }}
    }}

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {{
        val commonMain by getting {{
            dependencies {{
                api("com.tencent.kuikly-open:core:${{Version.getKuiklyVersion()}}")
                api("com.tencent.kuikly-open:core-annotations:${{Version.getKuiklyVersion()}}")
                api("com.tencent.kuikly-open:compose:${{Version.getKuiklyVersion()}}")
                implementation(project(":common:base"))
                implementation(project(":common:widget"))
                implementation("io.insert-koin:koin-core:{koin_version}")
                implementation("com.tencent.kuiklybase:resource-core:0.0.1")
                implementation("com.tencent.kuiklybase:resource-compose:0.0.1")
            }}
        }}
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }}
    }}
}}

ksp {{
    arg(KEY_PAGE_NAME, getPageName())
    // 多模块配置：{module} 是子模块（moduleId 不能包含冒号）
    arg("moduleId", "{module}")
    arg("isMainModule", "false")
    arg("enableMultiModule", "true")
}}

dependencies {{
    compileOnly("com.tencent.kuikly-open:core-ksp:${{Version.getKuiklyVersion()}}") {{
        add("kspAndroid", this)
        add("kspIosArm64", this)
        add("kspIosX64", this)
        add("kspIosSimulatorArm64", this)
    }}
}}

multiplatformResources {{
    multiplatformResourcesPackage = "{namespace}"
    multiplatformResourcesClassName = "{class_name}MR"
    multiplatformResourcesPrefix = "{module}_"
}}

android {{
    namespace = "{namespace}"
    compileSdk = 34
    defaultConfig {{
        minSdk = 21
    }}
}}

fun getPageName(): String {{
    return (project.properties[KEY_PAGE_NAME] as? String) ?: ""
}}
"""


# ============================================================
#  模板: build.ohos.gradle.kts (OHOS 构建)
# ============================================================

TEMPLATE_COMMON_BUILD_OHOS = """\
plugins {{
    kotlin("multiplatform")
    id("com.android.library")
    id("com.tencent.kuiklybase.resource.generator")
}}

kotlin {{
    androidTarget {{
        compilations.all {{
            kotlinOptions {{
                jvmTarget = "1.8"
            }}
        }}
    }}

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    ohosArm64 {{
    }}

    sourceSets {{
        val commonMain by getting {{
            dependencies {{
                implementation("com.tencent.kuiklybase:resource-core:0.0.1")
                implementation("com.tencent.kuiklybase:resource-compose:0.0.1")
            }}
        }}
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }}
        val ohosArm64Main by getting {{
            dependsOn(commonMain)
        }}
    }}
}}

multiplatformResources {{
    multiplatformResourcesPackage = "{namespace}"
    multiplatformResourcesClassName = "{class_name}MR"
    multiplatformResourcesPrefix = "{module}_"
}}

android {{
    namespace = "{namespace}"
    compileSdk = 34
    defaultConfig {{
        minSdk = 21
    }}
}}
"""

TEMPLATE_BUSINESS_API_BUILD_OHOS = """\
plugins {{
    kotlin("multiplatform")
    id("com.android.library")
    id("com.tencent.kuiklybase.resource.generator")
}}

kotlin {{
    androidTarget {{
        compilations.all {{
            kotlinOptions {{
                jvmTarget = "1.8"
            }}
        }}
    }}

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    ohosArm64 {{
    }}

    sourceSets {{
        val commonMain by getting {{
            dependencies {{
                implementation("com.tencent.kuiklybase:resource-core:0.0.1")
                implementation("com.tencent.kuiklybase:resource-compose:0.0.1")
            }}
        }}
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }}
        val ohosArm64Main by getting {{
            dependsOn(commonMain)
        }}
    }}
}}

multiplatformResources {{
    multiplatformResourcesPackage = "{namespace}"
    multiplatformResourcesClassName = "{class_name}MR"
    multiplatformResourcesPrefix = "{module}_"
}}

android {{
    namespace = "{namespace}"
    compileSdk = 34
    defaultConfig {{
        minSdk = 21
    }}
}}
"""

TEMPLATE_BUSINESS_IMPL_BUILD_OHOS = """\
import org.gradle.kotlin.dsl.kotlin

plugins {{
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.tencent.kuiklybase.knoi.plugin") version("0.0.4")
}}

kotlin {{
    androidTarget {{
        compilations.all {{
            kotlinOptions {{
                jvmTarget = "1.8"
            }}
        }}
    }}

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    ohosArm64 {{
    }}

    sourceSets {{
        val commonMain by getting {{
            dependencies {{
                implementation(project("{api_project_path}"))
                implementation(project(":common:base"))
                implementation(project(":common:widget"))
                implementation(project(":shared"))
            }}
        }}
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }}
        val ohosArm64Main by getting {{
            dependsOn(commonMain)
        }}
    }}
}}

android {{
    namespace = "{namespace}"
    compileSdk = 34
    defaultConfig {{
        minSdk = 21
    }}
}}
"""

TEMPLATE_PAGE_BUILD_OHOS = """\
import org.gradle.kotlin.dsl.kotlin

plugins {{
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.tencent.kuiklybase.knoi.plugin") version("0.0.4")
    id("com.tencent.kuiklybase.resource.generator")
}}

kotlin {{
    androidTarget {{
        compilations.all {{
            kotlinOptions {{
                jvmTarget = "1.8"
            }}
        }}
    }}

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    ohosArm64 {{
    }}

    sourceSets {{
        val commonMain by getting {{
            dependencies {{
                implementation("com.tencent.kuikly-open:core:${{Version.getKuiklyOhosVersion()}}")
                implementation("com.tencent.kuikly-open:core-annotations:${{Version.getKuiklyOhosVersion()}}")
                implementation("com.tencent.kuikly-open:compose:${{Version.getKuiklyOhosVersion()}}")
                implementation(project(":common:base"))
                implementation(project(":common:widget"))
                implementation(project(":shared"))
                implementation("com.tencent.kuiklybase:resource-core:0.0.1")
                implementation("com.tencent.kuiklybase:resource-compose:0.0.1")
            }}
        }}
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }}
        val ohosArm64Main by getting {{
            dependsOn(commonMain)
        }}
    }}
}}

multiplatformResources {{
    multiplatformResourcesPackage = "{namespace}"
    multiplatformResourcesClassName = "{class_name}MR"
    multiplatformResourcesPrefix = "{module}_"
}}

android {{
    namespace = "{namespace}"
    compileSdk = 34
    defaultConfig {{
        minSdk = 21
    }}
}}
"""


# ============================================================
#  模板: 示例代码
# ============================================================

TEMPLATE_COMMON_MODULE = """\
package {package}

/**
 * {module_name} 模块入口
 *
 * 提供本模块的基础能力，后续业务代码通过此入口调用。
 */
object {class_name}Module {{
    const val TAG = "{class_name}Module"

    // TODO: 实现基础能力
}}
"""

TEMPLATE_BUSINESS_API_INTERFACE = """\
package {package}

/**
 * {module_name} 服务接口
 */
interface I{class_name}Service {{
    // TODO: 定义业务接口
}}
"""

TEMPLATE_BUSINESS_IMPL_SERVICE = """\
package {package}

import {api_package}.I{class_name}Service

/**
 * {module_name} 服务实现
 */
class {class_name}ServiceImpl : I{class_name}Service {{
    // TODO: 实现业务逻辑
}}
"""

TEMPLATE_BUSINESS_IMPL_KOIN = """\
package {package}

import {api_package}.I{class_name}Service
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * {module_name} 模块 Koin 依赖注入配置
 *
 * 使用方式：
 *   startKoin {{
 *       modules({class_name}Module)
 *   }}
 *
 * 获取实例：
 *   val service: I{class_name}Service = koin.get()
 */
val {class_name}Module: Module = module {{
    single<I{class_name}Service> {{ {class_name}ServiceImpl() }}
}}
"""

TEMPLATE_PAGE_PAGE = """\
package {package}

import androidx.compose.runtime.Composable
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.common.widget.LocalContextProvider
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.compose.ui.platform.LocalActivity
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.tmm.kmmresource.compose.stringResource

@Page("{module}_page")
public class {class_name}Page : BasePager() {{
    override fun willInit() {{
        super.willInit()
        setContent {{
            LocalContextProvider {{
                {class_name}Content()
            }}
        }}
    }}
}}

@Composable
private fun {class_name}Content() {{
    val localPager = LocalActivity.current.getPager()
    val routerModule = localPager.acquireModule<RouterModule>(RouterModule.MODULE_NAME)

    val pageTitle = stringResource({class_name}MR.strings.{module}_title)

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {{
        {class_name}Header(title = pageTitle, onClose = {{ routerModule.closePage() }})
        // TODO: 实现页面内容
    }}
}}

@Composable
private fun {class_name}Header(title: String, onClose: () -> Unit) {{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF7B7FE4)),
        contentAlignment = Alignment.Center
    ) {{
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "✕",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable {{ onClose() }}
                .padding(12.dp)
        )
    }}
}}
"""


# ============================================================
#  settings.gradle.kts 注册/注销逻辑
# ============================================================

def register_in_settings(module_gradle_paths: list[str]) -> None:
    """将模块注册到 settings.gradle.kts 和 settings.ohos.gradle.kts"""
    for settings_path in [SETTINGS_KTS, SETTINGS_OHOS_KTS]:
        if not settings_path.exists():
            continue

        with open(settings_path, "r", encoding="utf-8") as f:
            content = f.read()

        new_lines = []
        registered_count = 0

        for gradle_path in module_gradle_paths:
            if f'include("{gradle_path}")' in content:
                print(f"  跳过注册（已存在）: {gradle_path} @ {settings_path.name}")
                continue
            new_lines.append(f'include("{gradle_path}")')
            # 在 OHOS settings 中添加 buildFileName 重定向（仅 impl 子模块需要，api 不需要）
            if settings_path == SETTINGS_OHOS_KTS and not gradle_path.endswith(":api"):
                new_lines.append(f'project("{gradle_path}").buildFileName = buildFileName')
            registered_count += 1

        if new_lines:
            content = content.rstrip() + "\n\n" + "\n".join(new_lines) + "\n"
            with open(settings_path, "w", encoding="utf-8") as f:
                f.write(content)
            print(f"  注册 {registered_count} 个模块到 {settings_path.name}")


def unregister_from_settings(module_gradle_paths: list[str]) -> None:
    """从 settings.gradle.kts 和 settings.ohos.gradle.kts 注销模块"""
    for settings_path in [SETTINGS_KTS, SETTINGS_OHOS_KTS]:
        if not settings_path.exists():
            continue

        with open(settings_path, "r", encoding="utf-8") as f:
            lines = f.readlines()

        new_lines = []
        for line in lines:
            # 检查这一行是否包含要删除的 include 或 buildFileName
            should_remove = False
            for gradle_path in module_gradle_paths:
                if f'include("{gradle_path}")' in line or f'project("{gradle_path}").buildFileName' in line:
                    should_remove = True
                    print(f"  注销: {gradle_path} @ {settings_path.name}")
                    break
            if not should_remove:
                new_lines.append(line)

        # 清理多余的空行（连续超过 1 个空行压缩为 1 个）
        cleaned = []
        prev_empty = False
        for line in new_lines:
            is_empty = line.strip() == ""
            if is_empty and prev_empty:
                continue
            cleaned.append(line)
            prev_empty = is_empty

        with open(settings_path, "w", encoding="utf-8") as f:
            f.writelines(cleaned)


# ============================================================
#  shared/build.gradle.kts 注册/注销逻辑
# ============================================================

SHARED_BUILD_KTS = PROJECT_ROOT / "shared" / "build.gradle.kts"


def register_in_shared(name: str) -> None:
    """在 shared/build.gradle.kts 中注册新业务模块

    修改三处：
    1. kotlin {} 块内的 commonMain dependencies 添加 implementation(project(":business:{name}:impl"))
    2. ksp 块添加/更新 arg("subModules", "{name}_impl")
    3. 文件末尾 dependencies 块添加 compileOnly(project(":business:{name}:impl"))
    """
    if not SHARED_BUILD_KTS.exists():
        print(f"  [WARN] 找不到 shared/build.gradle.kts，跳过注册")
        return

    with open(SHARED_BUILD_KTS, "r", encoding="utf-8") as f:
        content = f.read()

    impl_project_path = f"business:{name}:impl"
    sub_module = f"{name.replace('-', '_')}_impl"

    # === 1. 在 kotlin {} 块的 commonMain dependencies 中添加 implementation ===
    impl_dep = f'implementation(project(":business:{name}:impl"))'
    if impl_dep not in content:
        # 在 common:widget 依赖之后插入（shared 模块的固定依赖）
        anchor = 'implementation(project(":common:widget"))'
        if anchor in content:
            # 找到 anchor 所在的行，在其后插入新行
            lines = content.split("\n")
            new_lines = []
            for line in lines:
                new_lines.append(line)
                if line.strip() == anchor:
                    # 使用与 anchor 相同的缩进
                    indent = line[:len(line) - len(line.lstrip())]
                    new_lines.append(f'{indent}{impl_dep}')
            content = "\n".join(new_lines)
            print(f"  添加依赖: :{impl_project_path} (commonMain implementation)")
        else:
            print(f"  [WARN] 未找到锚点依赖 :common:widget，跳过 implementation 添加")

    # === 2. 在 ksp 块中更新 subModules ===
    submodules_pattern = r'arg\("subModules",\s*"([^"]*)"\)'
    match = re.search(submodules_pattern, content)
    if match:
        existing = match.group(1)
        if sub_module not in existing:
            # 追加新子模块
            new_submodules = f"{existing}, {sub_module}"
            content = content[:match.start(1)] + new_submodules + content[match.end(1):]
            print(f"  更新 subModules: {existing} -> {new_submodules}")
        else:
            print(f"  跳过 subModules 更新（已包含 {sub_module}）")
    else:
        # 没有 subModules，在 isMainModule 之后添加
        is_main_pattern = r'(arg\("isMainModule",\s*"true"\))'
        is_main_match = re.search(is_main_pattern, content)
        if is_main_match:
            anchor_line = is_main_match.group(1)
            lines = content.split("\n")
            new_lines = []
            for line in lines:
                new_lines.append(line)
                if line.strip() == anchor_line:
                    indent = line[:len(line) - len(line.lstrip())]
                    new_lines.append(f'{indent}arg("subModules", "{sub_module}")')
            content = "\n".join(new_lines)
            print(f"  添加 subModules: {sub_module}")
        else:
            print(f"  [WARN] 未找到 ksp 块锚点，跳过 subModules 添加")

    # === 3. 在文件末尾 dependencies 块中添加 compileOnly ===
    compile_only_dep = f'compileOnly(project(":business:{name}:impl"))'
    if compile_only_dep not in content:
        # 在第一个 compileOnly 之前插入（保持 core-ksp 在最后）
        first_compile_pattern = r'(compileOnly\("com\.tencent\.kuikly-open:core-ksp)'
        compile_match = re.search(first_compile_pattern, content)
        if compile_match:
            insert_pos = compile_match.start()
            # 找到该行的行首
            line_start = content.rfind("\n", 0, insert_pos) + 1
            indent = content[line_start:insert_pos]
            # 如果 indent 只包含空白，用它作为缩进
            if indent.strip() == "":
                content = content[:line_start] + indent + compile_only_dep + "\n" + content[line_start:]
            else:
                content = content[:insert_pos] + compile_only_dep + "\n" + content[insert_pos:]
            print(f"  添加依赖: :{impl_project_path} (compileOnly)")
        else:
            # fallback: 在 dependencies { 块的开头后插入
            dep_block_pattern = r'(dependencies \{\s*\n)'
            dep_match = re.search(dep_block_pattern, content)
            if dep_match:
                insert_pos = dep_match.end()
                content = content[:insert_pos] + f"    {compile_only_dep}\n" + content[insert_pos:]
                print(f"  添加依赖: :{impl_project_path} (compileOnly)")
            else:
                print(f"  [WARN] 未找到 dependencies 块，跳过 compileOnly 添加")
    else:
        print(f"  跳过 compileOnly 添加（已存在 :{impl_project_path}）")

    with open(SHARED_BUILD_KTS, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"  注册模块 {name} 到 shared/build.gradle.kts")


def unregister_from_shared(name: str) -> None:
    """从 shared/build.gradle.kts 中注销业务模块"""
    if not SHARED_BUILD_KTS.exists():
        print(f"  [WARN] 找不到 shared/build.gradle.kts，跳过注销")
        return

    with open(SHARED_BUILD_KTS, "r", encoding="utf-8") as f:
        content = f.read()

    impl_project_path = f"business:{name}:impl"
    sub_module = f"{name.replace('-', '_')}_impl"

    # === 1. 移除 commonMain dependencies 中的 implementation ===
    impl_dep = f'implementation(project(":business:{name}:impl"))'
    if impl_dep in content:
        # 移除整行（包括前面的缩进和后面的换行）
        content = re.sub(r'[ \t]*' + re.escape(impl_dep) + r'\n?', '', content)
        print(f"  移除依赖: :{impl_project_path} (commonMain implementation)")
    else:
        print(f"  跳过 implementation 移除（未找到 :{impl_project_path}）")

    # === 2. 从 subModules 中移除 ===
    submodules_pattern = r'arg\("subModules",\s*"([^"]*)"\)'
    match = re.search(submodules_pattern, content)
    if match:
        existing = match.group(1)
        if sub_module in existing:
            # 移除该子模块
            new_value = re.sub(r',\s*' + re.escape(sub_module), '', existing)
            new_value = re.sub(re.escape(sub_module) + r',\s*', '', new_value)
            new_value = re.sub(re.escape(sub_module), '', new_value)
            new_value = new_value.strip()
            if new_value:
                content = content[:match.start(1)] + new_value + content[match.end(1):]
                print(f"  更新 subModules: {existing} -> {new_value}")
            else:
                # 没有剩余子模块，移除整行
                content = re.sub(
                    r'\s*arg\("subModules",\s*"[^"]*"\)\n?',
                    '',
                    content
                )
                print(f"  移除 subModules 参数（无剩余子模块）")
        else:
            print(f"  跳过 subModules 移除（未包含 {sub_module}）")
    else:
        print(f"  跳过 subModules 移除（未找到 subModules 参数）")

    # === 3. 移除 compileOnly ===
    compile_only_dep = f'compileOnly(project(":business:{name}:impl"))'
    if compile_only_dep in content:
        content = re.sub(r'[ \t]*' + re.escape(compile_only_dep) + r'\n?', '', content)
        print(f"  移除依赖: :{impl_project_path} (compileOnly)")
    else:
        print(f"  跳过 compileOnly 移除（未找到 :{impl_project_path}）")

    # 清理多余的空行（连续超过 2 个换行压缩为 2 个）
    content = re.sub(r'\n{4,}', '\n\n\n', content)

    with open(SHARED_BUILD_KTS, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"  从 shared/build.gradle.kts 注销模块 {name}")


# ============================================================
#  创建 common 模块（扁平结构）
# ============================================================

def create_common_module(name: str) -> None:
    """创建 common 类型模块"""
    package = read_package_name()
    package_path = package_to_path(package)

    # 模块根目录
    module_dir = PROJECT_ROOT / "common" / name
    base_package = f"{package}.common.{name}"
    base_package_path = package_to_path(base_package)

    # 类名（首字母大写的模块名）
    class_name = name.replace("-", "_").split("_")
    class_name = "".join(word.capitalize() for word in class_name)

    print(f"\n[创建 common 模块] common/{name}")
    print(f"  包名: {base_package}")

    # 创建源码集目录
    source_sets = ["commonMain", "androidMain", "iosMain", "ohosArm64Main"]
    for ss in source_sets:
        make_dirs(module_dir / "src" / ss / "kotlin" / base_package_path)

    # 生成 build.gradle.kts
    write_file(
        module_dir / "build.gradle.kts",
        TEMPLATE_COMMON_BUILD.format(
            koin_version=KOIN_VERSION,
            namespace=base_package,
            class_name=class_name,
            module=name.replace("-", "_"),
        )
    )

    # 生成 build.ohos.gradle.kts（OHOS 构建配置）
    write_file(
        module_dir / "build.ohos.gradle.kts",
        TEMPLATE_COMMON_BUILD_OHOS.format(
            namespace=base_package,
            class_name=class_name,
            module=name.replace("-", "_"),
        )
    )

    # 生成模块入口文件（命名更有意义：{ModuleName}Module.kt）
    write_file(
        module_dir / "src" / "commonMain" / "kotlin" / base_package_path / f"{class_name}Module.kt",
        TEMPLATE_COMMON_MODULE.format(
            package=base_package,
            module_name=name,
            class_name=class_name,
        )
    )

    # 注册到 settings
    register_in_settings([f":common:{name}"])

    # git add
    git_add_files(CREATED_FILES)

    print(f"[完成] common/{name} 模块创建成功")


# ============================================================
#  创建 business 模块（api/impl 分离）
# ============================================================

def create_business_module(name: str) -> None:
    """创建 business 类型模块"""
    package = read_package_name()

    # 模块根目录
    module_dir = PROJECT_ROOT / "business" / name
    base_package = f"{package}.business.{name}"

    # 类名
    class_name = name.replace("-", "_").split("_")
    class_name = "".join(word.capitalize() for word in class_name)

    api_package = f"{base_package}.api"
    impl_package = f"{base_package}.impl"
    api_package_path = package_to_path(api_package)
    impl_package_path = package_to_path(impl_package)

    api_project_path = f":business:{name}:api"
    module_prefix = name.replace("-", "_")

    print(f"\n[创建 business 模块] business/{name}")
    print(f"  api 包名: {api_package}")
    print(f"  impl 包名: {impl_package}")

    # ---- api 子模块（仅 commonMain，纯接口定义）----
    api_dir = module_dir / "api"
    make_dirs(api_dir / "src" / "commonMain" / "kotlin" / api_package_path)

    # api build.gradle.kts
    write_file(
        api_dir / "build.gradle.kts",
        TEMPLATE_BUSINESS_API_BUILD.format(
            koin_version=KOIN_VERSION,
            namespace=api_package,
            class_name=class_name,
            module=f"{module_prefix}_api",
        )
    )

    # api build.ohos.gradle.kts（OHOS 构建配置）
    write_file(
        api_dir / "build.ohos.gradle.kts",
        TEMPLATE_BUSINESS_API_BUILD_OHOS.format(
            namespace=api_package,
            class_name=class_name,
            module=f"{module_prefix}_api",
        )
    )

    # api 接口文件
    write_file(
        api_dir / "src" / "commonMain" / "kotlin" / api_package_path / f"I{class_name}Service.kt",
        TEMPLATE_BUSINESS_API_INTERFACE.format(
            package=api_package,
            module_name=name,
            class_name=class_name,
        )
    )

    # ---- impl 子模块（全平台源码集）----
    impl_dir = module_dir / "impl"
    impl_source_sets = ["commonMain", "androidMain", "iosMain", "ohosArm64Main"]
    for ss in impl_source_sets:
        make_dirs(impl_dir / "src" / ss / "kotlin" / impl_package_path)

    # impl build.gradle.kts
    write_file(
        impl_dir / "build.gradle.kts",
        TEMPLATE_BUSINESS_IMPL_BUILD.format(
            koin_version=KOIN_VERSION,
            namespace=impl_package,
            api_project_path=api_project_path,
            class_name=class_name,
            module=f"{module_prefix}_impl",
        )
    )

    # impl build.ohos.gradle.kts（OHOS 构建配置）
    write_file(
        impl_dir / "build.ohos.gradle.kts",
        TEMPLATE_BUSINESS_IMPL_BUILD_OHOS.format(
            namespace=impl_package,
            api_project_path=api_project_path,
        )
    )

    # impl 实现文件
    write_file(
        impl_dir / "src" / "commonMain" / "kotlin" / impl_package_path / f"{class_name}ServiceImpl.kt",
        TEMPLATE_BUSINESS_IMPL_SERVICE.format(
            package=impl_package,
            api_package=api_package,
            module_name=name,
            class_name=class_name,
        )
    )

    # impl Koin Module 文件
    write_file(
        impl_dir / "src" / "commonMain" / "kotlin" / impl_package_path / f"{class_name}Module.kt",
        TEMPLATE_BUSINESS_IMPL_KOIN.format(
            package=impl_package,
            api_package=api_package,
            module_name=name,
            class_name=class_name,
        )
    )

    # 注册到 settings
    register_in_settings([
        f":business:{name}:api",
        f":business:{name}:impl",
    ])

    # 注册到 shared/build.gradle.kts
    register_in_shared(name)

    # git add
    git_add_files(CREATED_FILES)

    print(f"[完成] business/{name} 模块创建成功")


# ============================================================
#  创建 page 模块（Composable 页面）
# ============================================================

def create_page_module(name: str) -> None:
    """创建 page 类型模块"""
    package = read_package_name()
    package_path = package_to_path(package)

    # 模块根目录
    module_dir = PROJECT_ROOT / "common" / name
    base_package = f"{package}.common.{name}"
    base_package_path = package_to_path(base_package)

    # 类名（首字母大写的模块名）
    class_name = name.replace("-", "_").split("_")
    class_name = "".join(word.capitalize() for word in class_name)

    module_prefix = name.replace("-", "_")

    print(f"\n[创建 page 模块] common/{name}")
    print(f"  包名: {base_package}")

    # 创建源码集目录
    source_sets = ["commonMain", "androidMain", "iosMain", "ohosArm64Main"]
    for ss in source_sets:
        make_dirs(module_dir / "src" / ss / "kotlin" / base_package_path)

    # 生成 build.gradle.kts
    write_file(
        module_dir / "build.gradle.kts",
        TEMPLATE_PAGE_BUILD.format(
            koin_version=KOIN_VERSION,
            namespace=base_package,
            class_name=class_name,
            module=module_prefix,
        )
    )

    # 生成 build.ohos.gradle.kts（OHOS 构建配置）
    write_file(
        module_dir / "build.ohos.gradle.kts",
        TEMPLATE_PAGE_BUILD_OHOS.format(
            namespace=base_package,
            class_name=class_name,
            module=module_prefix,
        )
    )

    # 生成示例页面文件
    write_file(
        module_dir / "src" / "commonMain" / "kotlin" / base_package_path / f"{class_name}Page.kt",
        TEMPLATE_PAGE_PAGE.format(
            package=base_package,
            module=module_prefix,
            class_name=class_name,
        )
    )

    # 注册到 settings
    register_in_settings([f":common:{name}"])

    # git add
    git_add_files(CREATED_FILES)

    print(f"[完成] page/{name} 模块创建成功")


# ============================================================
#  删除模块
# ============================================================

def delete_common_module(name: str) -> None:
    """删除 common 类型模块"""
    module_dir = PROJECT_ROOT / "common" / name

    print(f"\n[删除 common 模块] common/{name}")

    if not module_dir.exists():
        print(f"  目录不存在: {module_dir.relative_to(PROJECT_ROOT)}")
    else:
        # git rm 目录
        git_rm_files([module_dir])
        # 删除目录
        shutil.rmtree(module_dir)
        print(f"  已删除目录: {module_dir.relative_to(PROJECT_ROOT)}")

    # 从 settings 注销
    unregister_from_settings([f":common:{name}"])

    print(f"[完成] common/{name} 模块已删除")


def delete_business_module(name: str) -> None:
    """删除 business 类型模块"""
    module_dir = PROJECT_ROOT / "business" / name

    print(f"\n[删除 business 模块] business/{name}")

    if not module_dir.exists():
        print(f"  目录不存在: {module_dir.relative_to(PROJECT_ROOT)}")
    else:
        # git rm 目录
        git_rm_files([module_dir])
        # 删除目录
        shutil.rmtree(module_dir)
        print(f"  已删除目录: {module_dir.relative_to(PROJECT_ROOT)}")

    # 从 settings 注销
    unregister_from_settings([
        f":business:{name}:api",
        f":business:{name}:impl",
    ])

    # 从 shared/build.gradle.kts 注销
    unregister_from_shared(name)

    print(f"[完成] business/{name} 模块已删除")


def delete_page_module(name: str) -> None:
    """删除 page 类型模块"""
    module_dir = PROJECT_ROOT / "common" / name

    print(f"\n[删除 page 模块] common/{name}")

    if not module_dir.exists():
        print(f"  目录不存在: {module_dir.relative_to(PROJECT_ROOT)}")
    else:
        # git rm 目录
        git_rm_files([module_dir])
        # 删除目录
        shutil.rmtree(module_dir)
        print(f"  已删除目录: {module_dir.relative_to(PROJECT_ROOT)}")

    # 从 settings 注销
    unregister_from_settings([f":common:{name}"])

    print(f"[完成] page/{name} 模块已删除")


# ============================================================
#  主函数
# ============================================================

def main():
    parser = argparse.ArgumentParser(
        description="Kuikly 脚手架 - 模块脚手架脚本",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  python script/scaffold-module.py --type common --name util
  python script/scaffold-module.py --type business --name login
  python script/scaffold-module.py --type page --name settings
  python script/scaffold-module.py --type common --name util --delete
  python script/scaffold-module.py --type business --name login --delete
  python script/scaffold-module.py --type page --name settings --delete
        """
    )
    parser.add_argument(
        "--type",
        required=True,
        choices=["common", "business", "page"],
        help="模块类型: common(扁平结构) / business(api/impl分离) / page(Composable页面)"
    )
    parser.add_argument(
        "--name",
        required=True,
        help="模块名称，如 util, login, settings"
    )
    parser.add_argument(
        "--delete",
        action="store_true",
        default=False,
        help="删除指定模块（取消注册 + 删除目录 + git rm）"
    )

    args = parser.parse_args()

    # 校验模块名（只允许小写字母、数字、下划线、连字符）
    if not re.match(r"^[a-z][a-z0-9_-]*$", args.name):
        print(f"[ERROR] 模块名格式不合法: {args.name}")
        print("  规则: 小写字母开头，只包含小写字母、数字、下划线、连字符")
        sys.exit(1)

    # 检查项目根目录
    if not GRADLE_PROPERTIES.exists():
        print(f"[ERROR] 找不到项目根目录（gradle.properties）: {GRADLE_PROPERTIES}")
        print("  请在项目根目录执行此脚本")
        sys.exit(1)

    print("=" * 60)
    print("  Kuikly 脚手架 - 模块脚手架")
    print("=" * 60)
    print(f"  类型: {args.type}")
    print(f"  名称: {args.name}")
    print(f"  操作: {'删除' if args.delete else '创建'}")

    if args.delete:
        if args.type == "common":
            delete_common_module(args.name)
        elif args.type == "business":
            delete_business_module(args.name)
        elif args.type == "page":
            delete_page_module(args.name)
    else:
        if args.type == "common":
            create_common_module(args.name)
        elif args.type == "business":
            create_business_module(args.name)
        elif args.type == "page":
            create_page_module(args.name)

    print("\n" + "=" * 60)
    print("  全部完成！")
    print("=" * 60)


if __name__ == "__main__":
    main()
