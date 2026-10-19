import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

val localProps = Properties()
val localFile = rootProject.file("local.properties")
if (localFile.exists()) {
    FileInputStream(localFile).use { localProps.load(it) }
}

val cloudUrl = localProps.getProperty("SKILLQUIZ_CLOUD_URL")
    ?: System.getenv("SKILLQUIZ_CLOUD_URL")
    ?: "https://skillquiz-lqej.onrender.com/api/"

val localUrl = localProps.getProperty("SKILLQUIZ_LOCAL_URL")
    ?: System.getenv("SKILLQUIZ_LOCAL_URL")
    ?: "http://192.168.18.12:3000/api/"

android {
    namespace = "pe.edu.cibertec.skillquiznuevo"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "CLOUD_API_URL", "\"$cloudUrl\"")
        buildConfigField("String", "LOCAL_API_URL", "\"$localUrl\"")
        applicationId = "pe.edu.cibertec.skillquiznuevo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}