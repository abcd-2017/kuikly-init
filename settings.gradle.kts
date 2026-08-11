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
        gradlePluginPortal()
        mavenLocal()
        maven {
            url = uri("https://mirrors.tencent.com/nexus/repository/maven-tencent/")
        }
    }
}

rootProject.name = "kuikly-init"
include(":androidApp")
include(":shared")

// common 基础能力模块
include(":common:util")
include(":common:widget")

// business 业务模块
include(":business:initTask")
include(":business:login:api")
include(":business:login:impl")

include(":common:base")

include(":business:debug:api")
include(":business:debug:impl")

