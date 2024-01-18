object XTEA {
    private const val ROUNDS = 32
    private const val DELTA = -0x61c88647
    fun encryptXTEA(data: ByteArray, key: IntArray): ByteArray {
        //-Start: added this section
        var data = data
        val pad = data.size % 8
        if (pad > 0) {
            var msgSize = data.size
            msgSize += 8 - pad
            val length = msgSize

            // add the extra null padding to make a fixed-size block
            val arrTemp = ByteArray(length)
            System.arraycopy(data, 0, arrTemp, 0, data.size)
            data = arrTemp
        }
        //-End: added this section
        val numBlocks = data.size / 8
        for (block in 0 until numBlocks) {
            val start = block * 8
            var v0 =
                data[start].toInt() and 0xFF or (data[start + 1].toInt() and 0xFF shl 8) or (data[start + 2].toInt() and 0xFF shl 16) or (data[start + 3].toInt() and 0xFF shl 24)
            var v1 =
                data[start + 4].toInt() and 0xFF or (data[start + 5].toInt() and 0xFF shl 8) or (data[start + 6].toInt() and 0xFF shl 16) or (data[start + 7].toInt() and 0xFF shl 24)
            var sum = 0
            for (round in 0 until ROUNDS) {
                v0 += (v1 shl 4 xor (v1 ushr 5)) + v1 xor sum + key[sum and 3]
                sum += DELTA
                v1 += (v0 shl 4 xor (v0 ushr 5)) + v0 xor sum + key[sum ushr 11 and 3]
            }
            data[start] = v0.toByte()
            data[start + 1] = (v0 ushr 8).toByte()
            data[start + 2] = (v0 ushr 16).toByte()
            data[start + 3] = (v0 ushr 24).toByte()
            data[start + 4] = v1.toByte()
            data[start + 5] = (v1 ushr 8).toByte()
            data[start + 6] = (v1 ushr 16).toByte()
            data[start + 7] = (v1 ushr 24).toByte()
        }
        return data
    }

    fun decryptXTEA(data: ByteArray, key: IntArray): ByteArray {
        val numBlocks = data.size / 8
        for (block in 0 until numBlocks) {
            val start = block * 8
            var v0 =
                data[start].toInt() and 0xFF or (data[start + 1].toInt() and 0xFF shl 8) or (data[start + 2].toInt() and 0xFF shl 16) or (data[start + 3].toInt() and 0xFF shl 24)
            var v1 =
                data[start + 4].toInt() and 0xFF or (data[start + 5].toInt() and 0xFF shl 8) or (data[start + 6].toInt() and 0xFF shl 16) or (data[start + 7].toInt() and 0xFF shl 24)
            var sum = DELTA * ROUNDS
            for (round in 0 until ROUNDS) {
                v1 -= (v0 shl 4 xor (v0 ushr 5)) + v0 xor sum + key[sum ushr 11 and 3]
                sum -= DELTA
                v0 -= (v1 shl 4 xor (v1 ushr 5)) + v1 xor sum + key[sum and 3]
            }
            data[start] = v0.toByte()
            data[start + 1] = (v0 ushr 8).toByte()
            data[start + 2] = (v0 ushr 16).toByte()
            data[start + 3] = (v0 ushr 24).toByte()
            data[start + 4] = v1.toByte()
            data[start + 5] = (v1 ushr 8).toByte()
            data[start + 6] = (v1 ushr 16).toByte()
            data[start + 7] = (v1 ushr 24).toByte()
        }
        return data
    }

    fun getFormattedKey(key: String): IntArray {
        var key = key
        require(key.isNotEmpty()) { "Key must be between 1 and 16 characters in length" }
        key = String.format("%-16s", key).substring(0, 16)
        val formattedKey = IntArray(4)
        var j = 0
        var i = 0
        while (i < key.length) {
            formattedKey[j++] = convertStringToUInt(key.substring(i, i + 4))
            i += 4
        }
        return formattedKey
    }

    private fun convertStringToUInt(input: String): Int {
        var output: Int = input[0].code
        output += input[1].code shl 8
        output += input[2].code shl 16
        output += input[3].code shl 24
        return output
    }
}
