plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.kuikly.init"
    compileSdk = 34
    buildToolsVersion = "36.0.0"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.kuikly.init"
        minSdk = 23
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
    }

    // CI 签名配置（仅在 CI 环境变量存在时生效，本地开发不受影响）
    val ciKeystorePath = System.getenv("CI_KEYSTORE_PATH")
    val ciSigningEnabled = ciKeystorePath != null && file(ciKeystorePath).exists()
    if (ciSigningEnabled) {
        signingConfigs {
            create("ci") {
                storeFile = file(ciKeystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            if (ciSigningEnabled) {
                signingConfig = signingConfigs.getByName("ci")
            }
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

configurations.all {
    resolutionStrategy {
        // 强制使用兼容版本，避免 R8/D8 Kotlin 元数据处理失败
        force("androidx.fragment:fragment:1.6.2")
        force("androidx.fragment:fragment-ktx:1.6.2")
        force("androidx.activity:activity:1.8.2")
        force("androidx.activity:activity-ktx:1.8.2")
        force("androidx.appcompat:appcompat:1.6.1")
        force("androidx.appcompat:appcompat-resources:1.6.1")
        force("androidx.core:core:1.12.0")
        force("androidx.core:core-ktx:1.12.0")
        force("androidx.lifecycle:lifecycle-runtime:2.6.2")
        force("androidx.lifecycle:lifecycle-common:2.6.2")
        force("androidx.lifecycle:lifecycle-livedata:2.6.2")
        force("androidx.lifecycle:lifecycle-livedata-core:2.6.2")
        force("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
        force("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.6.2")
        force("androidx.lifecycle:lifecycle-process:2.6.2")
        force("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
        force("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":common:base"))
    implementation(project(":business:initTask"))
    implementation(project(":business:debug:impl"))

    implementation("io.insert-koin:koin-android:4.0.1")

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.appcompat:appcompat:1.3.1")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
}
