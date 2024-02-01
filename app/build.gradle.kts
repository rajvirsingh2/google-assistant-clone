plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.8.0"
    id("kotlin-kapt")
}

android {
    namespace = "com.example.googleassistantclone"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.googleassistantclone"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }


}



dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-components:17.1.5")
    implementation("androidx.camera:camera-core:1.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    val lifecycle_version = "2.6.2"
    val arch_version = "2.2.0"
    val room_version = ("2.6.1")

    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")

    // Saved state module for ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")

    // Annotation processor
    //noinspection LifecycleAnnotationProcessorWithJava8
    annotationProcessor ("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
    // alternately - if using Java8, use the following instead of lifecycle-compiler
    implementation ("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")

    // optional - helpers for implementing LifecycleOwner in a Service
    implementation ("androidx.lifecycle:lifecycle-service:$lifecycle_version")

    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    implementation ("androidx.lifecycle:lifecycle-process:$lifecycle_version")

    // optional - ReactiveStreams support for LiveData

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

    // optional - Test helpers for LiveData
    testImplementation ("androidx.arch.core:core-testing:$arch_version")

    implementation("androidx.room:room-runtime:$room_version")
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:$room_version")
//    kapt("com.android.databinding:compiler:3.1.4")
//    kapt("com.android.databinding:compiler:3.2.0-alpha10")
    //noinspection LifecycleAnnotationProcessorWithJava8
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
    implementation("androidx.room:room-rxjava2:$room_version")
    implementation ("the.bot.box:horoscope-api:1.0.2")
    implementation("androidx.room:room-rxjava3:$room_version")
    implementation("androidx.room:room-guava:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")
    implementation("androidx.room:room-paging:$room_version")
    // optional - Test helpers
    implementation ("com.android.volley:volley:1.2.1")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
//    testImplementation ("androidx.room:room-testing:$room_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    implementation ("com.vanniktech:android-image-cropper:4.5.0")
    implementation ("com.theartofdev.edmodo:android-image-cropper:2.8.0")
//    implementation ("com.theartofdev.edmodo:android-image-cropper:2.8.0")
    implementation ("com.github.shubham0204:Text2Summary-Android:alpha-05")
//    implementation ("com.github.KwabenBerko:OpenWeatherMap-Android-Library:2.1.0")
//    implementation ("the.bot.box:horoscope-api:1.0.2")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.cedarsoftware:json-io:4.19.1")


}
