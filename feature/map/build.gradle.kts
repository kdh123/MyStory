plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
}

android {
    namespace = "com.dhkim.map"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        testOptions {
            unitTests {
                isIncludeAndroidResources = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
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

    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:location"))
    implementation(project(":feature:location"))

    implementation(libs.bundles.androidx.compose.main)
    implementation(libs.bundles.androidx.compose.side)
    implementation(libs.bundles.androidx.paging3)
    implementation(libs.bundles.google.permission)
    implementation(libs.bundles.naver.map)
    implementation(libs.bundles.google.location)
    implementation(libs.bundles.retrofit)

    implementation(libs.hilt)

    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.compiler)

    //test
    testImplementation(libs.bundles.androidx.paging3)
    testImplementation(libs.bundles.test)
    kaptTest(libs.hilt.compiler)
    testAnnotationProcessor(libs.hilt.compiler)
    androidTestImplementation(libs.bundles.androidx.ui.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    kaptAndroidTest(libs.hilt.compiler)
    androidTestAnnotationProcessor(libs.hilt.compiler)
    debugImplementation(libs.bundles.debug.ui.test)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}