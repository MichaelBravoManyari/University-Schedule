plugins {
    id("universityschedule.android.ui")
}

android {
    namespace = "com.studentsapps.ui"

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    testImplementation(project(":core:testing"))
    testImplementation(project(":ui-test-hilt-manifest"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Local test
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.espresso.core)

}