import org.jetbrains.kotlin.gradle.tasks.Kapt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-kapt")



}

android {
    namespace = "com.example.roomdemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.roomdemo"
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.runtime.livedata)
    implementation (libs.androidx.lifecycle.common.java8)
    implementation (libs.kotlinx.coroutines.android)
    implementation (libs.androidx.lifecycle.livedata.ktx)
// For Kotlin coroutines
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.coroutines.android)
    implementation (libs.kotlinx.coroutines.core)
    implementation(libs.okhttp)


    implementation(libs.converter.gson)



}

