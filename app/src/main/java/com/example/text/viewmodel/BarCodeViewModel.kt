package com.example.text.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.ViewModel
import com.example.text.viewmodel.BarCodeGenerator.generateBarcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.MultiFormatWriter
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
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
    fun updateDecodedText(decodedText: String) {
        _uiState.value = _uiState.value.copy(inputText = decodedText)
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
        val validationError = validateInput(text, format)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
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

@Composable
fun UiGallery(
    onImagePickClick: () -> Unit
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onImagePickClick, Modifier.fillMaxWidth()) {
            Text("Select an image")
        }
        Spacer(modifier = Modifier.height(16.dp))
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
    fun BarcodeFromGalleryScreen(
        onGalleryBitmapUpdated: (Bitmap) -> Unit,
        onDecodedTextChanged: (String) -> Unit
    ) {
        val context = LocalContext.current
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                try {
                    val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                            .copy(Bitmap.Config.ARGB_8888, false) // важно для ZXing
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }

                    bitmap = bmp
                    onGalleryBitmapUpdated(bmp)

                    val image = InputImage.fromBitmap(bmp, 0)
                    val scanner = BarcodeScanning.getClient()

                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            val rawText = if (barcodes.isNotEmpty()) {
                                barcodes.first().rawValue ?: ""
                            } else {
                                decodeBarcodeWithZxing(bmp) ?: ""
                            }

                            // Если Code39 — декодируем Extended
                            val decodedText = if (rawText.isNotEmpty() && isCode39(rawText)) {
                                decodeExtendedCode39(rawText)
                            } else rawText

                            // Обновляем ViewModel, TextField получит результат
                            onDecodedTextChanged(decodedText)
                        }


                } catch (e: IOException) {
                    onDecodedTextChanged("Image loading error: ${e.localizedMessage}")
                } catch (e: Exception) {
                    onDecodedTextChanged("An error occurred: ${e.localizedMessage}")
                }
            }
        }

        UiGallery(
            onImagePickClick = { launcher.launch("image/*") }
        )
    }

    fun isCode39(text: String): Boolean {
        return true
    }

    fun decodeBarcodeWithZxing(bitmap: Bitmap): String? {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, pixels)
        val bitmapBinary = BinaryBitmap(HybridBinarizer(source))

        val reader = MultiFormatReader().apply {
            setHints(
                mapOf(
                    DecodeHintType.POSSIBLE_FORMATS to listOf(
                        BarcodeFormat.CODE_39,
                        BarcodeFormat.CODE_93,
                        BarcodeFormat.CODE_128,
                        BarcodeFormat.ITF,
                        BarcodeFormat.QR_CODE
                    ),
                    DecodeHintType.CHARACTER_SET to "UTF-8"
                )
            )
        }

        return try {
            val result = reader.decode(bitmapBinary)
            if (result.barcodeFormat == BarcodeFormat.CODE_39) {
                decodeExtendedCode39(result.text)
            } else result.text
        } catch (e: NotFoundException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    // Распознавание Code39 Extended (+A -> a, /, % -> спецсимволы)
    fun decodeExtendedCode39(input: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < input.length) {
            val c = input[i]
            if (c == '+' && i + 1 < input.length) {
                sb.append(input[i + 1].lowercaseChar())
                i += 2
            } else if (c == '%' && i + 1 < input.length) {
                val map = mapOf(
                    'A' to ' ', 'B' to '-', 'C' to '.', 'D' to '<',
                    'E' to '>', 'F' to '[', 'G' to ']', 'H' to '{',
                    'I' to '}', 'J' to '*'
                )
                sb.append(map[input[i + 1]] ?: '?')
                i += 2
            } else if (c == '/' && i + 1 < input.length) {
                val map = mapOf(
                    'A' to '!', 'B' to '"', 'C' to '#', 'D' to '$',
                    'E' to '%', 'F' to '&', 'G' to '\'', 'H' to '(',
                    'I' to ')', 'J' to '*', 'K' to '+', 'L' to ',',
                    'M' to '-', 'N' to '.', 'O' to '/'
                )
                sb.append(map[input[i + 1]] ?: '?')
                i += 2
            } else {
                sb.append(c)
                i++
            }
        }
        return sb.toString()
    }


}
