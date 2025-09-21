package com.example.text.ciphers.crc32_checksum

import java.util.zip.CRC32
import java.util.zip.Checksum

object CRC32CheckSum {

    fun crc32checksum(str: String): Long {
        val bytes: ByteArray = str.toByteArray()
        val checksum: Checksum = CRC32()
        checksum.update(bytes, 0, bytes.size)
        return checksum.value
    }
}