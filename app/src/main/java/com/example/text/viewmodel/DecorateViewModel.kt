package com.example.text.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow



data class DecorateUiState(
    val inputText: String = "",
    val resultText: String = "",
    val selectedDecorateStyle:Int = 0
)
class DecorateViewModel: ViewModel(){

    private val _uiState = MutableStateFlow(DecorateUiState())
    val uiState: StateFlow<DecorateUiState> = _uiState

     val decoStyles = listOf(
        // Стиль 1: Cool Font
        listOf(
            "-ˏˋ♥̩͙♥̩̩̥͙♥̩̥̩ ⑅   ⑅ ♥̩̥̩♥̩̩̥͙♥̩͙ˊˎ"
        ),
        // Стиль 2: Gothic
        listOf("(⌒_⌒)   (⌒_⌒)"),
        // Стиль 3: Bold
        listOf(
            ".｡*ﾟ+.*.｡   ﾟ+..｡*ﾟ+"
        ),
        listOf(
            "＊*•̩̩͙✩•̩̩͙*˚   ˚*•̩̩͙✩•̩̩͙*˚＊"
        ),
        listOf(
            "«·´`·.(*·.¸(`·.¸*   *¸.·´)¸.·*).·´`·»"
        ),
        // Стиль 2: Gothic
        listOf("☆★☆★→   ←☆★☆★"),
        // Стиль 3: Bold
        listOf(
            "(๑ ⌾⃝ ꇴ ⌾⃝ ๑)⊃━*:༅｡༅:*ﾟ:*:✼✿   ✿✼:*ﾟ:༅｡༅:*･ﾟﾟ･"
        ),
        listOf(
            ":۞:••:۞:••:«   »:••:۞:••:۞:"
        ),
        listOf(
            "╰┈➤   ❝"
        ),
        listOf(
            "୨⎯  ⎯୧ "
        ),
        listOf(
            "♪♫•*¨*•.¸¸❤   ❤¸¸.•*¨*•♫♪"
        ),
        listOf(
            "↤↤↤↤↤   ↦↦↦↦↦"
        ),
        listOf(
            "♡+*   *+♡"
        ),
        listOf(
            "(¯`·.¸¸.·´¯`·.¸¸.->  <-.¸¸.·´¯`·.¸¸.·´¯)"
        ),
        listOf(
            "♱★   ★♱"
        ),
        listOf(
            "૮꒰ ˶• ༝ •˶꒱ა   ૮꒰ ˶• ༝ •˶꒱ა"
        ),
        listOf(
            "❁•❁•❁•❁•❁•❁•   ❁•❁•❁•❁•❁•❁ "
        ),
        listOf(
            "༺♥༻❀༺♥༻   ༺♥༻❀༺♥༻"
        )
    )

    fun updateInputText(inputText: String){
        val decorateText = insertBetweenSpaces(inputText, _uiState.value.selectedDecorateStyle)
        _uiState.update { it.copy(inputText = inputText, resultText = decorateText) }
    }
    fun applyTextStyle(styleIndex: Int) {
        val validIndex = styleIndex.coerceIn(0, decoStyles.size - 1)
        _uiState.update {
            it.copy(
                selectedDecorateStyle = validIndex,
                resultText = insertBetweenSpaces(it.inputText, validIndex)
            )
        }
    }
    fun insertBetweenSpaces(text: String, style: Int): String {
        val decoText = decoStyles.getOrNull(style)?.getOrNull(0) ?: decoStyles[0][0]

        val firstSpace = decoText.indexOf(' ')
        val lastSpace = decoText.lastIndexOf(' ')

        return if (firstSpace != -1 && lastSpace != -1 && firstSpace != lastSpace) {
            decoText.substring(0, firstSpace + 1) + text + decoText.substring(lastSpace)
        } else {
            decoText // если нет подходящих пробелов — вернём как есть
        }
    }


}