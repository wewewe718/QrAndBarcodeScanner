package com.example.barcodescanner.feature.barcode

import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.print.PrintHelper
import com.example.barcodescanner.R
import com.example.barcodescanner.di.barcodeImageGenerator
import com.example.barcodescanner.di.barcodeImageSaver
import com.example.barcodescanner.di.wifiConnector
import com.example.barcodescanner.feature.barcode.image.BarcodeImageActivity
import com.example.barcodescanner.feature.barcode.receipt.CheckReceiptActivity
import com.example.barcodescanner.feature.common.orZero
import com.example.barcodescanner.feature.common.showError
import com.example.barcodescanner.feature.common.toStringId
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.BarcodeSchema
import com.example.barcodescanner.model.ParsedBarcode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_barcode.*
import java.text.SimpleDateFormat
import java.util.*


class BarcodeActivity : AppCompatActivity() {

    companion object {
        private const val BARCODE_KEY = "BARCODE_KEY"

        fun start(context: Context, barcode: Barcode) {
            val intent = Intent(context, BarcodeActivity::class.java)
            intent.putExtra(BARCODE_KEY, barcode)
            context.startActivity(intent)
        }
    }


    private val disposable = CompositeDisposable()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

    private val originalBarcode by lazy {
        intent?.getParcelableExtra(BARCODE_KEY) as? Barcode ?: throw IllegalArgumentException("No barcode passed")
    }

    private val barcode by lazy {
        ParsedBarcode(originalBarcode)
    }

