package com.studentsapps.universityschedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studentsapps.universityschedule.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: SplashViewModel by viewModels()
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        splashScreen.setKeepOnScreenCondition {
            viewModel.isReady.value == false && viewModel.forceUpdateRequired.value == false
        }

        observeViewModel()
        setupNavController()
        setupBottomNavAndRail()
        observeDestinationChanges()
        observeViewModelSync()

        if (savedInstanceState == null) {
            viewModel.startSyncIfNeeded()
        }
    }

    private fun observeViewModel() {
        viewModel.forceUpdateRequired.observe(this) { required ->
            if (required) {
                showForceUpdateDialog()
            }
        }
    }

    private fun observeViewModelSync() {
        lifecycleScope.launch {
            viewModel.isSyncing.collect { isSyncing ->
                if (isSyncing) {
                    if (dialog == null) dialog = createLoadingDialog()
                    dialog?.show()
                } else {
                    dialog?.dismiss()
                    dialog = null
                }
            }
        }
    }

    private fun showForceUpdateDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.title_update_required_dialog))
            .setMessage(getString(R.string.message_update_required_dialog))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.update)) { _, _ ->
                val appPackageName = packageName
                try {
                    startActivity(
                        Intent(Intent.ACTION_VIEW, "market://details?id=$appPackageName".toUri()),
                    )
                } catch (_: Exception) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=$appPackageName".toUri(),
                        ),
                    )
                }
                finish()
            }.show()
    }

    private fun createLoadingDialog(): AlertDialog =
        MaterialAlertDialogBuilder(this)
            .setView(com.studentsapps.common.R.layout.dialog_progress)
            .setTitle(this.getText(com.studentsapps.common.R.string.synchronizing))
            .setCancelable(false)
            .create()

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
        val options =
            NavOptions
                .Builder()
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
                com.studentsapps.login.R.id.emailLoginFragment,
                -> hideNavigation()

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
