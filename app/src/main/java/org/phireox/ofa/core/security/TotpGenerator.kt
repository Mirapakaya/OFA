package org.phireox.ofa.core.security

import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object TotpGenerator {

    fun generate(secret: String, timeStep: Long = 30): String {
        val key = base32Decode(secret.uppercase().replace(" ", ""))
        if (key.isEmpty()) return "000000"
        val time = System.currentTimeMillis() / 1000 / timeStep
        val data = ByteBuffer.allocate(8).putLong(time).array()
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(key, "HmacSHA1"))
        val hash = mac.doFinal(data)
        val offset = hash.last().toInt() and 0x0F
        val otp = ((hash[offset].toInt() and 0x7F) shl 24 or
                (hash[offset + 1].toInt() and 0xFF) shl 16 or
                (hash[offset + 2].toInt() and 0xFF) shl 8 or
                (hash[offset + 3].toInt() and 0xFF))
        return String.format("%06d", otp % 1_000_000)
    }

    fun remainingSeconds(timeStep: Long = 30): Int {
        val time = System.currentTimeMillis() / 1000
        return (timeStep - (time % timeStep)).toInt()
    }

    private fun base32Decode(input: String): ByteArray {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val output = mutableListOf<Byte>()
        var buffer = 0
        var bitsLeft = 0
        for (char in input) {
            val value = alphabet.indexOf(char)
            if (value < 0) continue
            buffer = (buffer shl 5) or value
            bitsLeft += 5
            if (bitsLeft >= 8) {
                bitsLeft -= 8
                output.add((buffer shr bitsLeft).toByte())
            }
        }
        return output.toByteArray()
    }
}