    private val clipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(BarcodeViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)
        handleToolbarBackPressed()
        handleToolbarMenuClicked()
        handleButtonsClicked()
        showBarcode()
        showOrHideButtons()
    }

    override fun onResume() {
        super.onResume()
        subscribeToViewModel()
    }

    override fun onPause() {
        super.onPause()
        unsubscribeFromViewModel()
    }


    private fun handleToolbarBackPressed() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleToolbarMenuClicked() {
        toolbar.inflateMenu(R.menu.menu_barcode)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_show_barcode_image -> showBarcodeImage()
                R.id.item_delete -> viewModel.onDeleteClicked(barcode)
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun handleButtonsClicked() {
        button_add_to_calendar.setOnClickListener { addToCalendar() }
        button_add_to_contacts.setOnClickListener { addToContacts() }
        button_call_phone.setOnClickListener { callPhone() }
        button_send_sms_or_mms.setOnClickListener { sendSmsOrMms() }
        button_send_email.setOnClickListener { sendEmail() }
        button_show_location.setOnClickListener { showLocation() }
        button_connect_to_wifi.setOnClickListener { connectToWifi() }
        button_copy_network_name.setOnClickListener { copyNetworkNameToClipboard() }
        button_copy_network_password.setOnClickListener { copyNetworkPasswordToClipboard() }
        button_open_in_google_play.setOnClickListener { openInGooglePlay() }
        button_open_in_youtube.setOnClickListener { openInYoutube() }
        button_save_bookmark.setOnClickListener { saveBookmark() }
        button_open_link.setOnClickListener { openLink() }
        button_check_receipt.setOnClickListener { checkReceipt() }

        button_show_barcode_image.setOnClickListener { showBarcodeImage() }
        button_share_as_text.setOnClickListener { shareBarcodeAsText() }
        button_copy.setOnClickListener { copyBarcodeTextToClipboard() }
        button_search.setOnClickListener { searchBarcodeTextOnInternet() }
        button_share_as_image.setOnClickListener { shareBarcodeAsImage() }
        button_save_to_gallery.setOnClickListener { saveBarcodeImage() }
        button_print.setOnClickListener { printBarcode() }
        button_delete.setOnClickListener { viewModel.onDeleteClicked(barcode) }
    }


    private fun subscribeToViewModel() {
        subscribeToLoading()
        subscribeToError()
        subscribeToBarcodeDeleted()
    }

    private fun subscribeToLoading() {
        viewModel.isLoading
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::showLoading)
            .addTo(disposable)
    }

    private fun subscribeToError() {
        viewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::showError)
            .addTo(disposable)
    }

    private fun subscribeToBarcodeDeleted() {
        viewModel.barcodeDeleted
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { finish() }
            .addTo(disposable)
    }

    private fun unsubscribeFromViewModel() {
        disposable.clear()
    }


    private fun showLoading(isLoading: Boolean) {
        progress_bar_loading.isVisible = isLoading
        group_main_content.isVisible = isLoading.not()
    }

    private fun showBarcode() {
        showBarcodeDate()
        showBarcodeFormat()
        showBarcodeText()
    }

    private fun showBarcodeDate() {
        text_view_date.text = dateFormatter.format(barcode.date)
    }

    private fun showBarcodeFormat() {
        val format = barcode.format.toStringId()
        toolbar.setTitle(format)
    }

    private fun showBarcodeText() {
        when (barcode.schema) {
            BarcodeSchema.BOOKMARK -> showBookmark()
            BarcodeSchema.CALENDAR -> showCalendar()
            BarcodeSchema.EMAIL -> showEmail()
            BarcodeSchema.PHONE -> showPhone()
            BarcodeSchema.SMS, BarcodeSchema.MMS -> showSmsOrMms()
            BarcodeSchema.VCARD -> showVCard()
            BarcodeSchema.MECARD -> showMeCard()
            BarcodeSchema.WIFI -> showWifi()
            BarcodeSchema.RECEIPT -> showReceipt()
            else -> text_view_barcode_text.text = barcode.text
        }
    }

    private fun showBookmark() {
        text_view_barcode_text.text = getString(
            R.string.activity_barcode_bookmark_format,
            barcode.bookmarkTitle.orEmpty(),
            barcode.url.orEmpty()
        )
    }

    private fun showCalendar() {
        text_view_barcode_text.text = getString(
            R.string.activity_barcode_calendar_format,
            barcode.eventUid.orEmpty(),
            barcode.eventOrganizer.orEmpty(),
            barcode.eventSummary.orEmpty(),
            dateFormatter.format(Date(barcode.eventStartDate.orZero())),
            dateFormatter.format(Date(barcode.eventEndDate.orZero()))
        )
    }

    private fun showEmail() {
        text_view_barcode_text.text = getString(
            R.string.activity_barcode_email_format,
            barcode.email.orEmpty(),
            barcode.emailSubject.orEmpty(),
            barcode.emailBody.orEmpty()
        )
    }

    private fun showPhone() {
        text_view_barcode_text.text = barcode.phone
    }

    private fun showSmsOrMms() {
        text_view_barcode_text.text = getString(
            R.string.activity_barcode_sms_format,
            barcode.phone,
            barcode.smsBody
        )
    }

    private fun showVCard() {
        text_view_barcode_text.text = StringBuilder()
            .appendIfNotEmpty(barcode.name, R.string.activity_barcode_contact_name)
            .appendIfNotEmpty(barcode.organization, R.string.activity_barcode_contact_organization)
            .appendIfNotEmpty(barcode.jobTitle, R.string.activity_barcode_contact_job_position)
            .appendIfNotEmpty(barcode.phone, R.string.activity_barcode_contact_phone)
            .appendIfNotEmpty(barcode.secondaryPhone, R.string.activity_barcode_contact_secondary_phone)
            .appendIfNotEmpty(barcode.tertiaryPhone, R.string.activity_barcode_contact_tertiary_phone)
            .appendIfNotEmpty(barcode.email, R.string.activity_barcode_contact_email)
            .appendIfNotEmpty(barcode.secondaryEmail, R.string.activity_barcode_contact_secondary_email)
            .appendIfNotEmpty(barcode.tertiaryEmail, R.string.activity_barcode_contact_tertiary_email)
            .appendIfNotEmpty(barcode.url, R.string.activity_barcode_contact_url)
            .appendIfNotEmpty(barcode.geoUri, R.string.activity_barcode_contact_geo)
            .apply {
                if (isNotEmpty() && last().isLineSeparator()) {
                    setLength(length - 1)
                }
            }
            .toString()
    }

    private fun showMeCard() {
        text_view_barcode_text.text = StringBuilder()
            .appendIfNotEmpty(barcode.name, R.string.activity_barcode_contact_name)
            .appendIfNotEmpty(barcode.phone, R.string.activity_barcode_contact_phone)
            .appendIfNotEmpty(barcode.address, R.string.activity_barcode_contact_address)
            .appendIfNotEmpty(barcode.email, R.string.activity_barcode_contact_email)
            .apply {
                if (isNotEmpty() && last().isLineSeparator()) {
                    setLength(length - 1)
                }
            }
            .toString()
    }

    private fun StringBuilder.appendIfNotEmpty(text: String?, stringId: Int): StringBuilder {
        if (text.isNullOrEmpty().not()) {
            append(getString(stringId, text))
        }
        return this
    }

    private fun Char.isLineSeparator(): Boolean {
        return this == '\r' || this == '\n'
    }

    private fun showWifi() {
        text_view_barcode_text.text = getString(
            R.string.activity_barcode_wifi_format,
            barcode.networkAuthType,
            barcode.networkName,
            barcode.networkPassword
        )
    }

    private fun showReceipt() {
        val receiptType = when (barcode.receiptType) {
            1 -> R.string.activity_barcode_receipt_type_income
            2 -> R.string.activity_barcode_receipt_type_return_income
            3 -> R.string.activity_barcode_receipt_type_expense
            4 -> R.string.activity_barcode_receipt_type_return_expense
            else -> R.string.activity_barcode_receipt_type_income
        }

        text_view_barcode_text.text = getString(
            R.string.activity_barcode_receipt_format,
            dateFormatter.format(Date(barcode.receiptTime.orZero())),
            getString(receiptType),
            barcode.receiptFiscalDriveNumber,
            barcode.receiptFiscalDocumentNumber,
            barcode.receiptFiscalSign,
            barcode.receiptSum
        )
    }

    private fun showOrHideButtons() {
        button_add_to_calendar.isVisible = barcode.schema == BarcodeSchema.CALENDAR
        button_add_to_contacts.isVisible = barcode.email.isNullOrEmpty().not() || barcode.phone.isNullOrEmpty().not()
        button_call_phone.isVisible = barcode.phone.isNullOrEmpty().not()
        button_send_sms_or_mms.isVisible = barcode.phone.isNullOrEmpty().not() || barcode.smsBody.isNullOrEmpty().not()
        button_send_email.isVisible = barcode.email.isNullOrEmpty().not() || barcode.emailSubject.isNullOrEmpty().not() || barcode.emailBody.isNullOrEmpty().not()
        button_show_location.isVisible = barcode.geoUri.isNullOrEmpty().not()
        button_connect_to_wifi.isVisible = barcode.schema == BarcodeSchema.WIFI
        button_copy_network_name.isVisible = barcode.networkName.isNullOrEmpty().not()
        button_copy_network_password.isVisible = barcode.networkPassword.isNullOrEmpty().not()
        button_open_in_google_play.isVisible = barcode.googlePlayUrl.isNullOrEmpty().not()
        button_open_in_youtube.isVisible = barcode.youtubeUrl.isNullOrEmpty().not()
        button_open_link.isVisible = barcode.url.isNullOrEmpty().not()
        button_save_bookmark.isVisible = barcode.schema == BarcodeSchema.BOOKMARK
        button_check_receipt.isVisible = barcode.schema == BarcodeSchema.RECEIPT
    }


    private fun addToCalendar() {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, barcode.eventUid)
            putExtra(CalendarContract.Events.DESCRIPTION, barcode.eventSummary)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, barcode.eventStartDate)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, barcode.eventEndDate)
        }
        startActivityIfExists(intent)
    }

    private fun addToContacts() {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE

            putExtra(ContactsContract.Intents.Insert.NAME, barcode.name.orEmpty())
            putExtra(ContactsContract.Intents.Insert.COMPANY, barcode.organization.orEmpty())
            putExtra(ContactsContract.Intents.Insert.JOB_TITLE, barcode.jobTitle.orEmpty())

            putExtra(ContactsContract.Intents.Insert.PHONE, barcode.phone.orEmpty())
            putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, barcode.phoneType.orEmpty())
            putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, barcode.secondaryPhone.orEmpty())
            putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, barcode.secondaryPhoneType.orEmpty())
            putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, barcode.tertiaryPhone.orEmpty())
            putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE, barcode.tertiaryPhoneType.orEmpty())

            putExtra(ContactsContract.Intents.Insert.EMAIL, barcode.email.orEmpty())
            putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, barcode.emailType.orEmpty())
            putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL, barcode.secondaryEmail.orEmpty())
            putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE, barcode.secondaryEmailType.orEmpty())
            putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL, barcode.tertiaryEmail.orEmpty())
            putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE, barcode.tertiaryEmailType.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun callPhone() {
        val phoneUri = "tel:${barcode.phone.orEmpty()}"
        startActivityIfExists(Intent.ACTION_DIAL, phoneUri)
    }

    private fun sendSmsOrMms() {
        val smsUri = Uri.parse("sms:${barcode.phone.orEmpty()}")
        val intent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
            putExtra("sms_body", barcode.smsBody.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, barcode.email.orEmpty())
            putExtra(Intent.EXTRA_SUBJECT, barcode.emailSubject.orEmpty())
            putExtra(Intent.EXTRA_TEXT, barcode.emailBody.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun showLocation() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.geoUri.orEmpty())
    }

    private fun connectToWifi() {
        try {
            barcode.apply {
                wifiConnector.connect(networkAuthType.orEmpty(), networkName.orEmpty(), networkPassword.orEmpty())
            }
        } catch (ex: Exception) {
            showError(ex)
        }
    }

    private fun copyNetworkNameToClipboard() {
        copyToClipboard(barcode.networkName.orEmpty())
        showToast(R.string.activity_barcode_copied)
    }

    private fun copyNetworkPasswordToClipboard() {
        copyToClipboard(barcode.networkPassword.orEmpty())
        showToast(R.string.activity_barcode_copied)
    }

    private fun openInGooglePlay() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.googlePlayUrl.orEmpty())
    }

    private fun openInYoutube() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.youtubeUrl.orEmpty())
    }

    private fun openLink() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.url.orEmpty())
    }

    private fun saveBookmark() {
        val intent = Intent(Intent.ACTION_INSERT, Uri.parse("content://browser/bookmarks")).apply {
            putExtra("title", barcode.bookmarkTitle.orEmpty())
            putExtra("url", barcode.url.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun checkReceipt() {
        CheckReceiptActivity.start(
            this,
            barcode.receiptType.orZero(),
            barcode.receiptTimeOriginal.orEmpty(),
            barcode.receiptFiscalDriveNumber.orEmpty(),
            barcode.receiptFiscalDocumentNumber.orEmpty(),
            barcode.receiptFiscalSign.orEmpty(),
            barcode.receiptSum.orEmpty()
        )
    }

    private fun shareBarcodeAsText() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, barcode.text)
        }
        startActivityIfExists(intent)
    }

    private fun copyBarcodeTextToClipboard() {
        copyToClipboard(barcode.text)
        showToast(R.string.activity_barcode_copied)
    }

    private fun searchBarcodeTextOnInternet() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, barcode.text)
        }
        startActivityIfExists(intent)
    }

    private fun showBarcodeImage() {
        BarcodeImageActivity.start(this, originalBarcode)
    }

    private fun shareBarcodeAsImage() {
        val imageUri = try {
            barcodeImageSaver.saveImageToCache(this, barcode)
        } catch (ex: Exception) {
            showError(ex)
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivityIfExists(intent)
    }

    private fun saveBarcodeImage() {
        val imagePath = barcodeImageSaver.saveImageToPublicDirectory(this, barcode)
        if (imagePath != null) {
            showToast(R.string.activity_barcode_barcode_image_saved)
        }
    }

    private fun printBarcode() {
        val barcodeImage = barcodeImageGenerator.generateImage(barcode, 1000, 1000, 3)
        PrintHelper(this).apply {
            scaleMode = PrintHelper.SCALE_MODE_FIT
            printBitmap("${barcode.format}_${barcode.schema}_${barcode.date}", barcodeImage)
        }
    }


    private fun startActivityIfExists(action: String, uri: String) {
        val intent = Intent(action, Uri.parse(uri))
        startActivityIfExists(intent)
    }

    private fun startActivityIfExists(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast(R.string.activity_barcode_no_app)
        }
    }

    private fun copyToClipboard(text: String) {
        val clipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
    }
}
