plugins {
    id("universityschedule.android.library")
    id("universityschedule.android.hilt")
    id("universityschedule.android.room")
}

android {
    namespace = "com.studentsapps.database"
}

dependencies {
    implementation(project(":core:model"))
}