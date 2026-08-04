plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.kuikly.init"
    compileSdk = 34
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":common:base"))
    implementation(project(":business:initTask"))

    implementation("io.insert-koin:koin-android:4.0.1")

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.appcompat:appcompat:1.3.1")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
}
