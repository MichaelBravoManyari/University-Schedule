import com.studentsapps.universityschedule.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidUiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("universityschedule.android.library")
                apply("universityschedule.android.hilt")
                apply("androidx.navigation.safeargs.kotlin")
            }

            dependencies {
                add("testImplementation", kotlin("test"))
                add("androidTestImplementation", kotlin("test"))
                add("implementation", libs.findLibrary("kotlinx.coroutines.core").get())
            }
        }
    }
}