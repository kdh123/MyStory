import java.io.FileInputStream
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.google.service)
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

val apiProperties = Properties().apply {
    load(FileInputStream(rootProject.file("apikey.properties")))
}

android {
    namespace = "com.dhkim.timecapsule"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dhkim.timecapsule"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "KAKAO_API_KEY", localProperties["KAKAO_API_KEY"] as String)
        buildConfigField("String", "KAKAO_ADMIN_KEY", localProperties["KAKAO_ADMIN_KEY"] as String)
        buildConfigField("String", "FCM_URL", localProperties["FCM_URL"] as String)
        buildConfigField("String", "FCM_AUTHORIZATION", localProperties["FCM_AUTHORIZATION"] as String)
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            manifestPlaceholders["NAVER_MAP_API_KEY"] = apiProperties["NAVER_MAP_API_KEY"] as String
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        release {
            manifestPlaceholders += mapOf()
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            manifestPlaceholders["NAVER_MAP_API_KEY"] = apiProperties["NAVER_MAP_API_KEY"] as String

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":feature:map"))
    implementation(project(":feature:friend"))
    implementation(project(":feature:location"))
    implementation(project(":feature:setting"))
    implementation(project(":core:common"))
    implementation(project(":core:user"))
    implementation(project(":core:ui"))
    implementation(project(":core:work"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":camera"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.location)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.hilt)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.compiler)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.android.joda)
    implementation(libs.androidx.animation)

    implementation(libs.bundles.androidx.compose.side)
    implementation(libs.bundles.naver.map)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.google.permission)
    implementation(libs.bundles.google.location)
    implementation(libs.bundles.androidx.paging3)
    implementation(libs.bundles.androidx.room)
    kapt(libs.androidx.room.compiler)
    implementation(libs.bundles.firebase)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.bundles.androidx.datastore)
    implementation(libs.bundles.androidx.workManager)
}