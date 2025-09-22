package com.example.text

import com.example.text.ciphers.crc16_checksum.CRC16CheckSum
import com.example.text.ciphers.crc32_checksum.CRC32CheckSum
import com.example.text.ciphers.md5_hash.MD5
import nadirian.hamlet.android.encdecapp.model.american_standard_code_for_information_interchange.ASCIIEncryptor
import nadirian.hamlet.android.encdecapp.model.base64.Base64Encoding
import nadirian.hamlet.android.encdecapp.model.sha256_hash.SHA256
import nadirian.hamlet.android.encdecapp.model.string_to_binary.StringToBinary
import nadirian.hamlet.android.encdecapp.model.string_to_hex.StringToHex
import nadirian.hamlet.android.encdecapp.model.utf_8_code.UTF8Encryptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test


class EncDecAppUnitTests {

    // *******************************************
    //Test for UTF8
    // *******************************************

    @Test
    fun utf8_stringToUTF8_basicString() {
        val input = "Hello"
        val result = UTF8Encryptor.stringToUTF8(input)
        val expected = "72, 101, 108, 108, 111"
        assertEquals(expected, result)
    }

    @Test
    fun utf8_stringToUTF8_emptyString() {
        val input = ""
        val result = UTF8Encryptor.stringToUTF8(input)
        assertEquals("", result)
    }

