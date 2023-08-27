plugins {
    `kotlin-dsl`
}

group = "com.studentsapps.universityschedule.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "universityschedule.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "universityschedule.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidUi") {
            id = "universityschedule.android.ui"
            implementationClass = "AndroidUiConventionPlugin"
        }
        register("androidApplication") {
            id = "universityschedule.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
    }
}