plugins {
    id("universityschedule.android.ui")
}

android {
    namespace = "com.studentsapps.schedule"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(project(":designsystem"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Local test
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.hamcrest)
    testImplementation(libs.espresso.core)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.mockk)

    // Instrumented Test
    testImplementation(libs.espresso.core)
    testImplementation(libs.androidx.test.ext.junit)


    // Testing fragments
    debugImplementation(libs.fragment.testing)
}

kapt {
    correctErrorTypes = true
}
