plugins {
    id("universityschedule.android.ui")
}

android {
    namespace = "com.studentsapps.schedule"

    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(project(":core:ui"))
    testImplementation(project(":core:testing"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Local test
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.espresso.core)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.mockk)
}
