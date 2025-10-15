plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
    id("universityschedule.android.room")
    id("universityschedule.android.lint")
}

android {
    namespace = "com.studentsapps.database"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    // Test
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(project(":core:database-test"))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(project(":core:database-test"))
}
