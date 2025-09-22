package com.example.text.tabs


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.text.components.CipherSelector
import com.example.text.ui.theme.TextTheme
import com.example.text.viewmodel.CodecViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CodecScreen(viewModel: CodecViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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

