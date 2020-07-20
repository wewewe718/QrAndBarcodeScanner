package com.example.barcodescanner.feature.barcode

import android.Manifest
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.print.PrintHelper
import com.example.barcodescanner.R
import com.example.barcodescanner.di.*
import com.example.barcodescanner.extension.*
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.feature.barcode.image.BarcodeImageActivity
import com.example.barcodescanner.feature.barcode.receipt.CheckReceiptActivity
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.ParsedBarcode
import com.example.barcodescanner.model.schema.BarcodeSchema
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_barcode.*
import java.text.SimpleDateFormat
import java.util.*


class BarcodeActivity : BaseActivity() {

    companion object {
        private const val REQUEST_PERMISSIONS_CODE = 101
        private const val BARCODE_KEY = "BARCODE_KEY"
        private const val IS_CREATED = "IS_CREATED"

        fun start(context: Context, barcode: Barcode, isCreated: Boolean = false) {
            val intent = Intent(context, BarcodeActivity::class.java).apply {
                putExtra(BARCODE_KEY, barcode)
                putExtra(IS_CREATED, isCreated)
            }
            context.startActivity(intent)
        }
    }

    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val disposable = CompositeDisposable()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

    private val originalBarcode by lazy {
        intent?.getSerializableExtra(BARCODE_KEY) as? Barcode ?: throw IllegalArgumentException("No barcode passed")
    }

    private val isCreated by lazy {
        intent?.getBooleanExtra(IS_CREATED, false).orFalse()
    }

    private val barcode by lazy {
        ParsedBarcode(originalBarcode)
    }

