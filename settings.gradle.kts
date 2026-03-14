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
            url = uri("https://repository.map.naver.com/archive/maven")
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
            url = uri("https://repository.map.naver.com/archive/maven")
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
include(":feature")
include(":feature:setting")
include(":feature:location")
include(":feature:friend")
include(":feature:map")
include(":feature:notification")
include(":feature:home")
include(":feature:onboarding")
include(":feature:main")
include(":feature:trip")
include(":baselineprofile")
include(":benchmark")
include(":core:setting")
include(":core:testing")
include(":core:designsystem")
include(":core:domain")
include(":core:domain:domain-story")
include(":core:data")
include(":core:data:data-story")
include(":core:common-v2")
include(":core:domain:domain-user")
include(":core:data:data-user")
include(":core:domain:domain-trip")
include(":core:data:data-trip")
include(":core:domain:domain-location")
include(":core:data:data-location")
