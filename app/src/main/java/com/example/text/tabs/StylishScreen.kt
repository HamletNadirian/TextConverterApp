package com.example.text.tabs

import androidx.compose.ui.tooling.preview.Preview

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.text.ui.theme.TextTheme
import com.example.text.viewmodel.StylishViewModel

@Composable
fun StylishScreen(viewModel: StylishViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = state.inputText,
            onValueChange = { viewModel.updateInputText(it) },
            label = { Text("Enter text") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            maxLines = 1,
            textStyle = LocalTextStyle.current
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(List(viewModel.fancyStyles.size) { it })
            { index, styleName ->
                StylishTextItem(
                    text = viewModel.toFancyUnicode(
                        state.inputText.ifEmpty { "Enter Text" },
                        index
                    ),
                   // styleName = styleName,
                    isSelected = index == state.selectedStyle,
                    onClick = { viewModel.changeStyle(index) },
                    onCopyClick = {
                        val displayText = viewModel.toFancyUnicode(
                            state.inputText.ifEmpty { "Enter Text" },
                            index
                        )
                        clipboardManager.setText(AnnotatedString(displayText))
                        Toast.makeText(context, "Text copied!", Toast.LENGTH_SHORT).show()
                    },
                    onShareClick = {
                        val displayText = viewModel.toFancyUnicode(
                            state.inputText.ifEmpty { "Enter Text" },
                            index
                        )
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, displayText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share text"))
                    }
                )
            }
        }
    }
}

@Composable
fun StylishTextItem(
    text: String,
    //styleName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE0F0FF) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically        )
        {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                IconButton(
                    onClick = onCopyClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Copy",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

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
            }
            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),

            ) {
             /*   Text(
                    text = styleName,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )*/
                Text(
                    text = text,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun StylishScreenPreview() {
    TextTheme {
        StylishScreen()
    }
}
