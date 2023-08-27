plugins {
    id("universityschedule.android.ui")
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "com.studentsapps.schedule"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Local test
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.hamcrest)
    testImplementation(libs.espresso.core)
    testImplementation(libs.androidx.test.ext.junit)

    // Instrumented Test
    testImplementation(libs.espresso.core)
    testImplementation(libs.androidx.test.ext.junit)


    // Testing fragments
    debugImplementation(libs.fragment.testing)
}