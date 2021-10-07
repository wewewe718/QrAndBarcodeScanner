package com.example.barcodescanner.model.schema


import com.example.barcodescanner.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.example.barcodescanner.extension.removePrefixIgnoreCase
import com.example.barcodescanner.extension.startsWithIgnoreCase
import org.apache.commons.codec.binary.Base64
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class NZCovidTracer(
    val title: String? = null,
    val addr: String? = null,
    private val decodedBytes: String? = null
) : Schema {
    //{"gln":"9429300660232","opn":"Queens Park ","adr":"150 Gala Street \nInvercargill ","ver":"c19:1","typ":"entry"}
    //{"gln":"7000000754500","ver":"c19:1","typ":"entry","opn":"Treetown Gym","adr":"1 Shakespeare Road\nBastia Hill\nWhanganui"}

    companion object {
        private const val PREFIX = "NZCOVIDTRACER:"

        fun parse(text: String): NZCovidTracer? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }

            var title: String? = null
            var addr: String? = null
            var decodedBytes: String? = null

            try {
                decodedBytes = String(Base64().decode(text.removePrefixIgnoreCase(PREFIX)))
            }
            catch (e: Exception) {
                return null
            }

            try {
                val obj = JSONObject(decodedBytes)
                title = obj.getString("opn")
                addr = obj.getString("adr")
            }
            catch (e: JSONException) {
                return null
            }

            addr = addr.replace("\\n", "\n")
            return NZCovidTracer(title.trim(), addr.trim())
        }
    }

    override val schema = BarcodeSchema.NZCOVIDTRACER
    override fun toFormattedText(): String = listOf(title, addr).joinToStringNotNullOrBlankWithLineSeparator()
    override fun toBarcodeText(): String = "$PREFIX$decodedBytes"
}