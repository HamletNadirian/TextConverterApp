package com.example.text

import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.text.ui.theme.TabLayout
import com.example.text.viewmodel.BarCodeViewModel
import saveBitmapToGallery

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(viewModel: BarCodeViewModel = viewModel()) {
    val navController = rememberNavController()

    Column(Modifier.padding(8.dp)) {
        NavHost(navController, startDestination = "home", Modifier.fillMaxSize()) {
            composable("home") {
                TabLayout(
                    navController, viewModel
                )
            }

            composable("save") {
                val context = LocalContext.current
                val saveState by viewModel.uiState.collectAsState()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    SaveBarcodeScreen(
                        bitmap = saveState.generatedBitmap,
                        onBack = { navController.popBackStack() },
                        onSave = { bitmap ->
                            val success = saveBitmapToGallery(context, bitmap)
                            Toast.makeText(
                                context,
                                if (success) "Saved!" else "Save failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }

        }
    }
}

// Новый экран для сохранения
@Composable
@RequiresApi(Build.VERSION_CODES.P)
private fun SaveBarcodeScreen(
    bitmap: Bitmap?,
    onBack: () -> Unit,
    onSave: (Bitmap) -> Unit
) {
    if (bitmap == null) {
        // Fallback, если bitmap не готов
        onBack()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Кнопка назад
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "Generated Barcode",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Изображение баркода
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Barcode to save",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Кнопка сохранения
        Button(
            onClick = { onSave(bitmap) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save to Gallery")
        }

        Text(
            text = "Barcode will be saved in Pictures/BarcodeApp",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}