    private val clipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)
        applySettings()
        initScrollView()
        handleToolbarBackPressed()
        handleToolbarMenuClicked()
        handleButtonsClicked()
        showBarcode()
        showOrHideButtons()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_CODE && permissionsHelper.areAllPermissionsGranted(grantResults)) {
            saveBarcodeImage()
        }
    }


    private fun applySettings() {
        if (settings.copyToClipboard) {
            copyToClipboard(barcode.text)
        }

        if (settings.openLinksAutomatically.not()) {
            return
        }

        when (barcode.schema) {
            BarcodeSchema.APP -> openInGooglePlay()
            BarcodeSchema.YOUTUBE -> openInYoutube()
            BarcodeSchema.GOOGLE_MAPS -> showLocation()
            BarcodeSchema.URL -> openLink()
            else -> return
        }
    }

    private fun initScrollView() {
        scroll_view.makeSmoothScrollable()
    }

    private fun handleToolbarBackPressed() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleToolbarMenuClicked() {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_show_barcode_image -> navigateToBarcodeImageActivity()
                R.id.item_delete -> deleteBarcode()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun handleButtonsClicked() {
        button_search_on_rate_and_goods.setOnClickListener { searchBarcodeTextOnRateAndGoods() }
        button_search_on_amazon.setOnClickListener { searchBarcodeTextOnAmazon() }
        button_search_on_ebay.setOnClickListener { searchBarcodeTextOnEbay() }
        button_search_on_web.setOnClickListener { searchBarcodeTextOnInternet() }

        button_add_to_calendar.setOnClickListener { addToCalendar() }
        button_add_to_contacts.setOnClickListener { addToContacts() }
        button_call_phone.setOnClickListener { callPhone() }
        button_send_sms_or_mms.setOnClickListener { sendSmsOrMms() }
        button_send_email.setOnClickListener { sendEmail() }
        button_show_location.setOnClickListener { showLocation() }
        button_connect_to_wifi.setOnClickListener { connectToWifi() }
        button_open_wifi_settings.setOnClickListener { openWifiSettings() }
        button_copy_network_name.setOnClickListener { copyNetworkNameToClipboard() }
        button_copy_network_password.setOnClickListener { copyNetworkPasswordToClipboard() }
        button_open_in_google_play.setOnClickListener { openInGooglePlay() }
        button_open_in_youtube.setOnClickListener { openInYoutube() }
        button_save_bookmark.setOnClickListener { saveBookmark() }
        button_open_link.setOnClickListener { openLink() }
        button_check_receipt.setOnClickListener { checkReceipt() }

        button_share_as_text.setOnClickListener { shareBarcodeAsText() }
        button_copy.setOnClickListener { copyBarcodeTextToClipboard() }
        button_search.setOnClickListener { searchBarcodeTextOnInternet() }
        button_share_as_image.setOnClickListener { shareBarcodeAsImage() }
        button_save_to_gallery.setOnClickListener { permissionsHelper.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE) }
        button_print.setOnClickListener { printBarcode() }
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

            val fullName = "${barcode.firstName.orEmpty()} ${barcode.lastName.orEmpty()}"
            putExtra(ContactsContract.Intents.Insert.NAME, fullName)
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
        showConnectToWifiButtonEnabled(false)

        wifiConnector
            .connect(this, barcode.networkAuthType.orEmpty(), barcode.networkName.orEmpty(), barcode.networkPassword.orEmpty())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    showConnectToWifiButtonEnabled(true)
                    showToast(R.string.activity_barcode_connecting_to_wifi)
                },
                { error ->
                    showConnectToWifiButtonEnabled(true)
                    showError(error)
                }
            )
            .addTo(disposable)
    }

    private fun openWifiSettings() {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        startActivityIfExists(intent)
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
            barcode.receiptTime.orEmpty(),
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

    private fun searchBarcodeTextOnRateAndGoods() {
        val url = "https://ratengoods.com/product/${barcode.text}/"
        startActivityIfExists(Intent.ACTION_VIEW, url)
    }

    private fun searchBarcodeTextOnAmazon() {
        val url = "https://www.amazon.com/s?k=${barcode.text}"
        startActivityIfExists(Intent.ACTION_VIEW, url)
    }

    private fun searchBarcodeTextOnEbay() {
        val url = "https://www.ebay.com/sch/i.html/?_nkw=${barcode.text}"
        startActivityIfExists(Intent.ACTION_VIEW, url)
    }

    private fun searchBarcodeTextOnInternet() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, barcode.text)
        }
        startActivityIfExists(intent)
    }

    private fun navigateToBarcodeImageActivity() {
        BarcodeImageActivity.start(this, originalBarcode)
    }

    private fun shareBarcodeAsImage() {
        val imageUri = try {
            val image = barcodeImageGenerator.generateImage(barcode, 200, 200, 1)
            barcodeImageSaver.saveImageToCache(this, image, barcode)
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
        try {
            val image = barcodeImageGenerator.generateImage(barcode, 300, 300, 2)
            barcodeImageSaver.saveImageToPublicDirectory(this, image, barcode)
        } catch (ex: Exception) {
            showError(ex)
            return
        }
        showToast(R.string.activity_barcode_barcode_image_saved)
    }

    private fun printBarcode() {
        val barcodeImage = try {
            barcodeImageGenerator.generateImage(barcode, 1000, 1000, 3)
        } catch (ex: Exception) {
            showError(ex)
            return
        }

        PrintHelper(this).apply {
            scaleMode = PrintHelper.SCALE_MODE_FIT
            printBitmap("${barcode.format}_${barcode.schema}_${barcode.date}", barcodeImage)
        }
    }

    private fun deleteBarcode() {
        showLoading(true)

        barcodeDatabase.delete(barcode.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { finish() },
                { error ->
                    showLoading(false)
                    showError(error)
                }
            )
            .addTo(disposable)
    }


    private fun showBarcode() {
        showBarcodeMenuIfNeeded()
        showBarcodeImageIfNeeded()
        showBarcodeDate()
        showBarcodeFormat()
        showBarcodeText()
        showBarcodeCountry()
    }

    private fun showBarcodeMenuIfNeeded() {
        if (isCreated) {
            return
        }

        if (barcode.isInDb) {
            toolbar.inflateMenu(R.menu.menu_barcode)
        } else {
            toolbar.inflateMenu(R.menu.menu_barcode_without_delete)
        }
    }

    private fun showBarcodeImageIfNeeded() {
        if (isCreated) {
            showBarcodeImage()
        }
    }

    private fun showBarcodeImage() {
        try {
            val bitmap = barcodeImageGenerator.generateImage(barcode, 2000, 2000, 0)
            image_view_barcode.isVisible = true
            image_view_barcode.setImageBitmap(bitmap)
        } catch (ex: Exception) {
            image_view_barcode.isVisible = false
            ex.printStackTrace()
        }
    }

    private fun showBarcodeDate() {
        text_view_date.text = dateFormatter.format(barcode.date)
    }

    private fun showBarcodeFormat() {
        val format = barcode.format.toStringId()
        toolbar.setTitle(format)
    }

    private fun showBarcodeText() {
        text_view_barcode_text.text = if (isCreated) {
            barcode.text
        } else {
            barcode.formattedText
        }
    }

    private fun showBarcodeCountry() {
        val country = barcode.country ?: return
        when (country.contains('/')) {
            false -> showOneBarcodeCountry(country)
            true -> showTwoBarcodeCountries(country.split('/'))
        }
    }

    private fun showOneBarcodeCountry(country: String) {
        val fullCountryName = buildFullCountryName(country)
        showFullCountryName(fullCountryName)
    }

    private fun showTwoBarcodeCountries(countries: List<String>) {
        val firstFullCountryName = buildFullCountryName(countries[0])
        val secondFullCountryName = buildFullCountryName(countries[1])
        val fullCountryName = "$firstFullCountryName / $secondFullCountryName"
        showFullCountryName(fullCountryName)
    }

    private fun buildFullCountryName(country: String): String {
        val currentLocale = currentLocale ?: return ""
        val countryName = Locale("", country).getDisplayName(currentLocale)
        val countryEmoji = country.toCountryEmoji()
        return "$countryEmoji $countryName"
    }

    private fun showFullCountryName(fullCountryName: String) {
        text_view_country.apply {
            text = fullCountryName
            isVisible = fullCountryName.isBlank().not()
        }
    }

    private fun showOrHideButtons() {
        button_search.isVisible = isCreated.not()

        if (isCreated) {
            return
        }

        button_search_on_rate_and_goods.isVisible = barcode.isProductBarcode
        button_search_on_amazon.isVisible = barcode.isProductBarcode
        button_search_on_ebay.isVisible = barcode.isProductBarcode
        button_search_on_web.isVisible = barcode.isProductBarcode
        button_search.isVisible = barcode.isProductBarcode.not()

        button_add_to_calendar.isVisible = barcode.schema == BarcodeSchema.VEVENT
        button_add_to_contacts.isVisible = barcode.email.isNullOrEmpty().not() || barcode.phone.isNullOrEmpty().not()
        button_call_phone.isVisible = barcode.phone.isNullOrEmpty().not() && isCreated.not()
        button_send_sms_or_mms.isVisible = barcode.phone.isNullOrEmpty().not() || barcode.smsBody.isNullOrEmpty().not()
        button_send_email.isVisible = barcode.email.isNullOrEmpty().not() || barcode.emailSubject.isNullOrEmpty().not() || barcode.emailBody.isNullOrEmpty().not()
        button_show_location.isVisible = barcode.geoUri.isNullOrEmpty().not()
        button_connect_to_wifi.isVisible = barcode.schema == BarcodeSchema.WIFI
        button_open_wifi_settings.isVisible = barcode.schema == BarcodeSchema.WIFI
        button_copy_network_name.isVisible = barcode.networkName.isNullOrEmpty().not()
        button_copy_network_password.isVisible = barcode.networkPassword.isNullOrEmpty().not()
        button_open_in_google_play.isVisible = barcode.googlePlayUrl.isNullOrEmpty().not()
        button_open_in_youtube.isVisible = barcode.youtubeUrl.isNullOrEmpty().not()
        button_open_link.isVisible = barcode.url.isNullOrEmpty().not()
        button_save_bookmark.isVisible = barcode.schema == BarcodeSchema.BOOKMARK
        button_check_receipt.isVisible = barcode.schema == BarcodeSchema.RECEIPT
    }

    private fun showConnectToWifiButtonEnabled(isEnabled: Boolean) {
        button_connect_to_wifi.isEnabled = isEnabled
    }

    private fun showLoading(isLoading: Boolean) {
        progress_bar_loading.isVisible = isLoading
        group_main_content.isVisible = isLoading.not()
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
