package com.rino.self_services.ui.main

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.rino.self_services.R
import com.rino.self_services.model.pojo.HRClearanceDetailsRequest
import com.rino.self_services.ui.internetConnection.BaseActivity
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.paymentProcessHome.NavToDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*

@AndroidEntryPoint

class MainActivity : BaseActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    val viewModel: MainActivityViewModel by viewModels()
    var _paymentProcessFiles = MutableLiveData<ArrayList<File>>()
    val paymentProcessFiles: LiveData<ArrayList<File>>
        get() = _paymentProcessFiles

    var _hrDetailsFiles = MutableLiveData<ArrayList<File>>()
    val hrdetailsFile: LiveData<ArrayList<File>>
        get() = _hrDetailsFiles
    private var  count = 0
    var caller:FileCaller = FileCaller.none
    private var value:NavToDetails? = null
    private var hrValue:HRClearanceDetailsRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        value = intent.getParcelableExtra("detailsData") as NavToDetails?
        hrValue = intent.getParcelableExtra("hrDetailsData") as HRClearanceDetailsRequest?

        splashSetup(navController)
    }



    private fun setArabicAsDefault() {
        val locale = Locale("ar")
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        applicationContext.resources.updateConfiguration(config, null)
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

                if(viewModel.isLogin() && value == null && intent.extras?.getString("processType") == null && hrValue == null) {
                    navController.popBackStack()
                    navController.navigate(R.id.homeFragment)
                }else if(viewModel.isLogin() && value?.id != null ){
                    navController.popBackStack()
                    if(value != null){
                        val args = Bundle()
                        args.putParcelable("nav_to_pp_details", NavToDetails("me",value!!.id,false))
                        args.putParcelable("nav_to_see_all_payment", NavSeeAll("", "", ""))
                        navController.navigate(R.id.homeFragment,args)
                    }

                }else if(viewModel.isLogin() && hrValue?.requestID != null){
                    navController.popBackStack()
                    val args = Bundle()
                    args.putParcelable("nav_to_HR_details", HRClearanceDetailsRequest(hrValue!!.entity,hrValue!!.requestID,false,"me"))
                    navController.navigate(R.id.homeFragment,args)
                }
                else if (viewModel.isLogin() && intent.extras?.getString("processType") != null){
                    navController.popBackStack()

                    if(intent.extras?.getString("processType").equals("payment")){
                        val args = Bundle()
                        args.putParcelable("nav_to_pp_details", NavToDetails("me",intent.extras?.getString("id")!!.toInt(),false))
//                        args.putParcelable("nav_to_see_all_payment", NavSeeAll("", "", ""))
                        navController.navigate(R.id.homeFragment,args)
                    }else if(intent.extras?.getString("processType").equals("clearance")){
                        val args = Bundle()
                        args.putParcelable("nav_to_HR_details", HRClearanceDetailsRequest(intent.extras?.getString("entityType")!!.toInt(),intent.extras?.getString("id")!!.toInt(),false,"me"))
                        navController.navigate(R.id.homeFragment,args)
                    }
                }
                else {
                    navController.popBackStack()
                    navController.navigate(R.id.loginFragment)
                }
            }
        }
    }
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
        if (caller != FileCaller.none){
            emitFileForCaller(data,resultCode)
        }
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
                    Log.d("hr","hr")
                    _hrDetailsFiles.postValue(array)

//                    caller = FileCaller.none
                }
                FileCaller.paymentDetails  -> {
                    _paymentProcessFiles.postValue(array)

//                    caller = FileCaller.none
                }
                FileCaller.none -> {

                }
            }

        }
    }

    fun openGalary(){
        val chooseFile: Intent
        val intent: Intent
        chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "/"
        chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent = Intent.createChooser(chooseFile, "Choose a file")
        startActivityForResult(intent, 111)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        setIntent(intent)
//    }

}
enum class FileCaller{
    paymentDetails,hrDetails,none
}