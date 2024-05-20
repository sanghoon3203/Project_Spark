plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
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
    implementation ("androidx.compose.animation:animation:1.6.7")
    implementation ("androidx.compose.compiler:compiler:1.5.14")
    implementation ("androidx.compose.compiler:compiler:1.5.14")
    implementation ("androidx.compose.ui:ui:1.6.7")
    implementation ("androidx.compose.material3:material3:1.2.1")
    implementation ("androidx.compose.runtime:runtime:1.6.7")
    implementation ("androidx.compose.foundation:foundation:1.6.7")
    implementation ("androidx.compose.material:material:1.6.7")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.6.7")
    implementation ("androidx.activity:activity-compose:1.7.2")
    testImplementation ("androidx.compose.ui:ui-test-junit4:1.6.7")
    debugImplementation ("androidx.compose.ui:ui-tooling:1.6.7")
    implementation ("com.google.accompanist:accompanist-coil:0.11.1")

    //--------제트팩컴포즈 종속성입니다-----------//
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.0.0")
    implementation("com.google.android.gms:play-services-auth:20.1.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.android.gms:play-services-auth:20.1.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation ("io.coil-kt:coil-compose:2.0.0")


// Retrofit dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.picasso:picasso:2.71828")

    testImplementation("junit:junit:4.13.2")
    // Android 테스트 종속성 추가
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    testImplementation("junit:junit:4.13.2")

    implementation("com.squareup.picasso:picasso:2.71828")
    implementation ("com.google.accompanist:accompanist-coil:0.11.1") // For loading images with Coil

}

apply(plugin = "com.google.gms.google-services")
