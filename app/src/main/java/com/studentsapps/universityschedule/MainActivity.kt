package com.studentsapps.universityschedule

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.data.repository.CourseRepository
import com.studentsapps.data.repository.PendingOperationRepository
import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.database.model.CourseEntity
import com.studentsapps.database.model.ScheduleEntity
import com.studentsapps.network.datasources.CourseNetworkDataSource
import com.studentsapps.network.datasources.ScheduleNetworkDataSource
import com.studentsapps.network.model.NetworkCourse
import com.studentsapps.network.model.NetworkSchedule
import com.studentsapps.universityschedule.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var pendingOperations: PendingOperationRepository

    @Inject
    lateinit var courseNetworkDataSource: CourseNetworkDataSource

    @Inject
    lateinit var scheduleNetworkDataSource: ScheduleNetworkDataSource

    @Inject
    lateinit var courseRepository: CourseRepository

    @Inject
    lateinit var scheduleRepository: ScheduleRepository

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavController()
        setupBottomNavAndRail()
        observeDestinationChanges()

        val userId = auth.currentUser?.uid

        if (userId != null && isInternetAvailable()) {
            lifecycleScope.launch {
                val dialog = createLoadingDialog()
                dialog.show()
                var hasSynced = false

                pendingOperations.getPendingOperations("PENDING", userId)
                    .takeWhile { !hasSynced }
                    .collect { pendingOperations ->
                        /*Si hay pendingOperations con estado pending, entonces mostrar un cuadro de
                        * dialogo que diga sincronización hasta que todas las pendingOperations esten en
                        * SYNCED, osea no haya ninguna PendingOpeartion con PENDING de estado.
                        * Una vez que ya no halla mas pendingOperations on PENDING, ejecutar la syncronizaión,
                        * y durante el proceso tambien mostrar el cuadro de dialogo hasta que termine,
                        * el usuario no podra hacer ninguna acción durante este proceso.*/
                        if (isInternetAvailable()) {
                            if (pendingOperations.isEmpty() && !hasSynced) {
                                hasSynced = true
                                startSynchronization(dialog, userId)
                            }
                        } else {
                            hasSynced = true
                            dialog.dismiss()
                        }
                    }
            }
        }
    }

    private suspend fun startSynchronization(dialog: Dialog, userId: String) {
        try {
            /*
            * Obtener todos los cursos de firebase.
            * Obtener todos los horarios de firebase.
            * Obtener todos los cursos de la base de datos local
            * Obtener todos los horarios de la base de datos local.
            * Por cada curso en la lista de firebase:
            * - Encontrar su igual en la lista de cursos de la base de datos local.
            * - Si se encuentra el curso dentro de la lista de la base de datos local.
            * -- Si el campo "lastModified" del curso de la base de datos es menor al de firebase.
            * --- Actualizar el curso de la base de datos con los datos del curso de firebase.
            * -Si no se encuentra el curso dentro de la lista de la base de datos local.
            * -- Crear un nuevo curso en la base de datos local con los datos. (OJO: Aqui podria haber un posible problema con los ids de las entidades)(aunnqe no CReoo)
            * Por cada horario en la lista de firebase:
            * - Encontrar su igual en la lista de horarios de la base de datos local.
            * - Si se encuentra el horario dentro de la lista de la base de datos local.
            * -- Si el campo "lastModified" del horario de la base de datos es menor al de firebase.
            * --- Actualizar el horario de la base de datos con los datos del horario de firebase.
            * -Si no se encuentra el horario dentro de la lista de la base de datos local.
            * -- Crear un nuevo horario en la base de datos local con los datos.
            * */

            val coursesFirebase = courseNetworkDataSource.getAllCourses(userId)
            val scheduleFirebase = scheduleNetworkDataSource.getAllSchedules(userId)
            val coursesLocal = courseRepository.getAllCourseEntity(userId).first()
            val schedulesLocal = scheduleRepository.getAllScheduleEntity(userId).first()

            coursesFirebase.forEach { remoteCourse ->
                val localCourse = coursesLocal.find { it.id == remoteCourse.id }
                if (localCourse != null) {
                    if (remoteCourse.lastModified.isAfter(localCourse.lastModified)) {
                        courseRepository.updateCourseEntity(remoteCourse.toCourseEntity())
                    }
                } else {
                    courseRepository.registerCourseEntity(remoteCourse.toCourseEntity())
                }
            }

            scheduleFirebase.forEach { remoteSchedule ->
                val localSchedule = schedulesLocal.find { it.id == remoteSchedule.id }
                if (localSchedule != null) {
                    if (remoteSchedule.lastModified.isAfter(localSchedule.lastModified)) {
                        scheduleRepository.updateScheduleEntity(remoteSchedule.toScheduleEntity())
                    }
                } else {
                    scheduleRepository.registerScheduleEntity(remoteSchedule.toScheduleEntity())
                }
            }
        } finally {
            dialog.dismiss()
        }
    }


    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_activity_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setupBottomNavAndRail() {
        binding.bottomNavView?.setupWithNavController(navController)
        binding.navigationRail?.setupWithNavController(navController)
    }

    private fun observeDestinationChanges() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                com.studentsapps.login.R.id.authFragment,
                com.studentsapps.login.R.id.emailSignUpFragment,
                com.studentsapps.login.R.id.emailLoginFragment -> hideNavigation()

                else -> showNavigation()
            }
        }
    }

    private fun hideNavigation() {
        binding.bottomNavView?.visibility = View.GONE
        binding.navigationRail?.visibility = View.GONE
    }

    private fun showNavigation() {
        binding.bottomNavView?.visibility = View.VISIBLE
        binding.navigationRail?.visibility = View.VISIBLE
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun createLoadingDialog(): AlertDialog {
        return MaterialAlertDialogBuilder(this)
            .setView(com.studentsapps.schedule.R.layout.dialog_progress)
            .setTitle("Sincronizando")
            .setCancelable(false)
            .create()
    }
}