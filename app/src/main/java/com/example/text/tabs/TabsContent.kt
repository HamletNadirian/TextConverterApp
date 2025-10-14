package com.example.text.tabs

import BarCodeScreen
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.text.viewmodel.BarCodeViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabsContent(
    pagerState: PagerState,
    navController: NavHostController,
    viewModel: BarCodeViewModel = viewModel()
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) { page ->
        when (page) {
            0 -> CodecScreen()
            1 -> StylishScreen()
            2 -> DecorateScreen()
            3 -> BarCodeScreen(navController = navController, viewModel = viewModel)

        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun TabsContentPreview() {
    val pagerState = rememberPagerState(initialPage = 0) { 4 }
    TabsContent(
        pagerState = pagerState,
        navController = NavHostController(LocalContext.current)
    )
}

