package com.example.text.tabs


import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
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
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = 16.dp,
                end = 16.dp,
                top = 16.dp
            )
            .imePadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*     OutlinedTextField(
                 value = state.inputText,
                 onValueChange = { viewModel.encryptFromInput(it) },
                 label = { Text("Enter text") },
                 modifier = Modifier
                     .weight(1f)
                     .fillMaxWidth(),
                 singleLine = false

             )*/


        /*    OutlinedTextField(
                value = state.resultText,
                onValueChange = { viewModel.decryptFromOutput(it) },
                label = { Text("Result") },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                singleLine = false
            )*/
        state.errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        CustomOutlinedTextField(
            value = state.inputText,
            onValueChange = { viewModel.encryptFromInput(it) },
            label = "Enter text",
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            singleLine = false,
            onCopyClick = {
                if (state.inputText.isNotBlank()) {
                    clipboardManager.setText(AnnotatedString(state.inputText))
                    Toast.makeText(context, "input text copied!", Toast.LENGTH_SHORT).show()
                }
            },
            onShareClick = {
                val textToShare = state.inputText.ifEmpty { "No text to share" }
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, textToShare)
                }
                context.startActivity(Intent.createChooser(intent, "Share text"))
            },
            onClearClick = {
                viewModel.encryptFromInput("")
                viewModel.decryptFromOutput("")
                Toast.makeText(context, "Text cleared!", Toast.LENGTH_SHORT).show()
            },
            onPasteClick = {
                val textToPaste = clipboardManager.getText()?.text
                if (!textToPaste.isNullOrEmpty()) {
                    viewModel.encryptFromInput(textToPaste)
                    Toast.makeText(context, "Pasted!", Toast.LENGTH_SHORT).show()
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Choose method",
                modifier = Modifier.padding(16.dp)
            )
            CipherSelector(
                selected = state.selectedCipher,
                onSelected = viewModel::onCipherSelected,
            )
        }

        CustomOutlinedTextField(
            value = state.resultText,
            onValueChange = { viewModel.decryptFromOutput(it) },
            label = "Result",
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            singleLine = false,
            onCopyClick = {
                if (state.resultText.isNotBlank()) {
                    clipboardManager.setText(AnnotatedString(state.resultText))
                    Toast.makeText(context, "Result text copied!", Toast.LENGTH_SHORT).show()
                }
            },
            onShareClick = {
                var ecnryptedText = state.resultText.ifEmpty { "No text to share" }
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, ecnryptedText)
                }
                context.startActivity(Intent.createChooser(intent, "Share text"))
            },
            onClearClick = {
                viewModel.encryptFromInput("")
                viewModel.decryptFromOutput("")
                Toast.makeText(context, "Text cleared!", Toast.LENGTH_SHORT).show()
            },
            onPasteClick = {
                val textToPaste = clipboardManager.getText()?.text
                if (!textToPaste.isNullOrEmpty()) {
                    viewModel.decryptFromOutput(textToPaste)
                    Toast.makeText(context, "Pasted!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    onCopyClick: () -> Unit,
    onPasteClick: () -> Unit,
    onShareClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Row(
        modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    )
    {
        Column {
            IconButton(
                onClick = onCopyClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .width(8.dp)
                    .height(8.dp)
            )
            IconButton(
                onClick = onPasteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentPaste,
                    contentDescription = "Paste",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .width(8.dp)
                    .height(8.dp)
            )
            IconButton(
                onClick = onShareClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .width(8.dp)
                    .height(8.dp)
            )

            IconButton(
                onClick = onClearClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = modifier,
            singleLine = singleLine
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

