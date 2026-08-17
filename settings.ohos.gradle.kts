pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
    }
}

rootProject.name = "kuikly"

val buildFileName = "build.ohos.gradle.kts"
rootProject.buildFileName = buildFileName

include(":androidApp")
include(":shared")
project(":shared").buildFileName = buildFileName

// common 基础能力模块
include(":common:util")
include(":common:base")
project(":common:base").buildFileName = buildFileName
include(":common:widget")
project(":common:widget").buildFileName = buildFileName

// business 业务模块
include(":business:initTask")
project(":business:initTask").buildFileName = buildFileName
include(":business:login:api")
include(":business:login:impl")
project(":business:login:impl").buildFileName = buildFileName

// debug:api 需要注册（androidApp 依赖），但 OHOS 不编译其源码
include(":business:debug:api")
project(":business:debug:api").buildFileName = buildFileName
include(":business:debug:impl")
project(":business:debug:impl").buildFileName = buildFileName
