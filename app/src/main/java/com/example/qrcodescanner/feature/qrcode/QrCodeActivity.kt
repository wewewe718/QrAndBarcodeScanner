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
import com.example.qrcodescanner.common.showError
import com.example.qrcodescanner.common.toStringId
import com.example.qrcodescanner.model.BarcodeSchema
import com.example.qrcodescanner.model.QrCode
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
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
        button_copy.setOnClickListener { copyQrCodeTextToClipboard() }
        button_search.setOnClickListener { searchOnInternet() }
        button_open_link.setOnClickListener { startActivityWithActionView() }
        button_call_phone.setOnClickListener { startActivityWithQrCodeUri(Intent.ACTION_DIAL) }
        button_show_location.setOnClickListener { startActivityWithActionView() }
        button_open_in_google_play.setOnClickListener { startActivityWithActionView() }
        button_open_in_youtube.setOnClickListener { startActivityWithActionView() }
        button_copy_network_name.setOnClickListener { copyNetworkNameToClipboard() }
        button_copy_network_password.setOnClickListener { copyNetworkPasswordToClipboard() }
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
            val bitmap = BarcodeEncoder().encodeBitmap(qrCode.text, qrCode.format, 2000, 2000, mapOf(
                EncodeHintType.MARGIN to 0
            ))
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
        button_open_link.isVisible = qrCode.scheme == BarcodeSchema.URL
        button_call_phone.isVisible = qrCode.scheme == BarcodeSchema.TELEPHONE
        button_show_location.isVisible = qrCode.scheme == BarcodeSchema.GEO_INFO
        button_open_in_google_play.isVisible = qrCode.scheme == BarcodeSchema.GOOGLE_PLAY
        button_open_in_youtube.isVisible = qrCode.scheme == BarcodeSchema.YOUTUBE
        button_copy_network_name.isVisible = qrCode.scheme == BarcodeSchema.WIFI
        button_copy_network_password.isVisible = qrCode.scheme == BarcodeSchema.WIFI && isNetworkPasswordPresent()
    }

    private fun isNetworkPasswordPresent(): Boolean {
        return Wifi.parse(qrCode.text).psk != null
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
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, qrCode.text)
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