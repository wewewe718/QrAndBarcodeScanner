package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.equalsAnyIgnoreCase
import com.example.barcodescanner.extension.joinNotNullOrBlankToStringWithLineSeparator
import com.example.barcodescanner.extension.removePrefixIgnoreCase
import com.example.barcodescanner.extension.startsWithIgnoreCase

class Cryptocurrency(
    val cryptocurrency: String? = null,
    val address: String? = null,
    val label: String? = null,
    val amount: String? = null,
    val message: String? = null
) : Schema {

    companion object {
        private const val BITCOIN_PREFIX = "bitcoin"
        private const val BITCOIN_CASH_PREFIX = "bitcoincash"
        private const val ETHEREUM_PREFIX = "ethereum"
        private const val LITECOIN_PREFIX = "litecoin"
        private const val DASH_PREFIX = "dash"
        private const val LABEL_PREFIX = "label="
        private const val AMOUNT_PREFIX = "amount="
        private const val MESSAGE_PREFIX = "message="
        private const val PREFIX_END_SYMBOL = ":"
        private const val ADDRESS_SEPARATOR = "?"
        private const val PARAMETERS_SEPARATOR = "&"
        private val PREFIXES = listOf(BITCOIN_PREFIX, BITCOIN_CASH_PREFIX, ETHEREUM_PREFIX, LITECOIN_PREFIX, DASH_PREFIX)

        fun parse(text: String): Cryptocurrency? {
            val prefixAndSuffix = text.split(PREFIX_END_SYMBOL)
            val cryptocurrency = prefixAndSuffix.getOrNull(0).orEmpty()
            if (cryptocurrency.equalsAnyIgnoreCase(PREFIXES).not()) {
                return null
            }

            val addressAndParameters = prefixAndSuffix.getOrNull(1).orEmpty().split(ADDRESS_SEPARATOR)
            val address = addressAndParameters.getOrNull(0)

            var label: String? = null
            var amount: String? = null
            var message: String? = null

            val parameters = addressAndParameters.getOrNull(1).orEmpty().split(PARAMETERS_SEPARATOR)
            parameters.forEach { parameter ->
                if (parameter.startsWithIgnoreCase(LABEL_PREFIX)) {
                    label = parameter.removePrefixIgnoreCase(LABEL_PREFIX)
                    return@forEach
                }

                if (parameter.startsWithIgnoreCase(AMOUNT_PREFIX)) {
                    amount = parameter.removePrefixIgnoreCase(AMOUNT_PREFIX)
                    return@forEach
                }

                if (parameter.startsWithIgnoreCase(MESSAGE_PREFIX)) {
                    message = parameter.removePrefixIgnoreCase(MESSAGE_PREFIX)
                    return@forEach
                }
            }

            return Cryptocurrency(cryptocurrency, address, label, amount, message)
        }
    }

    override val schema = BarcodeSchema.CRYPTOCURRENCY

    override fun toFormattedText(): String {
        return listOf(cryptocurrency, address, label, amount, message).joinNotNullOrBlankToStringWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return "${cryptocurrency.orEmpty()}$PREFIX_END_SYMBOL" +
                "${address.orEmpty()}$ADDRESS_SEPARATOR" +
                "${label.orEmpty()}$PARAMETERS_SEPARATOR" +
                "${amount.orEmpty()}$PARAMETERS_SEPARATOR" +
                message.orEmpty()
    }
}