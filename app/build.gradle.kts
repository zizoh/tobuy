import Dependencies.AndroidX
import Dependencies.Cache
import Dependencies.Others
import Dependencies.Test
import Dependencies.View

plugins {
    androidApplication
    kotlin(kotlinAndroid)
    kotlin(kotlinKapt)
}

android {

    defaultConfig {
        applicationId = Config.Android.applicationId
        minSdkVersion(Config.Version.minSdkVersion)
        compileSdkVersion(Config.Version.compileSdkVersion)
        targetSdkVersion(Config.Version.targetSdkVersion)
        versionCode = Config.Version.versionCode
        versionName = Config.Version.versionName
        multiDexEnabled = Config.isMultiDexEnabled
        testInstrumentationRunner = Config.Android.testInstrumentationRunner
    }

    buildTypes {
        named(BuildType.RELEASE) {
            isMinifyEnabled = BuildTypeDebug.isMinifyEnabled
            applicationIdSuffix = BuildTypeDebug.applicationIdSuffix
            versionNameSuffix = BuildTypeDebug.versionNameSuffix
            proguardFiles(getDefaultProguardFile(BuildTypeRelease.proguardAndroidFile), BuildTypeRelease.proguardRules)
        }
    }

    buildFeatures {
        dataBinding = Config.isDatabindingEnabld
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementAll(AndroidX.components)
    implementAll(View.components)
    implementAll(Others.components)
    api(Cache.roomRuntime)

    testImplementation(Test.junit)

    androidTestImplementation(Test.runner)

    androidTestImplementation(Test.espresso)

    kapt(Cache.AnnotationProcessor.room)
}
configurations {
    implementation.get().exclude(mapOf("group" to "org.jetbrains", "module" to "annotations"))
}
