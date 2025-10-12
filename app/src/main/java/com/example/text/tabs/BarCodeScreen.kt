import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.text.viewmodel.BarCodeGenerator.BarcodeFromGalleryScreen
import com.example.text.viewmodel.BarCodeUiState
import com.example.text.viewmodel.BarCodeViewModel
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BarCodeScreen(viewModel: BarCodeViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    BarCodeScreenContent(
        state = state,
        onInputChanged = viewModel::updateInput,
        onBarcodeTypeChanged = viewModel::updateBarcodeType,
        onGenerateClicked = { viewModel.generateBarCode(state.inputText) },
        onGalleryBitmapUpdated = viewModel::updateGalleryBitmap,
        onDecodedTextChanged = viewModel::updateDecodedText
    )
}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BarCodeScreenContent(
    state: BarCodeUiState,
    onInputChanged: (String) -> Unit,
    onBarcodeTypeChanged: (String) -> Unit,
    onGenerateClicked: () -> Unit,
    onGalleryBitmapUpdated: (Bitmap) -> Unit,
    onDecodedTextChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SelectBarcodeType(
            selectedType = state.selectedBarcodeType,
            onTypeSelected = onBarcodeTypeChanged
        )

        InputText(state, onInputChanged)
        Text(
            text = getInputHint(state.selectedBarcodeType),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(
                top = 4.dp, start = 16.dp, end = 16.dp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        GenerateBarcodeButton(onGenerateClicked, state)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SavedBarcodeImage(state)
            }
            Box(modifier = Modifier.weight(1f)) {
                BarcodeFromGalleryScreen(
                    onGalleryBitmapUpdated = onGalleryBitmapUpdated,
                    onDecodedTextChanged = onDecodedTextChanged
                )
            }
        }
    }

}

@Composable
private fun ColumnScope.InputText(
    state: BarCodeUiState, onInputChanged: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    OutlinedTextField(
        value = state.inputText,
        onValueChange = { onInputChanged(it) },
        label = { Text("Enter text") },
        modifier = Modifier
            .height(screenHeight * 0.4f) // 40% вместо 50%
            .fillMaxWidth(),
        textStyle = LocalTextStyle.current,
    )
}
@Composable
private fun getInputHint(selectedType: String): String {  // Функция для подсказки (остаётся)
    return when (selectedType) {
        "ITF" -> "Требуется чётное количество цифр (минимум 6)"
        "CODE_128", "CODE_39", "CODE_93" -> "Требуются только цифры и буквы (A-Z)"
        else -> "Введите любой текст для генерации"  // Для QR_CODE, AZTEC и т.д.
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun SelectBarcodeType(
    selectedType: String, onTypeSelected: (String) -> Unit
) {
    val options = listOf(
        "QR_CODE", "AZTEC", "DATA_MATRIX", "CODE_128", "CODE_39", "CODE_93", "ITF"
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = {
            expanded = !expanded
        }) {
        OutlinedTextField(
            readOnly = true,
            value = selectedType,
            onValueChange = { },
            label = { Text("Categories") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = {
                expanded = false
            }) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        onTypeSelected(selectionOption)
                        expanded = false
                    }) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

@Composable
private fun SavedBarcodeImage(state: BarCodeUiState) {
    val context = LocalContext.current
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CardWithButton(
            onClick = {
                state.generatedBitmap?.let {
                    val success = saveBitmapToGallery(context, it)
                    Toast.makeText(
                        context,
                        if (success) "Saved!" else "First, create a Barcode.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            icon = Icons.Default.Create,
            contentDescription = "Save",
            text = "Save a barcode"
        )
    }
}

@Composable
private fun GenerateBarcodeButton(
    onGenerateClicked: () -> Unit,
    state: BarCodeUiState,
) {

    Button(
        onClick = onGenerateClicked, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Generate Barcode")
    }
    state.errorMessage?.let { error ->
        Text(
            text = error, color = Color.Red, modifier = Modifier.padding(top = 8.dp)
        )
    }
    state.generatedBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Generated barcode",
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp), /* .liquefiable(liquidState)*/
        )
    }
}

fun saveBitmapToGallery(
    context: Context, bitmap: Bitmap, fileName: String = "image_${System.currentTimeMillis()}.png"
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

@RequiresApi(Build.VERSION_CODES.P)
@Preview(showBackground = true)
@Composable
fun BarCodeScreenPreview() {
    val sampleBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    sampleBitmap.eraseColor(android.graphics.Color.GRAY)

    BarCodeScreenContent(
        state = BarCodeUiState(
            inputText = "Preview Text",
            selectedBarcodeType = "QR_CODE",
            generatedBitmap = sampleBitmap,
            errorMessage = "Sample error message"
        ),
        onInputChanged = {},
        onBarcodeTypeChanged = {},
        onGenerateClicked = {},
        onGalleryBitmapUpdated = {},
        onDecodedTextChanged = {})
}

@Composable
fun CardWithButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    text: String = ""
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(150.dp), // Заполняет весь родитель (Box), без 0.5f
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            ElevatedButton(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text,
                        Modifier.padding(bottom = 8.dp),
                        color = Color.Black,
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}