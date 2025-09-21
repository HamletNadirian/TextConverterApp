package com.example.text.viewmodel

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import com.example.text.tabs.UiGallery
import com.example.text.viewmodel.BarCodeGenerator.generate
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException

data class BarCodeUiState(
    val inputText: String = "",
    val generatedBitmap: Bitmap? = null,
    val galleryBitmap: Bitmap? = null,
    val decodedText: String? = null
)

class BarCodeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BarCodeUiState())
    val uiState: StateFlow<BarCodeUiState> = _uiState
    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun generateBarCode(text: String) {
        val bitmap = generate(text)
        _uiState.update { it.copy(generatedBitmap = bitmap) }
    }

    fun updateGalleryBitmap(bitmap: Bitmap) {
        _uiState.update { it.copy(galleryBitmap = bitmap) }
    }
}

object BarCodeGenerator {
    fun generate(data: String): Bitmap? {
        return try {
            val writer = com.google.zxing.MultiFormatWriter()
            val bitMatrix = writer.encode(data, com.google.zxing.BarcodeFormat.CODE_128, 600, 300)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp[x, y] =
                        if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                }
            }
            bmp
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    fun BarcodeFromGalleryScreen() {
        val context = LocalContext.current
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        var barcodeText by remember { mutableStateOf<String?>(null) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                try {
                    // Используем совместимый способ загрузки изображения
                    val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }

                    bitmap = bmp

                    // Распознаем штрихкод
                    val image = InputImage.fromBitmap(bmp, 0)
                    val scanner = BarcodeScanning.getClient()

                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                barcodeText = barcodes.joinToString("\n") { barcode ->
                                    barcode.rawValue ?: "Unknown format"
                                }
                            } else {
                                barcodeText = "Barcode could not be found"
                            }
                        }
                        .addOnFailureListener { exception ->
                            barcodeText = "Recognition error: ${exception.localizedMessage}"
                        }
                } catch (e: IOException) {
                    barcodeText = "Image loading error: ${e.localizedMessage}"
                } catch (e: Exception) {
                    barcodeText = "An error has occurred: ${e.localizedMessage}"
                }
            }

        }

        UiGallery(
            bitmap = bitmap,
            barcodeText = barcodeText,
            onImagePickClick = { launcher.launch("image/*") }
        )
    }
}
