import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.dhkim.data.user"
    compileSdk = 36

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core:domain:domain-user"))
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))

    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.androidx.datastore)
    implementation(libs.bundles.firebase)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.bundles.androidx.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.hilt.compiler)

    //test
    testImplementation(libs.bundles.test)
    kspTest(libs.hilt.compiler)
    testAnnotationProcessor(libs.hilt.compiler)
    kspAndroidTest(libs.hilt.compiler)
    androidTestAnnotationProcessor(libs.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}