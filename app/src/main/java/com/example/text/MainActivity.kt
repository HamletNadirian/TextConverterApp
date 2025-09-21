package com.example.text


import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.text.tabs.TabsContent
import com.example.text.ui.theme.TextTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
//Test
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TextTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.systemBars.asPaddingValues())
                ) {
                    TabLayout()
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun TabLayout() {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val tabData = listOf(
        "Codec" to Icons.Default.Lock,
        "Stylish" to Icons.Default.Create,
        "Decorate" to Icons.Default.Settings,
        "Barcode" to Icons.Default.Star
    )
    val scope = rememberCoroutineScope()

    Scaffold(

    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 0.dp
            ) {
                tabData.forEachIndexed { index, (title, icon) ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) },
                        icon = { Icon(imageVector = icon, contentDescription = title) },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                }
            }

            TabsContent(pagerState = pagerState)
        }
    }
}

enum class CipherType {
    BINARY, ASCII, BASE64,
    MD5, SHA256, HEX, UTF8, CRC32, CRC16
}
