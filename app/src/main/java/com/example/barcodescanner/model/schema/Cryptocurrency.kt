package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.equalsAnyIgnoreCase
import com.example.barcodescanner.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.example.barcodescanner.extension.removePrefixIgnoreCase
import com.example.barcodescanner.extension.startsWithIgnoreCase

class Cryptocurrency(
    val cryptocurrency: String,
    val address: String? = null,
    val amount: String? = null,
    val label: String? = null,
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
        return listOf(cryptocurrency, address, label, amount, message).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        val result = StringBuilder()
            .append("$cryptocurrency$PREFIX_END_SYMBOL")
            .append(address)

        if (amount.isNullOrBlank() && label.isNullOrBlank() && message.isNullOrBlank()) {
            return result.toString()
        }

        result.append(ADDRESS_SEPARATOR)

        var isAtLeastOneParamSet = false

        if (amount.isNullOrBlank().not()) {
            isAtLeastOneParamSet = true
            result
                .append(AMOUNT_PREFIX)
                .append(amount)
        }

        if (label.isNullOrBlank().not()) {
            if (isAtLeastOneParamSet) {
                result.append(PARAMETERS_SEPARATOR)
            }
            isAtLeastOneParamSet = true
            result
                .append(LABEL_PREFIX)
                .append(label)
        }

        if (message.isNullOrBlank().not()) {
            if (isAtLeastOneParamSet) {
                result.append(PARAMETERS_SEPARATOR)
            }
            result
                .append(MESSAGE_PREFIX)
                .append(message)
        }

        return result.toString()
    }
}