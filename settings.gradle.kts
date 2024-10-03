pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven {
            url = uri("https://naver.jfrog.io/artifactory/maven/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://naver.jfrog.io/artifactory/maven/")
        }
        maven("https://jitpack.io")
    }

}

rootProject.name = "TimeCapsule"
include(":app")
include(":core")
include(":core:database")
include(":core:network")
include(":core:datastore")
include(":core:work")
include(":core:ui")
include(":core:common")
include(":feature")
include(":feature:setting")
include(":feature:location")
include(":core:user")
include(":feature:friend")
include(":feature:map")
include(":feature:notification")
include(":feature:home")
include(":feature:onboarding")
include(":feature:main")
include(":feature:trip")
