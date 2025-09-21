package com.example.text.tabs


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.text.components.CipherSelector
import com.example.text.viewmodel.CodecViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.text.ui.theme.TextTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CodecScreen(viewModel: CodecViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.inputText,
            onValueChange = { viewModel.encryptFromInput(it) },
            label = { Text("Enter text") },
            modifier = Modifier
                .weight(1f)       // занимает 1 часть
                .fillMaxWidth(),  // по ширине весь экран
            singleLine = false
        )

        CipherSelector(
            selected = state.selectedCipher,
            onSelected = viewModel::onCipherSelected,

        )

        OutlinedTextField(
            value = state.resultText,
            onValueChange = { viewModel.decryptFromOutput(it) },
            label = { Text("Result") },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            singleLine = false
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CodecScreenPreview() {
    TextTheme {
        CodecScreen()
    }
}

