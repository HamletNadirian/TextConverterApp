package nadirian.hamlet.android.encdecapp.model.sha256_hash

import java.math.BigInteger
import java.security.MessageDigest

object SHA256 {
    fun sha256(input:String): String {
        val md = MessageDigest.getInstance("SHA256")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}