plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin") // Hilt 플러그인 추가
}

android {

    namespace = "com.example.Project_Spark" // 네임스페이스 지정
    compileSdkVersion(34)

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    defaultConfig {
        applicationId = "com.example.Project_Spark"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Jetpack Compose dependencies
    implementation("androidx.compose.ui:ui:1.6.7")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.runtime:runtime:1.6.7")
    implementation("androidx.compose.foundation:foundation:1.6.7")
    implementation("androidx.compose.material:material:1.6.7")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.7")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.animation:animation:1.6.7")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    debugImplementation("androidx.compose.ui:ui-tooling:1.6.7")
    testImplementation("androidx.compose.ui:ui-test-junit4:1.6.7")

    // Jetpack Compose ViewModel dependencies
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")

    // Coil for image loading in Compose
    implementation("io.coil-kt:coil-compose:2.0.0")

    // AndroidX and other dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:26.5.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Google Play services
    implementation("com.google.android.gms:play-services-auth:20.1.0")

    // CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Retrofit dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Picasso for image loading
    implementation("com.squareup.picasso:picasso:2.71828")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Google services plugin
    implementation("com.google.android.gms:play-services-auth:20.1.0")

    // javax.inject
    implementation("javax.inject:javax.inject:1")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")
    // Hilt Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
}

apply(plugin = "com.google.gms.google-services")
