package com.example.text


import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.text.ui.theme.TextTheme

class MainActivity : ComponentActivity() {
    /*var textResultFromCamera = mutableStateOf("")

   private val requestPermissionLauncher = registerForActivityResult(
       ActivityResultContracts.RequestPermission()
   ) { isGranted ->
       if (isGranted) {
           showCamera()
       }
   }

   private fun checkCameraPermission(context: Context) {
       if (ContextCompat.checkSelfPermission(
               context,
               android.Manifest.permission.CAMERA
           ) == PackageManager.PERMISSION_GRANTED
       ) {
           showCamera()
       } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
           Toast.makeText(
               this@MainActivity,
               "Camera permission needed. It can be changed in settings.",
               Toast.LENGTH_LONG
           ).show()
       } else {
           requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
       }
   }

   private val barCodeLauncher = registerForActivityResult(ScanContract()) { result ->
       if (result.contents == null) {
           Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_LONG).show()
       } else {
           textResultFromCamera.value = result.contents
       }
   }

   private fun showCamera() {
       val options = ScanOptions()
       options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
       options.setPrompt("Scan a barcode")
       options.setCameraId(0) // Use a specific camera of the device
       options.setBeepEnabled(false)
       options.setBarcodeImageEnabled(true)
       options.setOrientationLocked(false)
       barCodeLauncher.launch(options)

   }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TextTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.systemBars.asPaddingValues())
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //     TabLayout()
                        AppNavHost()
                    }
                }
            }
        }
    }
}



