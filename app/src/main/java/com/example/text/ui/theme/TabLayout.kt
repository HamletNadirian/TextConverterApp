package com.example.text.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.text.tabs.TabsContent
import com.example.text.viewmodel.BarCodeViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun TabLayout(navController: NavHostController, viewModel: BarCodeViewModel = viewModel()) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val tabData = listOf(
        "Codec" to Icons.Default.Lock,
        "Stylish" to Icons.Default.Create,
        "Decorate" to Icons.Default.Settings,
        "Barcode" to Icons.Default.Star
    )
    val scope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
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
                        modifier = Modifier
                            .weight(1f) // Равномерное распределение ширины вкладок
                            .minimumInteractiveComponentSize() // Минимальный размер для клика
                    )
                }
            }

            TabsContent(pagerState = pagerState, navController, viewModel = viewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Preview
@Composable
fun TabLayoutPreview() {
    TabLayout(navController = NavHostController(LocalContext.current))
}
