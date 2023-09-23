plugins {
    id("universityschedule.android.application")
    id("universityschedule.android.hilt")
}

android {
    namespace = "com.studentsapps.universityschedule"

    defaultConfig {
        applicationId = "com.studentsapps.universityschedule"
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(project(":ui:schedule"))
    implementation(project(":designsystem"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

kapt {
    correctErrorTypes = true
}