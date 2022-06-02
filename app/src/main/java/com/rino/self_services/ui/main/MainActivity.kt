package com.rino.self_services.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.rino.self_services.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint

class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private  var _detailsData = MutableLiveData<Bitmap>()
    val detailsData: LiveData<Bitmap>
        get() = _detailsData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
       splashSetup(navController)

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
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

        if (resultCode == Activity.RESULT_OK && data != null) {
            var selectedImage: Uri = data?.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
            _detailsData.postValue(bitmap)

        }


    }
fun openGalary(){
    var myIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    startActivityForResult(myIntent,111)
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