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
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    testImplementation(project(":core:testing"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Local test
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.espresso.core)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}