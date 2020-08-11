package com.example.barcodescanner.usecase

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import com.example.barcodescanner.extension.orZero
import com.example.barcodescanner.model.Contact


object ContactHelper {
    private val PHONE_PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
    private val CONTACT_PROJECTION = arrayOf(ContactsContract.Data.LOOKUP_KEY)

    fun getPhone(context: Context, result: Intent?): String? {
        val uri = result?.data ?: return null
        val contentResolver = context.contentResolver

        val cursor = contentResolver.query(uri, PHONE_PROJECTION, null, null, null)
            ?: return null

        if (cursor.moveToNext().not()) {
            cursor.close()
            return null
        }

        val phone = cursor.getStringOrNull(ContactsContract.CommonDataKinds.Phone.NUMBER)
        cursor.close()
        return phone
    }

    fun getContact(context: Context, result: Intent?): Contact? {
        val uri = result?.data ?: return null
        val contentResolver = context.contentResolver

        val cursor = contentResolver.query(uri, CONTACT_PROJECTION, null, null, null)
            ?: return null

        if (cursor.moveToNext().not()) {
            cursor.close()
            return null
        }

        val lookupKey = cursor.getStringOrNull(ContactsContract.Data.LOOKUP_KEY)
        if (lookupKey == null) {
            cursor.close()
            return null
        }
        
        return Contact().also { contact ->
            buildContactPhoneDetails(contentResolver, lookupKey, contact)
            buildEmailDetails(contentResolver, lookupKey, contact)
            buildAddressDetails(contentResolver, lookupKey, contact)
            
            cursor.close()
        }
    }

    private fun buildContactPhoneDetails(contentResolver: ContentResolver, lookupKey: String, contact: Contact) {
        val contactWhere = ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val contactWhereParams = arrayOf(lookupKey, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        
        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            contactWhere,
            contactWhereParams,
            null
        ) ?: return
        
        if (cursor.count <= 0) {
            cursor.close()
            return
        }
        
        if (cursor.moveToNext().not()) {
            cursor.close()
            return
        }

        if (cursor.getStringOrNull(ContactsContract.Contacts.HAS_PHONE_NUMBER)?.toInt().orZero() <= 0) {
            cursor.close()
            return
        }
        
        contact.firstName = cursor.getStringOrNull(ContactsContract.Contacts.DISPLAY_NAME)
        contact.middleName = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME)
        contact.lastName = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)
        contact.phone = cursor.getStringOrNull(ContactsContract.CommonDataKinds.Phone.NUMBER)
        contact.contactType = cursor.getIntOrNull(ContactsContract.CommonDataKinds.Phone.TYPE)
        
        cursor.close()
    }

    private fun buildEmailDetails(contentResolver: ContentResolver, lookupKey: String, contact: Contact) {
        val emailWhere = ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val emailWhereParams = arrayOf(lookupKey, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)

        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            emailWhere,
            emailWhereParams,
            null
        ) ?: return

        if (cursor.moveToNext().not()) {
            cursor.close()
            return
        }

        contact.email = cursor.getStringOrNull(ContactsContract.CommonDataKinds.Email.DATA)

        cursor.close()
    }

    private fun buildAddressDetails(contentResolver: ContentResolver, lookupKey: String, contact: Contact) {
        val addressWhere = ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val addressWhereParams = arrayOf(lookupKey, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)

        val cursor = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            addressWhere,
            addressWhereParams,
            null
        ) ?: return

        if (cursor.moveToNext().not()) {
            cursor.close()
            return
        }

        contact.poBox = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.POBOX)
        contact.street = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.STREET)
        contact.city = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.CITY)
        contact.state = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.REGION)
        contact.zipcode = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.REGION)
        contact.country = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)
        contact.street = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.STREET)
        contact.neighborhood = cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD)
        contact.formattedAddress =  cursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)

        cursor.close()
    }
    
    private fun Cursor.getStringOrNull(columnName: String): String? {
        return try {
            getString(getColumnIndex(columnName))
        } catch (ex: Exception) {
            Logger.log(ex)
            null
        }
    }

    private fun Cursor.getIntOrNull(columnName: String): Int? {
        return try {
            getInt(getColumnIndex(columnName))
        } catch (ex: Exception) {
            Logger.log(ex)
            null
        }
    }
}