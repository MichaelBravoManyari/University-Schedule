import java.util.Properties

val apikeysPropertiesFile = rootProject.file("apikeys.properties")
val apikeysProperties =
    Properties().apply {
        load(apikeysPropertiesFile.inputStream())
    }

plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
    id("universityschedule.android.lint")
}

android {
    namespace = "com.studentsapps.admodule"

    buildFeatures {
        buildConfig = true
    }

    afterEvaluate {
        extensions.configure<com.android.build.gradle.LibraryExtension>("android") {
            buildFeatures.buildConfig = true
        }
    }

    defaultConfig {
        buildConfigField(
            "String",
            "ADMOB_INTERSTITIAL_ID",
            "\"${apikeysProperties["ADMOB_INTERSTITIAL_ID"]}\"",
        )
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    kapt(libs.androidx.hilt.compiler)
    // Google AdMob
    implementation(libs.play.services.ads)
}
