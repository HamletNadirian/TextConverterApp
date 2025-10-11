import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.text.viewmodel.BarCodeGenerator.BarcodeFromGalleryScreen
import com.example.text.viewmodel.BarCodeUiState
import com.example.text.viewmodel.BarCodeViewModel
import java.io.IOException


@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BarCodeScreen(viewModel: BarCodeViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

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
            onTypeSelected = { viewModel.updateBarcodeType(it) }
        )

        InputText(state, viewModel)
        Text(
            text = getInputHint(state.selectedBarcodeType),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(
                top = 4.dp,
                start = 16.dp,
                end = 16.dp
            ),  // Отступы для выравнивания под полем
            color = MaterialTheme.colorScheme.onSurfaceVariant  // Цвет подсказки
        )


        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GenerateBarcodeButton(viewModel, state)
            SavedBarcodeImage(state)
            BarcodeFromGalleryScreen(viewModel)
        }
    }
}

@Composable
private fun ColumnScope.InputText(
    state: BarCodeUiState,
    viewModel: BarCodeViewModel
) {
    OutlinedTextField(
        value = state.inputText,
        onValueChange = { viewModel.updateInput(it) },
        label = { Text("Enter text") },
        modifier = Modifier
            .weight(0.5f)       // занимает 1 часть
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
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val options = listOf(
        "QR_CODE", "AZTEC", "DATA_MATRIX",
        "CODE_128", "CODE_39", "CODE_93", "ITF"
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedType,
            onValueChange = { },
            label = { Text("Categories") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        onTypeSelected(selectionOption)
                        expanded = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

@Composable
private fun SavedBarcodeImage(state: BarCodeUiState) {
    val context = LocalContext.current
    Button(onClick = {
        state.generatedBitmap?.let {
            val success = saveBitmapToGallery(context, it)
            Toast.makeText(
                context,
                if (success) "Saved!" else "First, create a Barcode.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }, Modifier.fillMaxWidth()) {
        Text("Saved")
    }
}

@Composable
private fun GenerateBarcodeButton(
    viewModel: BarCodeViewModel,
    state: BarCodeUiState,
) {

    Button(
        onClick = {
            viewModel.generateBarCode(state.inputText)
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("Generate Barcode")
    }
    state.errorMessage?.let { error ->
        Text(
            text = error,
            color = Color.Red,
            modifier = Modifier.padding(top = 8.dp)
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