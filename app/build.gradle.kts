plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mammouthmedicalpharmacyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mammouthmedicalpharmacyapp"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Lib Main
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)

    // Androidx
    implementation(libs.activity)
    implementation(libs.annotation)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Firebase + Google
    implementation(libs.firebase.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ui.storage)
    implementation(libs.material)
    implementation(libs.play.services.auth)

    // Other
    implementation(libs.glide)
    annotationProcessor(libs.glide)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}