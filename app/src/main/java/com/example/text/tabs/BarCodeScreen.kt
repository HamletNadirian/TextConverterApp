package com.example.text.tabs

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.text.viewmodel.BarCodeViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.text.viewmodel.BarCodeGenerator.BarcodeFromGalleryScreen

import java.io.IOException


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BarCodeScreen(viewModel: BarCodeViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.inputText,
            onValueChange = { viewModel.updateInput(it) },
            label = { Text("Enter text") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current,
        )

        Button(
            onClick = {
                viewModel.generateBarCode(state.inputText)
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Generate Barcode")
        }

        state.generatedBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Generated barcode",
                modifier = Modifier.padding(top = 16.dp)
            )


        }
        val context = LocalContext.current
        Button(onClick = {
            state.generatedBitmap?.let {
                val success = saveBitmapToGallery(context, it)
                Toast.makeText(
                    context,
                    if (success) "Сохранено!" else "Сначала создайте QR-код",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text("Сохранить")
        }
        BarcodeFromGalleryScreen()

    }
}
@Composable
fun UiGallery(
    bitmap: Bitmap?,
    barcodeText: String?,
    onImagePickClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = onImagePickClick) {
            Text("Выбрать изображение")
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Загруженное изображение",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        barcodeText?.let {
            Text(
                text = "Результат: $it",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}



@RequiresApi(Build.VERSION_CODES.P)
@Preview
@Composable
fun BarCodeScreenPreview() {
    BarCodeScreen()
}

fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    fileName: String = "image_${System.currentTimeMillis()}.png"
): Boolean {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/BarcodeApp")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val uri =
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return false

    return try {
        resolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
        true
    } catch (e: IOException) {
        false
    }
}