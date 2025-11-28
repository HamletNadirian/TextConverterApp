package com.example.text

import BarCodeScreen
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.text.tabs.CodecScreen
import com.example.text.tabs.DecorateScreen
import com.example.text.tabs.StylishScreen
import com.example.text.ui.theme.Constants
import com.example.text.viewmodel.BarCodeViewModel
import com.example.text.viewmodel.CodecViewModel
import com.example.text.viewmodel.DecorateViewModel
import com.example.text.viewmodel.StylishViewModel
import saveBitmapToGallery


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost() {
    val barCodeViewModel: BarCodeViewModel = viewModel()
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,

                )
        },
        content = { padding ->
            NavHostContainer(
                barCodeViewModel = barCodeViewModel,
                navController = navController,
                padding = padding
            )
        }
    )
}

@Composable
fun NavHostContainer(
    barCodeViewModel: BarCodeViewModel,
    navController: NavHostController,
    padding: PaddingValues
) {
    val codecViewModel: CodecViewModel = viewModel()
    val stylishViewModel: StylishViewModel = viewModel()
    val decorateViewModel: DecorateViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {

            composable("Home") {
                CodecScreen(viewModel = codecViewModel)
            }
            composable("Stylish") {
                StylishScreen(viewModel = stylishViewModel)
            }

            composable("Decorate") {
                DecorateScreen(viewModel = decorateViewModel)
            }
            composable("BarCode") {
                BarCodeScreen(navController, viewModel = barCodeViewModel)
            }
            composable("save") {
                val context = LocalContext.current
                val saveState by barCodeViewModel.uiState.collectAsState()
                // Добавим отладку
                LaunchedEffect(saveState.generatedBitmap) {
                    println("SaveScreen: Received bitmap = ${saveState.generatedBitmap != null}")
                    println("SaveScreen: ViewModel instance = $barCodeViewModel")
                }
                /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {*/
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
    )
}


@Composable
fun BottomNavigationBar(
    navController: NavHostController,
) {
    NavigationBar(

        containerColor = Color(0xFFFCF5FD)
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Constants.BottomNavItems.forEach { navItem ->

            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route)

                },
                icon = {
                    Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                },

                label = {
                    Text(text = navItem.label, color = Color.Black)
                }, alwaysShowLabel = true,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black, // Icon color when selected
                    unselectedIconColor = Color.Black, // Icon color when not selected
                    selectedTextColor = Color.Black, // Label color when selected
                    indicatorColor = Color(0xFF3F51B5) // Highlight color for selected item
                )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun AppNavHostPreview() {
    AppNavHost()
}

@Composable
fun SaveBarcodeScreen(
    bitmap: Bitmap?,
    onBack: () -> Unit,
    onSave: (Bitmap) -> Unit
) {
    if (bitmap == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No barcode generated",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go Back")
            }
        }
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
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
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
            modifier = Modifier.fillMaxWidth(),
            enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        ) {
            Text(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) "Save to Gallery" else "Requires Android 9.0+")
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

@RequiresApi(Build.VERSION_CODES.P)
@Preview
@Composable
private fun SaveBarcodeScreenPreview() {
    // Create a dummy bitmap for the preview
    val previewBitmap = Bitmap.createBitmap(300, 100, Bitmap.Config.ARGB_8888).apply {
        eraseColor(MaterialTheme.colorScheme.primary.toArgb())
    }

    MaterialTheme {
        SaveBarcodeScreen(bitmap = previewBitmap, onBack = {}, onSave = {})
    }
}

