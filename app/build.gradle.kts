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
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(project(":feature:schedule"))
    implementation(project(":core:designsystem"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
}

kapt {
    correctErrorTypes = true
}