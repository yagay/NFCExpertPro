plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val configAuthority = providers.gradleProperty("ynfcConfigAuthority").orNull
    ?: "com.yagay.YNFC.config"

android {
    namespace = "com.yagay.YNFC"
    compileSdk = 35

    defaultConfig {
        minSdk = 31
        buildConfigField("int", "VERSION_CODE", "57")
        buildConfigField("String", "VERSION_NAME", "\"1.0.56\"")
        buildConfigField("int", "HOOK_BUILD", "40")
        buildConfigField("String", "CONFIG_AUTHORITY", "\"$configAuthority\"")
        manifestPlaceholders["ynfcConfigAuthority"] = configAuthority
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("com.google.code.gson:gson:2.11.0")
    compileOnly("io.github.libxposed:api:102.0.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
}
