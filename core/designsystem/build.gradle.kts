plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.lint")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.studentsapps.designsystem"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
}

dependencies {
    implementation(libs.material)
    implementation(libs.androidx.ui.text.google.fonts)

    // Compose core
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation(composeBom)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material3)
}
