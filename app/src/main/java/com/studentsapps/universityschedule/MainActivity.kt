package com.studentsapps.universityschedule

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.studentsapps.universityschedule.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavController()
        setupBottomNavAndRail()
        observeDestinationChanges()
    }

    private fun setupNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_activity_fragment_container) as NavHostFragment
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
}