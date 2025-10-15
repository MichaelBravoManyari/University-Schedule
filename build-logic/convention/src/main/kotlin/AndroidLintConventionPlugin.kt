import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            extensions.configure(KtlintExtension::class.java) {
                android.set(true)
                verbose.set(true)
                ignoreFailures.set(false)
            }

            extensions.configure(DetektExtension::class.java) {
                toolVersion = "1.23.8"
                buildUponDefaultConfig = true
                allRules = false
                config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            }
        }
    }
}
