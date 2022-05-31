package com.rino.self_services.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rino.self_services.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigation : BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigationSetup(navController,bottomNavigation)
        splashSetup(navController,bottomNavigation)

    }

    private fun bottomNavigationSetup(navController: NavController, bottomNavigation: BottomNavigationView) {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_item -> {
                    navController.popBackStack()
                    navController.navigate(R.id.splashFragment)
                    true
                }
                R.id.search_item -> {
                    navController.popBackStack()
                    navController.navigate(R.id.loginFragment)
                    true
                }
                R.id.account_item -> {
                    navController.popBackStack()
                    navController.navigate(R.id.seeAllFragment)
                    true
                }
                else-> false

            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun splashSetup(navController: NavController, bottomNavigation: BottomNavigationView){
        bottomNavigation.isGone = true
        CoroutineScope(Dispatchers.Default).launch{
            delay(3000)
            CoroutineScope(Dispatchers.Main).launch{
                navController.popBackStack()
                navController.navigate(R.id.loginFragment)
            }
        }
    }
}