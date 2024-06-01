// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false

}
// build.gradle (Project level)
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.4.0")
        classpath ("com.google.gms:google-services:4.3.10")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")


    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

