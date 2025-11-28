package com.example.text.ui.theme


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Converter",
            icon = Icons.Default.Lock,
            route = "Home"
        ),
        BottomNavItem(
            label = "Fancy",
            icon = Icons.Default.Abc,
            route = "Stylish"
        ),
        BottomNavItem(
            label = "Decorate",
            icon = Icons.Default.EmojiEmotions,
            route = "Decorate"
        ),
        BottomNavItem(
            label = "Barcode",
            icon = Icons.Default.Image,
            route = "BarCode"
        )
    )
}