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
    namespace = "com.dhkim.story"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "KAKAO_ADMIN_KEY", localProperties["KAKAO_ADMIN_KEY"] as String)

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":core:work"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":feature:setting"))
    implementation(project(":feature:location"))
    implementation(project(":core:common"))
    implementation(project(":core:user"))
    implementation(project(":core:ui"))

    implementation(libs.bundles.androidx.compose.main)
    implementation(libs.bundles.androidx.compose.side)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.androidx.workManager)
    implementation(libs.bundles.naver.map)
    implementation(libs.bundles.google.location)
    implementation(libs.bundles.google.permission)
    implementation(libs.bundles.androidx.paging3)
    implementation(libs.bundles.firebase)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.bundles.androidx.room)
    kapt(libs.androidx.room.compiler)
    implementation(libs.dhcamera)

    implementation(libs.hilt)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.compiler)

    //test
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