package com.example.text.tabs

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.text.viewmodel.DecorateViewModel

@Composable
fun DecorateScreen(viewModel: DecorateViewModel = viewModel()) {

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
            itemsIndexed(List(viewModel.decoStyles.size) { it })
            { index, styleName ->
                StylishTextItemDecor(
                    text = viewModel.insertBetweenSpaces(
                        state.inputText.ifEmpty { "Enter Text" },
                        index
                    ),
                    isSelected = index == state.selectedDecorateStyle,
                    onClick = { viewModel.applyTextStyle(index) },
                    onCopyClick = {
                        val displayText = viewModel.insertBetweenSpaces(
                            state.inputText.ifEmpty { "Enter Text" },
                            index
                        )
                        clipboardManager.setText(AnnotatedString(displayText))
                        Toast.makeText(context, "Text copied!", Toast.LENGTH_SHORT).show()
                    },
                    onShareClick = {
                        val displayText = viewModel.insertBetweenSpaces(
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
fun StylishTextItemDecor(
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
            .height(64.dp) // фиксированная высота карточки
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ), border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                IconButton(
                    onClick = onCopyClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = MaterialTheme.colorScheme.onSurface,
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
                        tint = MaterialTheme.colorScheme.onSurface,
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
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }


        }
    }
}

@Preview
@Composable
fun DecorateScreenPreview() {
    DecorateScreen()
}