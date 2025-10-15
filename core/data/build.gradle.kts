plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
    id("universityschedule.android.lint")
}

android {
    namespace = "com.studentsapps.data"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:database-test"))
    androidTestImplementation(project(":core:datastore-test"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.firebase.auth.ktx)
    kapt(libs.androidx.hilt.compiler)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.datastore.preferences)

    testImplementation(libs.kotlinx.coroutines.test)
}
