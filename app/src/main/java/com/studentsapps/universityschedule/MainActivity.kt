package com.studentsapps.universityschedule

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studentsapps.sync.SynchronizationManager
import com.studentsapps.universityschedule.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var synchronizationManager: SynchronizationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavController()
        setupBottomNavAndRail()
        observeDestinationChanges()

        if (savedInstanceState == null) {
            val dialog = createLoadingDialog()
            synchronizationManager.startSyncIfNeeded(dialog)
        }
    }

    private fun createLoadingDialog(): AlertDialog {
        return MaterialAlertDialogBuilder(this)
            .setView(com.studentsapps.common.R.layout.dialog_progress)
            .setTitle(this.getText(com.studentsapps.common.R.string.synchronizing))
            .setCancelable(false)
            .create()
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_activity_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setupBottomNavAndRail() {
        binding.bottomNavView?.apply {
            setupWithNavController(navController)
            setOnItemSelectedListener { item ->
                navigateWithFade(item.itemId)
                true
            }
        }

        binding.navigationRail?.apply {
            setupWithNavController(navController)
            setOnItemSelectedListener { item ->
                navigateWithFade(item.itemId)
                true
            }
        }
    }

    private fun navigateWithFade(destinationId: Int) {
        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        if (navController.currentDestination?.id != destinationId) {
            navController.navigate(destinationId, null, options)
        }
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
}