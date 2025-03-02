plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
}

android {

    lintOptions {
        disable.add("Instantiatable")
    }

    namespace = "com.dhkim.main"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    implementation(project(":core:setting"))
    implementation(project(":core:story"))
    implementation(project(":core:location"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:user"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:friend"))
    implementation(project(":feature:location"))
    implementation(project(":feature:setting"))
    implementation(project(":feature:map"))
    implementation(project(":feature:notification"))
    implementation(project(":feature:home"))
    implementation(project(":feature:trip"))



    implementation(libs.google.gson)
    implementation(libs.bundles.androidx.compose.main)
    implementation(libs.bundles.androidx.compose.side)
    implementation(libs.bundles.androidx.workManager)
    implementation(libs.bundles.firebase)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.bundles.androidx.workManager)

    implementation(libs.hilt)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}