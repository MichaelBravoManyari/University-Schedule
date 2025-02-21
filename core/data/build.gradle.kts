plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
}

android {
    namespace = "com.studentsapps.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:database-test"))
    androidTestImplementation(project(":core:datastore-test"))

    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.firebase.auth.ktx)
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.datastore.preferences)

    testImplementation(libs.kotlinx.coroutines.test)
}