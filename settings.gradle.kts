pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "SeSeCoffee"
include(":app")
 