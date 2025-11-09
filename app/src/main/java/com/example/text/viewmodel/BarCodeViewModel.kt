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
import com.example.text.viewmodel.BarCodeGenerator.createBarcodeBitmap
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
import com.journeyapps.barcodescanner.ScanOptions
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
    val textResultFromCamera = mutableStateOf("")

    init {
        textResultFromCamera.value.let { newText ->
            _uiState.update { it.copy(inputText = newText) }
        }
    }

    fun updateInput(text: String) {
        val currentState = _uiState.value
        val filtered = if (listOf("ITF").contains(currentState.selectedBarcodeType)) {
            text.filter { char: Char -> char.isDigit() }
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
        if (text.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Введите текст для генерации") }
            return
        }
        val currentState = _uiState.value
        val format = try {
            BarcodeFormat.valueOf(currentState.selectedBarcodeType)
        } catch (e: IllegalArgumentException) {
            _uiState.update { it.copy(errorMessage = "Неподдерживаемый формат баркода") }
            return
        }
        val validationError = validateInput(text, format)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }
        val bitmap = createBarcodeBitmap(
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
        //  _uiState.update { it.copy(generatedBitmap = bitmap) }
    }

    private fun validateInput(text: String, format: BarcodeFormat): String? {
        val digitsOnly = text.filter { char: Char -> char.isDigit() }
        val length = digitsOnly.length
        return when (format) {
            BarcodeFormat.ITF -> if (length % 2 == 0 && length >= 6) null else "ITF требует чётное количество цифр (минимум 6)"
            else -> null
        }
    }

    fun updateGalleryBitmap(bitmap: Bitmap) {
        _uiState.update { it.copy(galleryBitmap = bitmap) }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun UiGalleryPreview() {
    UiGallery(
        onImagePickClick = {}
    )
}

object BarCodeGenerator {
    fun createBarcodeBitmap(
        data: String,
        format: BarcodeFormat,
        width: Int = when (format) {
            BarcodeFormat.CODE_128 -> 1000
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.ITF -> 1200

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
    fun BarcodeFromScanScreen(
        onDecodedTextChanged: (String) -> Unit
    ) {
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

    fun startScan(launcher: androidx.activity.result.ActivityResultLauncher<ScanOptions>) {
        val options = ScanOptions().apply {
            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            options.setPrompt("Scan a barcode")
            options.setCameraId(0)
            options.setBeepEnabled(false)
            options.setBarcodeImageEnabled(false)
        }
        launcher.launch(options)
    }

    @Composable
    fun BarcodeFromGalleryScreenPreview() {
        BarcodeFromGalleryScreen(
            onGalleryBitmapUpdated = {},
            onDecodedTextChanged = {}
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
