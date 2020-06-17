package com.example.barcodescanner.feature.barcode

import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.example.barcodescanner.R
import com.example.barcodescanner.di.barcodeImageGenerator
import com.example.barcodescanner.di.barcodeImageSaver
import com.example.barcodescanner.di.barcodeSchemaParser
import com.example.barcodescanner.feature.common.showError
import com.example.barcodescanner.feature.common.toStringId
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.BarcodeSchema
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_barcode.*
import net.glxn.qrgen.core.scheme.Wifi
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

    private val barcode by lazy {
        intent?.getParcelableExtra(BARCODE_KEY) as? Barcode ?: throw IllegalArgumentException("No barcode passed")
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
            if (item.itemId == R.id.item_delete) {
                viewModel.onDeleteClicked(barcode)
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun handleButtonsClicked() {
        button_share_as_text.setOnClickListener { shareBarcodeAsText() }
        button_share_as_image.setOnClickListener { shareBarcodeAsImage() }
        button_copy.setOnClickListener { copyBarcodeTextToClipboard() }
        button_search.setOnClickListener { searchOnInternet() }
        button_open_link.setOnClickListener { startActivityWithActionView() }
        button_call_phone.setOnClickListener { startActivityWithBarcodeUri(Intent.ACTION_DIAL) }
        button_add_phone_to_contacts.setOnClickListener { addPhoneToContacts() }
        button_show_location.setOnClickListener { startActivityWithActionView() }
        button_open_in_google_play.setOnClickListener { startActivityWithActionView() }
        button_open_in_youtube.setOnClickListener { startActivityWithActionView() }
        button_copy_network_name.setOnClickListener { copyNetworkNameToClipboard() }
        button_copy_network_password.setOnClickListener { copyNetworkPasswordToClipboard() }
        button_send_sms.setOnClickListener { sendSms() }
        button_add_sms_phone_to_contacts.setOnClickListener { addSmsPhoneToContacts() }
        button_send_mms.setOnClickListener { sendSms() }
        button_add_mms_phone_to_contacts.setOnClickListener { addSmsPhoneToContacts() }
        button_send_email.setOnClickListener { sendEmail() }
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
        showBarcodeImage()
        showBarcodeDate()
        showBarcodeFormat()
        showBarcodeText()
    }

    private fun showBarcodeImage() {
        try {
            val bitmap = barcodeImageGenerator.generateImage(barcode, 2000, 2000)
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
        text_view_format.setText(format)
    }

    private fun showBarcodeText() {
        text_view_barcode_text.text = barcode.text
    }

    private fun showOrHideButtons() {
        button_open_link.isVisible = barcode.schema == BarcodeSchema.URL
        button_call_phone.isVisible = barcode.schema == BarcodeSchema.PHONE
        button_add_phone_to_contacts.isVisible = barcode.schema == BarcodeSchema.PHONE
        button_show_location.isVisible = barcode.schema == BarcodeSchema.GEO_INFO
        button_open_in_google_play.isVisible = barcode.schema == BarcodeSchema.GOOGLE_PLAY
        button_open_in_youtube.isVisible = barcode.schema == BarcodeSchema.YOUTUBE
        button_copy_network_name.isVisible = barcode.schema == BarcodeSchema.WIFI
        button_copy_network_password.isVisible = barcode.schema == BarcodeSchema.WIFI
        button_send_sms.isVisible = barcode.schema == BarcodeSchema.SMS
        button_add_sms_phone_to_contacts.isVisible = barcode.schema == BarcodeSchema.SMS
        button_send_mms.isVisible = barcode.schema == BarcodeSchema.MMS
        button_add_mms_phone_to_contacts.isVisible = barcode.schema == BarcodeSchema.MMS
        button_send_email.isVisible = barcode.schema == BarcodeSchema.EMAIL
    }


    private fun shareBarcodeAsText() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, barcode.text)
        }
        startActivityIfExists(intent)
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

    private fun copyBarcodeTextToClipboard() {
        copyToClipboard(barcode.text)
        showToast(R.string.activity_barcode_copied)
    }

    private fun copyNetworkNameToClipboard() {
        val name = Wifi.parse(barcode.text).ssid
        copyToClipboard(name)
        showToast(R.string.activity_barcode_copied)
    }

    private fun copyNetworkPasswordToClipboard() {
        val password = Wifi.parse(barcode.text).psk
        copyToClipboard(password)
        showToast(R.string.activity_barcode_copied)
    }

    private fun copyToClipboard(text: String) {
        val clipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun searchOnInternet() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, barcode.text)
        }
        startActivityIfExists(intent)
    }

    private fun sendSms() {
        val sms = barcodeSchemaParser.parseAsSms(barcode.text) ?: return
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("sms:${sms.phone}")).apply {
            putExtra("sms_body", sms.content)
        }
        startActivityIfExists(intent)
    }

    private fun addSmsPhoneToContacts() {
        val phone = barcodeSchemaParser.parseAsSms(barcode.text)?.phone ?: return
        addPhoneToContacts(phone)
    }

    private fun addPhoneToContacts() {
        val phone = barcodeSchemaParser.parseAsPhone(barcode.text) ?: return
        addPhoneToContacts(phone)
    }

    private fun addPhoneToContacts(phone: String) {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.PHONE, phone)
        }
        startActivityIfExists(intent)
    }

    private fun sendEmail() {
        val email = barcodeSchemaParser.parseAsEmail(barcode.text) ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, email.address)
            putExtra(Intent.EXTRA_SUBJECT, email.subject)
            putExtra(Intent.EXTRA_TEXT, email.body)
        }
        startActivityIfExists(intent)
    }

    private fun startActivityWithActionView() {
        startActivityWithBarcodeUri(Intent.ACTION_VIEW)
    }

    private fun startActivityWithBarcodeUri(action: String) {
        val intent = Intent(action, Uri.parse(barcode.text))
        startActivityIfExists(intent)
    }

    private fun startActivityIfExists(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast(R.string.activity_barcode_no_app)
        }
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
    }
}