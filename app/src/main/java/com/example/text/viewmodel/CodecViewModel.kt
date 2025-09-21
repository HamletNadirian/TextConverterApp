package com.example.text.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.text.CipherType
import com.example.text.ciphers.crc16_checksum.CRC16CheckSum
import com.example.text.ciphers.crc32_checksum.CRC32CheckSum
import com.example.text.ciphers.md5_hash.MD5
import nadirian.hamlet.android.encdecapp.model.american_standard_code_for_information_interchange.ASCIIEncryptor
import nadirian.hamlet.android.encdecapp.model.base64.Base64Encoding
import nadirian.hamlet.android.encdecapp.model.sha256_hash.SHA256
import nadirian.hamlet.android.encdecapp.model.string_to_binary.StringToBinary
import nadirian.hamlet.android.encdecapp.model.string_to_hex.StringToHex
import nadirian.hamlet.android.encdecapp.model.utf_8_code.UTF8Encryptor


data class CodecUiState(
    val inputText: String = "",
    val selectedCipher: CipherType = CipherType.MD5,
    val resultText: String = ""
)

class CodecViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CodecUiState())
    val uiState: StateFlow<CodecUiState> = _uiState

    @RequiresApi(Build.VERSION_CODES.O)
    fun onCipherSelected(cipher: CipherType) {
        _uiState.update { it.copy(selectedCipher = cipher) }
        updateResultEncryption()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptFromInput(input: String) {
        _uiState.update { it.copy(inputText = input) }
        updateResultEncryption()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decryptFromOutput(output: String) {
        _uiState.update { it.copy(resultText = output) }
        updateResultDecryption()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateResultEncryption() {
        val input = _uiState.value.inputText
        val cipher = _uiState.value.selectedCipher

        val result = when (cipher) {
            CipherType.MD5 -> MD5.md5(input)
            CipherType.CRC32 -> CRC32CheckSum.crc32checksum(input)
            CipherType.CRC16 -> CRC16CheckSum.crc16checksum(input)
            CipherType.BASE64 -> Base64Encoding.encode(input)
            CipherType.BINARY -> StringToBinary.convertStringToBinary(input)
            CipherType.ASCII -> ASCIIEncryptor.stringToACII(input)
            CipherType.HEX -> StringToHex.convertStringToHex(input)
            CipherType.UTF8 -> UTF8Encryptor.stringToUTF8(input)
            CipherType.SHA256 -> SHA256.sha256(input)
            else -> input
        }

        _uiState.update { it.copy(resultText = result.toString()) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateResultDecryption() {
        val result = _uiState.value.resultText
        val cipher = _uiState.value.selectedCipher

        val decoded = when (cipher) {
            CipherType.BASE64 -> Base64Encoding.decode(result)
            CipherType.BINARY -> StringToBinary.strToBinary(result)
            CipherType.ASCII -> ASCIIEncryptor.asciiToString(result)
            CipherType.HEX -> StringToHex.convertHexToString(result)
            CipherType.UTF8 -> UTF8Encryptor.decodeUTF8ToString(result)

            else -> result
        }

        _uiState.update { it.copy(inputText = decoded.toString()) }
    }
}