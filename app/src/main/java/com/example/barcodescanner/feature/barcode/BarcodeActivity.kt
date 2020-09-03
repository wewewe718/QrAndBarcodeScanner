package com.example.barcodescanner.feature.barcode

import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.example.barcodescanner.feature.barcode.otp.OtpActivity
import com.example.barcodescanner.feature.barcode.save.SaveBarcodeAsImageActivity
import com.example.barcodescanner.feature.barcode.save.SaveBarcodeAsTextActivity
import com.example.barcodescanner.feature.common.dialog.DeleteConfirmationDialogFragment
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.ParsedBarcode
import com.example.barcodescanner.model.schema.BarcodeSchema
import com.example.barcodescanner.model.schema.OtpAuth
import com.example.barcodescanner.usecase.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_barcode.*
import java.text.SimpleDateFormat
import java.util.*


class BarcodeActivity : BaseActivity(), DeleteConfirmationDialogFragment.Listener {

    companion object {
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

    private val disposable = CompositeDisposable()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

    private val originalBarcode by unsafeLazy {
        intent?.getSerializableExtra(BARCODE_KEY) as? Barcode ?: throw IllegalArgumentException("No barcode passed")
    }

    private val isCreated by unsafeLazy {
        intent?.getBooleanExtra(IS_CREATED, false).orFalse()
    }

    private val barcode by unsafeLazy {
        ParsedBarcode(originalBarcode)
    }

    private val clipboardManager by unsafeLazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)
        supportEdgeToEdge()
        applySettings()
        handleToolbarBackPressed()
        handleToolbarMenuClicked()
        handleButtonsClicked()
        showBarcode()
        showOrHideButtons()
        showButtonText()
    }

    override fun onDeleteConfirmed() {
        deleteBarcode()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }


    private fun supportEdgeToEdge() {
        root_view.applySystemWindowInsets(applyTop = true, applyBottom = true)
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

    private fun handleToolbarBackPressed() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleToolbarMenuClicked() {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_add_to_favorites -> toggleIsFavorite()
                R.id.item_show_barcode_image -> navigateToBarcodeImageActivity()
                R.id.item_delete -> showDeleteBarcodeConfirmationDialog()
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
        button_show_location.setOnClickListener { showLocation() }
        button_connect_to_wifi.setOnClickListener { connectToWifi() }
        button_open_wifi_settings.setOnClickListener { openWifiSettings() }
        button_copy_network_name.setOnClickListener { copyNetworkNameToClipboard() }
        button_copy_network_password.setOnClickListener { copyNetworkPasswordToClipboard() }
        button_open_in_google_play.setOnClickListener { openInGooglePlay() }
        button_open_in_youtube.setOnClickListener { openInYoutube() }
        button_show_otp.setOnClickListener { showOtp() }
        button_open_otp.setOnClickListener { openOtpInOtherApp() }
        button_open_bitcoin_uri.setOnClickListener { openBitcoinUrl() }
        button_open_link.setOnClickListener { openLink() }
        button_save_bookmark.setOnClickListener { saveBookmark() }

        button_call_phone_1.setOnClickListener { callPhone(barcode.phone) }
        button_call_phone_2.setOnClickListener { callPhone(barcode.secondaryPhone) }
        button_call_phone_3.setOnClickListener { callPhone(barcode.tertiaryPhone) }

        button_send_sms_or_mms_1.setOnClickListener { sendSmsOrMms(barcode.phone) }
        button_send_sms_or_mms_2.setOnClickListener { sendSmsOrMms(barcode.secondaryPhone) }
        button_send_sms_or_mms_3.setOnClickListener { sendSmsOrMms(barcode.tertiaryPhone) }

        button_send_email_1.setOnClickListener { sendEmail(barcode.email) }
        button_send_email_2.setOnClickListener { sendEmail(barcode.secondaryEmail) }
        button_send_email_3.setOnClickListener { sendEmail(barcode.tertiaryEmail) }

        button_share_as_text.setOnClickListener { shareBarcodeAsText() }
        button_copy.setOnClickListener { copyBarcodeTextToClipboard() }
        button_search.setOnClickListener { searchBarcodeTextOnInternet() }
        button_save_as_text.setOnClickListener { navigateToSaveBarcodeAsTextActivity() }
        button_share_as_image.setOnClickListener { shareBarcodeAsImage() }
        button_save_as_image.setOnClickListener { navigateToSaveBarcodeAsImageActivity() }
        button_print.setOnClickListener { printBarcode() }
    }


    private fun toggleIsFavorite() {
        val newBarcode = originalBarcode.copy(isFavorite = originalBarcode.isFavorite.not())

        barcodeDatabase.save(newBarcode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    originalBarcode.isFavorite = newBarcode.isFavorite
                    showBarcodeIsFavorite(newBarcode.isFavorite)
                },
                {}
            )
            .addTo(disposable)
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
            putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, barcode.phoneType.orEmpty().toPhoneType())

            putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, barcode.secondaryPhone.orEmpty())
            putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, barcode.secondaryPhoneType.orEmpty().toPhoneType())

            putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, barcode.tertiaryPhone.orEmpty())
            putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE, barcode.tertiaryPhoneType.orEmpty().toPhoneType())

            putExtra(ContactsContract.Intents.Insert.EMAIL, barcode.email.orEmpty())
            putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, barcode.emailType.orEmpty().toEmailType())

            putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL, barcode.secondaryEmail.orEmpty())
            putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE, barcode.secondaryEmailType.orEmpty().toEmailType())

            putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL, barcode.tertiaryEmail.orEmpty())
            putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE, barcode.tertiaryEmailType.orEmpty().toEmailType())
        }
        startActivityIfExists(intent)
    }

    private fun callPhone(phone: String?) {
        val phoneUri = "tel:${phone.orEmpty()}"
        startActivityIfExists(Intent.ACTION_DIAL, phoneUri)
    }

    private fun sendSmsOrMms(phone: String?) {
        val uri = Uri.parse("sms:${phone.orEmpty()}")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", barcode.smsBody.orEmpty())
        }
        startActivityIfExists(intent)
    }

    private fun sendEmail(email: String?) {
        val uri = Uri.parse("mailto:${email.orEmpty()}")
        val intent = Intent(Intent.ACTION_SEND, uri).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.orEmpty()))
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

    private fun showOtp() {
        val otp = OtpAuth.parse(barcode.otpUrl.orEmpty()) ?: return
        OtpActivity.start(this, otp)
    }

    private fun openOtpInOtherApp() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.otpUrl.orEmpty())
    }

    private fun openBitcoinUrl() {
        startActivityIfExists(Intent.ACTION_VIEW, barcode.bitcoinUri.orEmpty())
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

    private fun shareBarcodeAsImage() {
        val imageUri = try {
            val image = barcodeImageGenerator.generateBitmap(originalBarcode, 200, 200, 1)
            barcodeImageSaver.saveImageToCache(this, image, barcode)
        } catch (ex: Exception) {
            Logger.log(ex)
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

    private fun printBarcode() {
        val barcodeImage = try {
            barcodeImageGenerator.generateBitmap(originalBarcode, 1000, 1000, 3)
        } catch (ex: Exception) {
            Logger.log(ex)
            showError(ex)
            return
        }

        PrintHelper(this).apply {
            scaleMode = PrintHelper.SCALE_MODE_FIT
            printBitmap("${barcode.format}_${barcode.schema}_${barcode.date}", barcodeImage)
        }
    }

    private fun navigateToBarcodeImageActivity() {
        BarcodeImageActivity.start(this, originalBarcode)
    }

    private fun navigateToSaveBarcodeAsTextActivity() {
        SaveBarcodeAsTextActivity.start(this, originalBarcode)
    }

    private fun navigateToSaveBarcodeAsImageActivity() {
        SaveBarcodeAsImageActivity.start(this, originalBarcode)
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
        showBarcodeIsFavorite()
        showBarcodeImageIfNeeded()
        showBarcodeDate()
        showBarcodeFormat()
        showBarcodeText()
        showBarcodeCountry()
    }

    private fun showBarcodeMenuIfNeeded() {
        toolbar.inflateMenu(R.menu.menu_barcode)
        toolbar.menu.apply {
            findItem(R.id.item_add_to_favorites)?.isVisible = barcode.isInDb
            findItem(R.id.item_show_barcode_image)?.isVisible = isCreated.not()
            findItem(R.id.item_delete)?.isVisible = barcode.isInDb
        }
    }

    private fun showBarcodeIsFavorite() {
        showBarcodeIsFavorite(barcode.isFavorite)
    }

    private fun showBarcodeIsFavorite(isFavorite: Boolean) {
        val iconId = if (isFavorite) {
            R.drawable.ic_favorite_checked
        } else {
            R.drawable.ic_favorite_unchecked
        }
        toolbar.menu?.findItem(R.id.item_add_to_favorites)?.icon = getDrawable(iconId)
    }

    private fun showBarcodeImageIfNeeded() {
        if (isCreated) {
            showBarcodeImage()
        }
    }

    private fun showBarcodeImage() {
        val codeColor = if (settings.isDarkTheme) Color.WHITE else Color.BLACK
        val backgroundColor = resources.getColor(R.color.transparent)
        try {
            val bitmap = barcodeImageGenerator.generateBitmap(originalBarcode, 2000, 2000, 0, codeColor, backgroundColor)
            image_view_barcode.isVisible = true
            image_view_barcode.setImageBitmap(bitmap)
        } catch (ex: Exception) {
            Logger.log(ex)
            image_view_barcode.isVisible = false
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

        button_search_on_rate_and_goods.isVisible = barcode.isProductBarcode && currentLocale.isRussian
        button_search_on_amazon.isVisible = barcode.isProductBarcode
        button_search_on_ebay.isVisible = barcode.isProductBarcode
        button_search_on_web.isVisible = barcode.isProductBarcode
        button_search.isVisible = barcode.isProductBarcode.not()

        button_add_to_calendar.isVisible = barcode.schema == BarcodeSchema.VEVENT
        button_add_to_contacts.isVisible = barcode.schema == BarcodeSchema.VCARD || barcode.schema == BarcodeSchema.MECARD

        button_call_phone_1.isVisible = barcode.phone.isNullOrEmpty().not()
        button_call_phone_2.isVisible = barcode.secondaryPhone.isNullOrEmpty().not()
        button_call_phone_3.isVisible = barcode.tertiaryPhone.isNullOrEmpty().not()

        button_send_sms_or_mms_1.isVisible = barcode.phone.isNullOrEmpty().not() || barcode.smsBody.isNullOrEmpty().not()
        button_send_sms_or_mms_2.isVisible = barcode.secondaryPhone.isNullOrEmpty().not() || barcode.smsBody.isNullOrEmpty().not()
        button_send_sms_or_mms_3.isVisible = barcode.tertiaryPhone.isNullOrEmpty().not() || barcode.smsBody.isNullOrEmpty().not()

        button_send_email_1.isVisible = barcode.email.isNullOrEmpty().not() || barcode.emailSubject.isNullOrEmpty().not() || barcode.emailBody.isNullOrEmpty().not()
        button_send_email_2.isVisible = barcode.secondaryEmail.isNullOrEmpty().not() || barcode.emailSubject.isNullOrEmpty().not() || barcode.emailBody.isNullOrEmpty().not()
        button_send_email_3.isVisible = barcode.tertiaryEmail.isNullOrEmpty().not() || barcode.emailSubject.isNullOrEmpty().not() || barcode.emailBody.isNullOrEmpty().not()

        button_show_location.isVisible = barcode.geoUri.isNullOrEmpty().not()
        button_connect_to_wifi.isVisible = barcode.schema == BarcodeSchema.WIFI
        button_open_wifi_settings.isVisible = barcode.schema == BarcodeSchema.WIFI
        button_copy_network_name.isVisible = barcode.networkName.isNullOrEmpty().not()
        button_copy_network_password.isVisible = barcode.networkPassword.isNullOrEmpty().not()
        button_open_in_google_play.isVisible = barcode.googlePlayUrl.isNullOrEmpty().not()
        button_open_in_youtube.isVisible = barcode.youtubeUrl.isNullOrEmpty().not()
        button_show_otp.isVisible = barcode.otpUrl.isNullOrEmpty().not()
        button_open_otp.isVisible = barcode.otpUrl.isNullOrEmpty().not()
        button_open_bitcoin_uri.isVisible = barcode.bitcoinUri.isNullOrEmpty().not()
        button_open_link.isVisible = barcode.url.isNullOrEmpty().not()
        button_save_bookmark.isVisible = barcode.schema == BarcodeSchema.BOOKMARK
    }

    private fun showButtonText() {
        button_call_phone_1.text = getString(R.string.activity_barcode_call_phone, barcode.phone)
        button_call_phone_2.text = getString(R.string.activity_barcode_call_phone, barcode.secondaryPhone)
        button_call_phone_3.text = getString(R.string.activity_barcode_call_phone, barcode.tertiaryPhone)

        button_send_sms_or_mms_1.text = getString(R.string.activity_barcode_send_sms, barcode.phone)
        button_send_sms_or_mms_2.text = getString(R.string.activity_barcode_send_sms, barcode.secondaryPhone)
        button_send_sms_or_mms_3.text = getString(R.string.activity_barcode_send_sms, barcode.tertiaryPhone)

        button_send_email_1.text = getString(R.string.activity_barcode_send_email, barcode.email)
        button_send_email_2.text = getString(R.string.activity_barcode_send_email, barcode.secondaryEmail)
        button_send_email_3.text = getString(R.string.activity_barcode_send_email, barcode.tertiaryEmail)
    }

    private fun showConnectToWifiButtonEnabled(isEnabled: Boolean) {
        button_connect_to_wifi.isEnabled = isEnabled
    }

    private fun showDeleteBarcodeConfirmationDialog() {
        val dialog = DeleteConfirmationDialogFragment.newInstance(R.string.dialog_delete_barcode_message)
        dialog.show(supportFragmentManager, "")
    }

    private fun showLoading(isLoading: Boolean) {
        progress_bar_loading.isVisible = isLoading
        scroll_view.isVisible = isLoading.not()
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
