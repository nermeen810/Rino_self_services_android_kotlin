package com.rino.self_services.ui.main

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
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
import org.apache.commons.io.FileUtils
import java.io.File


@AndroidEntryPoint

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    private  var _paymentProcessFiles = MutableLiveData<ArrayList<File>>()
    val paymentProcessFiles: LiveData<ArrayList<File>>
        get() = _paymentProcessFiles

    private  var _hrDetailsFiles = MutableLiveData<ArrayList<File>>()
    val hrdetailsFile: LiveData<ArrayList<File>>
        get() = _paymentProcessFiles
    private var  count = 0
    lateinit var caller:FileCaller
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
     //  splashSetup(navController)

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


//    private fun splashSetup(navController: NavController){
//
//        CoroutineScope(Dispatchers.Default).launch{
//            delay(3000)
//            CoroutineScope(Dispatchers.Main).launch{
//                navController.popBackStack()
//                navController.navigate(R.id.loginFragment)
//            }
//        }
//    }
    private fun getImageFromUri(imageUri: Uri?) : File? {
        imageUri?.let { uri ->
            val mimeType = getMimeType(this@MainActivity, uri)
            mimeType?.let {
                val file = createTmpFileFromUri(this, imageUri,"temp_image", ".$it")
//                file?.let { Log.d("image Url = ", file.absolutePath) }
                return file
            }
        }
        return null
    }


    private fun getMimeType(context: Context, uri: Uri): String? {
        //Check uri format to avoid null
        val extension: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

    private fun createTmpFileFromUri(context: Context, uri: Uri, fileName: String, mimeType: String): File? {
        return try {
            val stream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile(fileName, mimeType,cacheDir)
            FileUtils.copyInputStreamToFile(stream,file)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

//    private fun deleteTempFiles(file: File = cacheDir): Boolean {
//        if (file.isDirectory) {
//            val files = file.listFiles()
//            if (files != null) {
//                for (f in files) {
//                    if (f.isDirectory) {
//                        deleteTempFiles(f)
//                    } else {
//                        f.delete()
//                    }
//                }
//            }
//        }
//        return file.delete()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        emitFileForCaller(data,resultCode)

    }
    fun emitFileForCaller(data:Intent?,resultCode:Int){
        if (resultCode == Activity.RESULT_OK && data != null) {
            var array = ArrayList<File>()
            if(data.clipData != null){

                val data: Intent? = data
                if (data?.clipData != null) {

                    val count = data.clipData?.itemCount ?: 0

                    for (i in 0 until count) {
                        val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                        val file = getImageFromUri(imageUri)
                        file?.let {
                            array.add(it)

                        }
                    }
                }
            }else if(data?.data != null){

                val imageUri: Uri? = data.data
                val file = getImageFromUri(imageUri)
                file?.let{
                    array.add(it)

                }
            }
            when(caller){
                FileCaller.hrDetails  -> {
                    _hrDetailsFiles.postValue(array)
                }
                FileCaller.paymentDetails  -> {
                    _paymentProcessFiles.postValue(array)
                }
            }
        }
    }

fun openGalary(){
    val chooseFile: Intent
    val intent: Intent
    chooseFile = Intent(Intent.ACTION_GET_CONTENT)
    chooseFile.type = "*/*"
    chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    intent = Intent.createChooser(chooseFile, "Choose a file")
    startActivityForResult(intent, 111)
}
override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}
enum class FileCaller{
     paymentDetails,hrDetails
}