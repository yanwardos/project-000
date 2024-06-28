plugins {
    alias(libs.plugins.androidApplication)
}

val vuforia_engine_path by extra("C:\\.vuforia")
val arcore_libpath by extra("${layout.buildDirectory}/arcore-native")

android {
    namespace = "com.virtualfittingroom"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.virtualfittingroom"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        ndkVersion = "26.1.10909125"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                arguments += "-Darcore_libpath=${arcore_libpath}/jni" +
                "-Dvuforia_engine_path=${vuforia_engine_path}" +
                "-Darcore_include=${project.rootDir}/arcore/include"
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

    buildFeatures{
        viewBinding = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    implementation(libs.picasso)
    implementation(libs.circleimageview)
    implementation(libs.preference)
    implementation(libs.arsceneview)
    implementation(libs.obj)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.core)
    api(libs.core)

    implementation(files("${vuforia_engine_path}\\build\\java\\VuforiaEngine.jar"))
}

task("extractNativeLibraries"){
    outputs.upToDateWhen { false }
    doFirst{
        configurations.api.map { s->{
            copy{
                from(zipTree(s))
                into(arcore_libpath)
                include("jni/**/*")
            }
        } }
    }
}

// Method added as part of enabling use of ARCore APIs in the App
tasks.whenTaskAdded {
    if (tasks.names.contains("external") || tasks.names.contains("CMake") && !tasks.names.contains("Clean")) {
        dependsOn("extractNativeLibraries")
    }
}
