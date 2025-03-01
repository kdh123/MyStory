import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.dhkim.core.location"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "KAKAO_API_KEY", localProperties["KAKAO_API_KEY"] as String)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:network"))

    implementation(libs.bundles.naver.map)
    implementation(libs.androidx.paging.common)
    implementation(libs.bundles.retrofit)

    implementation(libs.hilt)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.compiler)

    //test
    testImplementation(libs.bundles.androidx.paging3)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.bundles.test)
    kaptTest(libs.hilt.compiler)
    testAnnotationProcessor(libs.hilt.compiler)
    kaptAndroidTest(libs.hilt.compiler)
    androidTestAnnotationProcessor(libs.hilt.compiler)

    implementation(libs.androidx.core.ktx)
}