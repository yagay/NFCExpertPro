plugins {
    id("com.android.application")
}

android {
    namespace = "com.yagay.YNFC.standalone"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.yagay.YNFC"
        minSdk = 31
        targetSdk = 37
        versionCode = 57
        versionName = "1.0.56"
        manifestPlaceholders["ynfcConfigAuthority"] = "com.yagay.YNFC.config"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation(project(":feature"))
}
