package com.example.text


import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import nadirian.hamlet.android.encdecapp.model.utf_8_code.UTF8Encryptor

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.text", appContext.packageName)
    }
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

}