

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        jcenter()
    }
}



dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://jitpack.io")
        maven("https://dl.bintary.com/wotomas/maven")
        maven("https://maven.google.com")
        maven("https://dl.bintray.com/boxbotbarry/maven")
    }
}

rootProject.name = "Google Assistant Clone"
include(":app")
 