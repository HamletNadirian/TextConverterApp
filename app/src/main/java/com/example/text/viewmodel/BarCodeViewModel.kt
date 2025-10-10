package com.example.text.viewmodel

import UiGallery
import android.graphics.Bitmap
import android.graphics.Color
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
import com.example.text.viewmodel.BarCodeGenerator.generateBarcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException

data class BarCodeUiState(
    val inputText: String = "",
    val generatedBitmap: Bitmap? = null,
    val galleryBitmap: Bitmap? = null,
    val decodedText: String? = null,
    val selectedBarcodeType: String = "QR_CODE",
    val errorMessage: String? = null
)

class BarCodeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BarCodeUiState())
    val uiState: StateFlow<BarCodeUiState> = _uiState

    fun updateInput(text: String) {
        val currentState = _uiState.value
        val filtered = if (listOf("ITF").contains(currentState.selectedBarcodeType)) {
            text.filter { char: Char -> char.isDigit() }  // Явный параметр: char: Char
        } else {
            text
        }
        _uiState.update { it.copy(inputText = filtered) }
    }

    fun updateBarcodeType(type: String) {
        _uiState.update { it.copy(selectedBarcodeType = type) }
    }

    fun generateBarCode(text: String) {

        val currentState = _uiState.value
        val format = try {
            BarcodeFormat.valueOf(currentState.selectedBarcodeType)
        } catch (e: IllegalArgumentException) {
            BarcodeFormat.QR_CODE
        }
        val bitmap = generateBarcode(
            text,
            format
        )
        if (bitmap == null) {
            _uiState.update {
                it.copy(
                    errorMessage = "Ошибка генерации: неверный ввод или формат",
                    generatedBitmap = null
                )
            }
        } else {
            _uiState.update { it.copy(generatedBitmap = bitmap, errorMessage = null) }
        }
        _uiState.update { it.copy(generatedBitmap = bitmap) }
    }

    private fun validateInput(text: String, format: BarcodeFormat): String? {
        val digitsOnly = text.filter { char: Char -> char.isDigit() }
        val length = digitsOnly.length
        return when (format) {

            BarcodeFormat.ITF -> if (length % 2 == 0 && length >= 6) null else "ITF требует чётное количество цифр (минимум 6)"
            else -> null  // Для других форматов (QR и т.д.) валидация не нужна
        }
    }
    fun updateGalleryBitmap(bitmap: Bitmap) {
        _uiState.update { it.copy(galleryBitmap = bitmap) }
    }
}

object BarCodeGenerator {
    fun generateBarcode(
        data: String,
        format: BarcodeFormat,
        width: Int = when (format) {
            BarcodeFormat.CODE_128 -> 1000
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.ITF -> 1200  // Добавлены недостающие линейные форматы
            else -> 600
        },
        height: Int = when (format) {
            BarcodeFormat.CODE_128 -> 400
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.ITF -> 300

            else -> 600
        }
    ): Bitmap? {
        return try {
            val bitMatrix = MultiFormatWriter().encode(data, format, width, height)
            val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }
            bmp
        } catch (e: Exception) {
            e.printStackTrace()
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
