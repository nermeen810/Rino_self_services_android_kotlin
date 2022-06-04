package com.rino.self_services.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.rino.self_services.R
import com.rino.self_services.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint

class MainActivity : AppCompatActivity() {
   // private lateinit var appBarConfiguration: AppBarConfiguration
    private var  count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
       splashSetup(navController)

    }


    override fun onBackPressed() {
     //   super.onBackPressed()
        if(count==0)
        {
            Toast.makeText(
                this,
                getString(R.string.exit_msg),
                Toast.LENGTH_SHORT
            ).show()
            count += 1
        }
        else {
            finish()
        }
    }


    private fun splashSetup(navController: NavController){

        CoroutineScope(Dispatchers.Default).launch{
            delay(3000)
            CoroutineScope(Dispatchers.Main).launch{
                navController.popBackStack()
                navController.navigate(R.id.loginFragment)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val navHostFragment = supportFragmentManager.fragments.first()
                as? NavHostFragment

        navHostFragment?.let {
            val childFragments =
                navHostFragment.childFragmentManager.fragments

            childFragments.forEach { fragment ->
                fragment.onActivityResult(requestCode, resultCode, data)

                if (fragment is HomeFragment) {
                    val page = supportFragmentManager
                        .findFragmentByTag(
                            "android:switcher:${R.id.homeFragment}:${fragment.arguments}"
                        )

                    page?.let {
                        page.onActivityResult(requestCode, resultCode, data)
                    }
                }
            }
        }
    }

//   private fun splashSetup(navController: NavController){
//
//       CoroutineScope(Dispatchers.Default).launch{
//           delay(3000)
//           CoroutineScope(Dispatchers.Main).launch{
//               navController.popBackStack()
//               navController.navigate(R.id.loginFragment)
//           }
//       }
//   }

}