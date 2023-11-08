pluginManagement {
    includeBuild("build-logic")
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
    }
}

rootProject.name = "University Schedule"
include(":app")
include(":feature:schedule")
include(":core:designsystem")
include(":core:ui")
include(":ui-test-hilt-manifest")
include(":core:testing")
include(":core:datastore")
include(":core:model")
include(":core:data")
include(":core:datastore-test")
include(":core:data-test")
