plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.0" apply false
//    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
}
buildscript{
    repositories{
        google()
        mavenCentral()
    }
    dependencies{
        val kotlin_version = "1.8.0"
        classpath ("com.android.tools.build:gradle:8.2.0")
        classpath ("com.google.gms:google-services:4.4.0")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath ("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.3")
        classpath ("com.github.dcendents:android-maven-gradle-plugin:1.5")
    }
}