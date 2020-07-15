package com.example.barcodescanner.model.schema

import com.example.barcodescanner.extension.joinNotNullOrBlankToStringWithLineSeparator
import com.example.barcodescanner.extension.startsWithIgnoreCase
import com.example.barcodescanner.extension.valueOrNull
import ezvcard.Ezvcard
import ezvcard.VCardVersion
import ezvcard.parameter.EmailType
import ezvcard.parameter.TelephoneType
import ezvcard.property.*
import ezvcard.property.Email
import ezvcard.property.Url
import ezvcard.util.GeoUri

data class VCard(
    val firstName: String? = null,
    val lastName: String? = null,
    val nickname: String? = null,
    val organization: String? = null,
    val title: String? = null,
    val email: String? = null,
    val emailType: String? = null,
    val secondaryEmail: String? = null,
    val secondaryEmailType: String? = null,
    val tertiaryEmail: String? = null,
    val tertiaryEmailType: String? = null,
    val phone: String? = null,
    val phoneType: String? = null,
    val secondaryPhone: String? = null,
    val secondaryPhoneType: String? = null,
    val tertiaryPhone: String? = null,
    val tertiaryPhoneType: String? = null,
    val geoUri: String? = null,
    val url: String? = null
) : Schema {

    companion object {
        private const val SCHEMA_PREFIX = "BEGIN:VCARD"

        fun parse(text: String): VCard? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            val vCard = Ezvcard.parse(text).first()
            val firstName = vCard.structuredName?.given
            val lastName = vCard.structuredName?.family
            val nickname = vCard.nickname?.values?.firstOrNull()
            val organization = vCard.organizations?.firstOrNull()?.values?.firstOrNull()
            val title = vCard.titles?.firstOrNull()?.value
            val url = vCard.urls?.firstOrNull()?.value
            val geoUri = vCard.addresses?.firstOrNull()?.geo?.toString()
            var email: String? = null
            var emailType: String? = null
            var secondaryEmail: String? = null
            var secondaryEmailType: String? = null
            var tertiaryEmail: String? = null
            var tertiaryEmailType: String? = null
            var phone: String? = null
            var phoneType: String? = null
            var secondaryPhone: String? = null
            var secondaryPhoneType: String? = null
            var tertiaryPhone: String? = null
            var tertiaryPhoneType: String? = null

            vCard.emails?.getOrNull(0)?.apply {
                email = value
                emailType = types.getOrNull(0)?.value
            }
            vCard.emails?.getOrNull(1)?.apply {
                secondaryEmail = value
                secondaryEmailType = types.getOrNull(0)?.value
            }
            vCard.emails?.getOrNull(2)?.apply {
                tertiaryEmail = value
                tertiaryEmailType = types.getOrNull(0)?.value
            }

            vCard.telephoneNumbers?.getOrNull(0)?.apply {
                phone = this.text
                phoneType = types?.firstOrNull()?.value
            }
            vCard.telephoneNumbers?.getOrNull(1)?.apply {
                secondaryPhone = this.text
                secondaryPhoneType = types?.firstOrNull()?.value
            }
            vCard.telephoneNumbers?.getOrNull(2)?.apply {
                tertiaryPhone = this.text
                tertiaryPhoneType = types?.firstOrNull()?.value
            }

            return VCard(
                firstName,
                lastName,
                nickname,
                organization,
                title,
                email,
                emailType,
                secondaryEmail,
                secondaryEmailType,
                tertiaryEmail,
                tertiaryEmailType,
                phone,
                phoneType,
                secondaryPhone,
                secondaryPhoneType,
                tertiaryPhone,
                tertiaryPhoneType,
                geoUri,
                url
            )
        }
    }

    override val schema = BarcodeSchema.VCARD

    override fun toFormattedText(): String {
        return listOf(
            "${firstName.orEmpty()} ${lastName.orEmpty()}",
            nickname,
            organization,
            title,
            "${phone.orEmpty()} ${phoneType.orEmpty()}",
            "${secondaryPhone.orEmpty()} ${secondaryPhoneType.orEmpty()}",
            "${tertiaryPhone.orEmpty()} ${tertiaryPhoneType.orEmpty()}",
            "${email.orEmpty()} ${emailType.orEmpty()}",
            "${secondaryEmail.orEmpty()} ${secondaryEmailType.orEmpty()}",
            "${tertiaryEmail.orEmpty()} ${tertiaryEmailType.orEmpty()}",
            geoUri,
            url
        ).joinNotNullOrBlankToStringWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        val vCard = ezvcard.VCard()

        vCard.structuredName = StructuredName().apply {
            given = firstName
            family = lastName
        }
        vCard.nickname = Nickname().apply {
            values.add(nickname)
        }
        vCard.organization = Organization().apply {
            values.add(organization)
        }
        vCard.addTitle(Title(title))

        vCard.addEmail(Email(email))
        vCard.addEmail(Email(secondaryEmail))
        vCard.addEmail(Email(tertiaryEmail))

        vCard.addTelephoneNumber(Telephone(phone))
        vCard.addTelephoneNumber(Telephone(secondaryPhone))
        vCard.addTelephoneNumber(Telephone(tertiaryPhoneType))

        vCard.addUrl(Url(url))

        return Ezvcard.write(vCard).version(VCardVersion.V4_0).go()
    }
}