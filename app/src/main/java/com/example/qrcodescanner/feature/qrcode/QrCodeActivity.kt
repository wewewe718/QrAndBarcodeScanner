package com.example.qrcodescanner.feature.qrcode

import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.example.qrcodescanner.R
import com.example.qrcodescanner.barcodeImageGenerator
import com.example.qrcodescanner.barcodeSchemaParser
import com.example.qrcodescanner.feature.common.showError
import com.example.qrcodescanner.feature.common.toStringId
import com.example.qrcodescanner.model.BarcodeSchema
import com.example.qrcodescanner.model.QrCode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_qr_code.*
import net.glxn.qrgen.core.scheme.Wifi
import java.text.SimpleDateFormat
import java.util.*

class QrCodeActivity : AppCompatActivity() {

    companion object {
        private const val QR_CODE_KEY = "QR_CODE_KEY"

        fun start(context: Context, qrCode: QrCode) {
            val intent = Intent(context, QrCodeActivity::class.java)
            intent.putExtra(QR_CODE_KEY, qrCode)
            context.startActivity(intent)
        }
    }


    private val disposable = CompositeDisposable()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

    private val qrCode by lazy {
        intent?.getParcelableExtra(QR_CODE_KEY) as? QrCode ?: throw IllegalArgumentException("No QR Code passed")
    }

    private val clipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(QrCodeViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
        handleToolbarBackPressed()
        handleToolbarMenuClicked()
        handleButtonsClicked()
        showQrCode()
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
        toolbar.inflateMenu(R.menu.menu_qr_code)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.item_delete) {
                viewModel.onDeleteClicked(qrCode)
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun handleButtonsClicked() {
        button_share.setOnClickListener { shareQrCodeText() }
        button_copy.setOnClickListener { copyQrCodeTextToClipboard() }
        button_search.setOnClickListener { searchOnInternet() }
        button_open_link.setOnClickListener { startActivityWithActionView() }
        button_call_phone.setOnClickListener { startActivityWithQrCodeUri(Intent.ACTION_DIAL) }
        button_show_location.setOnClickListener { startActivityWithActionView() }
        button_open_in_google_play.setOnClickListener { startActivityWithActionView() }
        button_open_in_youtube.setOnClickListener { startActivityWithActionView() }
        button_copy_network_name.setOnClickListener { copyNetworkNameToClipboard() }
        button_copy_network_password.setOnClickListener { copyNetworkPasswordToClipboard() }
        button_send_sms.setOnClickListener { sendSms() }
        button_send_mms.setOnClickListener { sendMms() }
        button_send_email.setOnClickListener { sendEmail() }
    }


    private fun subscribeToViewModel() {
        subscribeToLoading()
        subscribeToError()
        subscribeToQrCodeDeleted()
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

    private fun subscribeToQrCodeDeleted() {
        viewModel.qrCodeDeleted
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

    private fun showQrCode() {
        showQrCodeImage()
        showQrCodeDate()
        showQrCodeFormat()
        showQrCodeText()
    }

    private fun showQrCodeImage() {
        try {
            val bitmap = barcodeImageGenerator.generateImage(qrCode)
            image_view_qr_code.setImageBitmap(bitmap)
        } catch (ex: Exception) {
            image_view_qr_code.isVisible = false
            ex.printStackTrace()
        }
    }

    private fun showQrCodeDate() {
        text_view_qr_code_date.text = dateFormatter.format(qrCode.date)
    }

    private fun showQrCodeFormat() {
        val format = qrCode.format.toStringId()
        toolbar.setTitle(format)
        text_view_qr_code_format.setText(format)
    }

    private fun showQrCodeText() {
        text_view_qr_code_text.text = qrCode.text
    }

    private fun showOrHideButtons() {
        button_open_link.isVisible = qrCode.schema == BarcodeSchema.URL
        button_call_phone.isVisible = qrCode.schema == BarcodeSchema.TELEPHONE
        button_show_location.isVisible = qrCode.schema == BarcodeSchema.GEO_INFO
        button_open_in_google_play.isVisible = qrCode.schema == BarcodeSchema.GOOGLE_PLAY
        button_open_in_youtube.isVisible = qrCode.schema == BarcodeSchema.YOUTUBE
        button_copy_network_name.isVisible = qrCode.schema == BarcodeSchema.WIFI
        button_copy_network_password.isVisible = qrCode.schema == BarcodeSchema.WIFI && isNetworkPasswordPresent()
        button_send_sms.isVisible = qrCode.schema == BarcodeSchema.SMS
        button_send_mms.isVisible = qrCode.schema == BarcodeSchema.MMS
        button_send_email.isVisible = qrCode.schema == BarcodeSchema.EMAIL
    }

    private fun isNetworkPasswordPresent(): Boolean {
        return Wifi.parse(qrCode.text).psk != null
    }


    private fun shareQrCodeText() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, qrCode.text)
        }
        startActivityIfExists(intent)
    }

    private fun copyQrCodeTextToClipboard() {
        copyToClipboard(qrCode.text)
        showToast(R.string.activity_qr_code_copied)
    }

    private fun copyNetworkNameToClipboard() {
        val name = Wifi.parse(qrCode.text).ssid
        copyToClipboard(name)
        showToast(R.string.activity_qr_code_copied)
    }

    private fun copyNetworkPasswordToClipboard() {
        val password = Wifi.parse(qrCode.text).psk
        copyToClipboard(password)
        showToast(R.string.activity_qr_code_copied)
    }

    private fun copyToClipboard(text: String) {
        val clipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun searchOnInternet() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, qrCode.text)
        }
        startActivityIfExists(intent)
    }

    private fun sendSms() {
        sendMms()
    }

    private fun sendMms() {
        val sms = barcodeSchemaParser.parseAsSms(qrCode.text) ?: return
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("sms:${sms.phone}")).apply {
            putExtra("sms_body", sms.content)
        }
        startActivityIfExists(intent)
    }

    private fun sendEmail() {
        val email = barcodeSchemaParser.parseAsEmail(qrCode.text) ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, email.address)
            putExtra(Intent.EXTRA_SUBJECT, email.subject)
            putExtra(Intent.EXTRA_TEXT, email.body)
        }
        startActivityIfExists(intent)
    }

    private fun startActivityWithActionView() {
        startActivityWithQrCodeUri(Intent.ACTION_VIEW)
    }

    private fun startActivityWithQrCodeUri(action: String) {
        val intent = Intent(action, Uri.parse(qrCode.text))
        startActivityIfExists(intent)
    }

    private fun startActivityIfExists(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast(R.string.activity_qr_code_no_app)
        }
    }

    private fun showToast(stringId: Int) {
        showToast(getString(stringId))
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}