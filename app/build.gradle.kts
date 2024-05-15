plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.projectspark" // 네임스페이스 지정
    compileSdkVersion(31)

    defaultConfig {
        applicationId = "com.example.projectspark"
        minSdkVersion(21)
        targetSdkVersion(31)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.0.0")
    implementation("com.google.android.gms:play-services-auth:20.1.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.android.gms:play-services-auth:20.1.0") // 추가된 부분

    implementation("com.squareup.picasso:picasso:2.71828")
}

apply(plugin = "com.google.gms.google-services")
