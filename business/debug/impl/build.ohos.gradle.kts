plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.tencent.kuiklybase.knoi.plugin") version("0.0.4")
}

kotlin {
    ohosArm64 {
    }
    sourceSets {
        val ohosArm64Main by getting {
            dependsOn(commonMain)
        }
    }
}
