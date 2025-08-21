plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
}

android {
    namespace = "com.studentsapps.admodule"
}

dependencies {
    kapt(libs.androidx.hilt.compiler)
    // Google AdMob
    implementation(libs.play.services.ads)
}