    @Test
    fun utf8_stringToUTF8_specialCharacters() {
        val input = "Hello, 世界!"
        val result = UTF8Encryptor.stringToUTF8(input)
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun utf8_decodeUTF8ToString_validBytes() {
        val input = "72, 101, 108, 108, 111"
        val result = UTF8Encryptor.decodeUTF8ToString(input)
        assertEquals("Hello", result)
    }

    @Test
    fun utf8_decodeUTF8ToString_invalidBytes() {
        val input = "invalid, bytes"
        val result = UTF8Encryptor.decodeUTF8ToString(input)
        assertEquals("", result)
    }

    @Test
    fun utf8_decodeUTF8ToString_emptyString() {
        val input = ""
        val result = UTF8Encryptor.decodeUTF8ToString(input)
        assertEquals("", result)
    }

    @Test
    fun utf8_encodeDecodeRoundTrip() {
        val original = "Hello World! 123"
        val encoded = UTF8Encryptor.stringToUTF8(original)
        val decoded = UTF8Encryptor.decodeUTF8ToString(encoded)
        assertEquals(original, decoded)
    }
    // *******************************************
    //HEX
    // *******************************************

    @Test
    fun hex_processHex_encryption() {
        val input = "Hello"
        val result = StringToHex.processHex(input, true)
        assertEquals("48656c6c6f", result)
    }

    @Test
    fun hex_processHex_decryption() {
        val input = "48656c6c6f"
        val result = StringToHex.processHex(input, false)
        assertEquals("Hello", result)
    }

    @Test
    fun hex_convertStringToHex_basicString() {
        val input = "Hello"
        val result = StringToHex.convertStringToHex(input)
        assertEquals("48656c6c6f", result)
    }

    @Test
    fun hex_convertHexToString_validHex() {
        val input = "48656c6c6f"
        val result = StringToHex.convertHexToString(input)
        assertEquals("Hello", result)
    }

    @Test
    fun hex_convertHexToString_invalidHex() {
        val input = "invalid_hex_string"
        val result = StringToHex.convertHexToString(input)
        assertEquals("Invalid Hex format", result)
    }

    @Test
    fun hex_convertHexToString_nullInput() {
        val result = StringToHex.convertHexToString(null)
        assertEquals("Invalid Hex format", result)
    }

    @Test
    fun hex_convertHexToString_withSpaces() {
        val input = "48 65 6c 6c 6f"
        val result = StringToHex.convertHexToString(input)
        assertEquals("Hello", result)
    }

    @Test
    fun hex_encodeDecodeRoundTrip() {
        val original = "Test String 123!"
        val encoded = StringToHex.convertStringToHex(original)
        val decoded = StringToHex.convertHexToString(encoded)
        assertEquals(original, decoded)
    }
    // *******************************************
    //StringToBinary
    // *******************************************

    @Test
    fun binary_convertStringToBinary_basicString() {
        val input = "Hi"
        val result = StringToBinary.convertStringToBinary(input)
        assertEquals("01001000 01101001", result)
    }

    @Test
    fun binary_convertStringToBinary_emptyString() {
        val result = StringToBinary.convertStringToBinary("")
        assertEquals("", result)
    }

    @Test
    fun binary_processBinary_encryption() {
        val input = "A"
        val result = StringToBinary.processBinary(input, true)
        assertEquals("01000001", result)
    }

    @Test
    fun binary_strToBinary_validBinary() {
        val input = "01001000 01101001"
        val result = StringToBinary.strToBinary(input)
        assertTrue(result.contains("H"))
        assertTrue(result.contains("i"))
    }

    @Test
    fun binary_prettyBinary_formatting() {
        val input = "0100100001101001"
        val result = StringToBinary.prettyBinary(input, 8, " ")
        assertEquals("01001000 01101001", result)
    }
    // *******************************************
    // Tests for SHA256
    // *******************************************

    @Test
    fun sha256_basicString() {
        val input = "hello"
        val result = SHA256.sha256(input)
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", result)
    }

    @Test
    fun sha256_emptyString() {
        val input = ""
        val result = SHA256.sha256(input)
        assertEquals(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            result
        ) // SHA256 всегда 64 символа в hex
    }

    @Test
    fun sha256_sameInputSameOutput() {
        val input = "test"
        val result1 = SHA256.sha256(input)
        val result2 = SHA256.sha256(input)
        assertEquals(result1, result2)
    }

    @Test
    fun sha256_differentInputDifferentOutput() {
        val result1 = SHA256.sha256("test1")
        val result2 = SHA256.sha256("test2")
        assertNotEquals(result1, result2)
    }

    // *******************************************
    // Tests for MD5
    // *******************************************
    @Test
    fun md5_basicString() {
        val input = "hello"
        val result = MD5.md5(input)
        assertEquals("5d41402abc4b2a76b9719d911017c592", result) // MD5 всегда 32 символа в hex
    }

    @Test
    fun md5_emptyString() {
        val input = ""
        val result = MD5.md5(input)
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", result)
    }

    @Test
    fun md5_sameInputSameOutput() {
        val input = "test"
        val result1 = MD5.md5(input)
        val result2 = MD5.md5(input)
        assertEquals(result1, result2)
    }
    // *******************************************
    // Tests для CRC32CheckSum
    // *******************************************

    @Test
    fun crc32_basicString() {
        val input = "hello"
        val result = CRC32CheckSum.crc32checksum(input)
        assertEquals(907060870, result)

    }

    @Test
    fun crc32_emptyString() {
        val input = ""
        val result = CRC32CheckSum.crc32checksum(input)
        assertEquals(0L, result)
    }

    @Test
    fun crc32_sameInputSameOutput() {
        val input = "test"
        val result1 = CRC32CheckSum.crc32checksum(input)
        val result2 = CRC32CheckSum.crc32checksum(input)
        assertEquals(result1, result2)
    }

    @Test
    fun crc32_differentInputDifferentOutput() {
        val result1 = CRC32CheckSum.crc32checksum("test1")
        val result2 = CRC32CheckSum.crc32checksum("test2")
        assertNotEquals(result1, result2)
    }
    // ===========================================
    // Tests для CRC16CheckSum
    // ===========================================

    @Test
    fun crc16_basicString() {
        val input = "hello"
        val result = CRC16CheckSum.crc16checksum(input)
        assertEquals(13522, result)
    }

    @Test
    fun crc16_emptyString() {
        val input = ""
        val result = CRC16CheckSum.crc16checksum(input)
        assertEquals(0, result)
    }

    @Test
    fun crc16_sameInputSameOutput() {
        val input = "test"
        val result1 = CRC16CheckSum.crc16checksum(input)
        val result2 = CRC16CheckSum.crc16checksum(input)
        assertEquals(result1, result2)
    }

    // *******************************************
    // Tests для Base64Encoding
    // *******************************************
    @Test
    fun base64_encode_basicString() {
        val input = "Hello World"
        val result = Base64Encoding.encode(input)
        assertEquals("SGVsbG8gV29ybGQ=", result)
    }

    @Test
    fun base64_decode_validBase64() {
        val input = "SGVsbG8gV29ybGQ="
        val result = Base64Encoding.decode(input)
        assertEquals("Hello World", result)
    }

    @Test
    fun base64_encode_emptyString() {
        val input = ""
        val result = Base64Encoding.encode(input)
        assertEquals("", result)
    }

    @Test
    fun base64_encodeDecodeRoundTrip() {
        val original = "This is a test message with special chars: !@#$%^&*()"
        val encoded = Base64Encoding.encode(original)
        val decoded = Base64Encoding.decode(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun base64_encode_specialCharacters() {
        val input = "Hello, 世界! 🌍"
        val encoded = Base64Encoding.encode(input)
        val decoded = Base64Encoding.decode(encoded)
        assertEquals(input, decoded)
    }
    // *******************************************
    // Test для ASCIIEncryptor
    // *******************************************

    @Test
    fun ascii_processASCII_encryption() {
        val input = "Hello"
        val result = ASCIIEncryptor.processASCII(input, true)
        assertEquals("72 101 108 108 111", result)
    }

    @Test
    fun ascii_processASCII_decryption() {
        val input = "72 101 108 108 111"
        val result = ASCIIEncryptor.asciiToString(input)
        assertEquals("Hello", result)
    }

    @Test
    fun ascii_stringToACII_basicString() {
        val input = "ABC"
        val result = ASCIIEncryptor.stringToACII(input)
        assertEquals("65 66 67", result)
    }

    @Test
    fun ascii_stringToACII_emptyString() {
        val input = ""
        val result = ASCIIEncryptor.stringToACII(input)
        assertEquals("", result)
    }

    @Test
    fun ascii_asciiToString_validCodes() {
        val input = "65 66 67"
        val result = ASCIIEncryptor.asciiToString(input)
        assertEquals("ABC", result)
    }

    @Test
    fun ascii_asciiToString_invalidCodes() {
        val input = "999999"
        val result = ASCIIEncryptor.asciiToString(input)
        assertEquals("", result)
    }

    @Test
    fun ascii_specialCharacters() {
        val input = "!@#"
        val encoded = ASCIIEncryptor.stringToACII(input)
        assertEquals("33 64 35", encoded)
    }

    // ===========================================
    // Integrations Tests
    // ===========================================

    @Test
    fun integration_multipleEncodingsRoundTrip() {
        val original = "Hello World"

        // UTF8 round trip
        val utf8Encoded = UTF8Encryptor.stringToUTF8(original)
        val utf8Decoded = UTF8Encryptor.decodeUTF8ToString(utf8Encoded)
        assertEquals(original, utf8Decoded)

        // Hex round trip
        val hexEncoded = StringToHex.convertStringToHex(original)
        val hexDecoded = StringToHex.convertHexToString(hexEncoded)
        assertEquals(original, hexDecoded)

        // Base64 round trip
        val base64Encoded = Base64Encoding.encode(original)
        val base64Decoded = Base64Encoding.decode(base64Encoded)
        assertEquals(original, base64Decoded)
    }

    @Test
    fun integration_hashingConsistency() {
        val input = "test input"

        val sha256Result1 = SHA256.sha256(input)
        val sha256Result2 = SHA256.sha256(input)
        assertEquals(sha256Result1, sha256Result2)

        val md5Result1 = MD5.md5(input)
        val md5Result2 = MD5.md5(input)
        assertEquals(md5Result1, md5Result2)

        val crc32Result1 = CRC32CheckSum.crc32checksum(input)
        val crc32Result2 = CRC32CheckSum.crc32checksum(input)
        assertEquals(crc32Result1, crc32Result2)

        val crc16Result1 = CRC16CheckSum.crc16checksum(input)
        val crc16Result2 = CRC16CheckSum.crc16checksum(input)
        assertEquals(crc16Result1, crc16Result2)
    }

    @Test
    fun performance_largeStringHandling() {
        val largeString = "A".repeat(10000)

        assertNotNull(UTF8Encryptor.stringToUTF8(largeString))
        assertNotNull(StringToHex.convertStringToHex(largeString))
        assertNotNull(StringToBinary.convertStringToBinary(largeString))
        assertNotNull(SHA256.sha256(largeString))
        assertNotNull(MD5.md5(largeString))
        assertNotNull(Base64Encoding.encode(largeString))
        assertNotNull(ASCIIEncryptor.stringToACII(largeString))
    }
    // ===========================================
    // Тесты граничных случаев
    // ===========================================

    @Test
    fun edgeCase_unicodeCharacters() {
        val unicodeString = "Hello 世界 🌍 مرحبا"

        val utf8Encoded = UTF8Encryptor.stringToUTF8(unicodeString)
        val utf8Decoded = UTF8Encryptor.decodeUTF8ToString(utf8Encoded)
        assertEquals(unicodeString, utf8Decoded)


        val base64Encoded = Base64Encoding.encode(unicodeString)
        val base64Decoded = Base64Encoding.decode(base64Encoded)
        assertEquals(unicodeString, base64Decoded)
    }

    @Test
    fun edgeCase_specialCharacters() {
        val specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?"

        val utf8Encoded = UTF8Encryptor.stringToUTF8(specialChars)
        val utf8Decoded = UTF8Encryptor.decodeUTF8ToString(utf8Encoded)
        assertEquals(specialChars, utf8Decoded)

        val hexEncoded = StringToHex.convertStringToHex(specialChars)
        val hexDecoded = StringToHex.convertHexToString(hexEncoded)
        assertEquals(specialChars, hexDecoded)
    }
}