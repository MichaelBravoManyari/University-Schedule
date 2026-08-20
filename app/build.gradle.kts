import java.util.Properties

val apikeysPropertiesFile = rootProject.file("apikeys.properties")
val apikeysProperties =
    Properties().apply {
        load(apikeysPropertiesFile.inputStream())
    }

plugins {
    id("universityschedule.android.application")
    id("universityschedule.android.lint")
    id("universityschedule.android.hilt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "com.studentsapps.universityschedule"

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        create("profile") {
            initWith(getByName("release"))
            isDebuggable = true
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    defaultConfig {
        applicationId = "com.studentsapps.universityschedule"
        versionCode = 4
        versionName = "1.0"

        // App ID (para el manifest)
        manifestPlaceholders["ADMOB_APP_ID"] = apikeysProperties["ADMOB_APP_ID"] ?: ""
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(project(":feature:schedule"))
    implementation(project(":feature:course"))
    implementation(project(":feature:login"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":sync"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.core.splashscreen)

    kapt(libs.androidx.hilt.compiler)

    // Navigation component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    // Google AdMob
    implementation(libs.play.services.ads)
}

kapt {
    correctErrorTypes = true
}
