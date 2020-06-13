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
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_qr_code.*
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
        handleCopyClicked()
        handleSearchClicked()
        handleOpenLinkClicked()
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

    private fun handleCopyClicked() {
        button_copy.setOnClickListener {
            copyToClipboard()
        }
    }

    private fun handleSearchClicked() {
        button_search.setOnClickListener {
            searchOnInternet()
        }
    }

    private fun handleOpenLinkClicked() {
        button_open_link.setOnClickListener {
            openLink()
        }
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
            val writer = MultiFormatWriter()
            val bitMatrix = writer.encode(qrCode.text, qrCode.format, 2000, 2000)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            image_view_qr_code.setImageBitmap(bitmap)
        } catch (ex: Exception) {
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
    }

    private fun copyToClipboard() {
        val clipData = ClipData.newPlainText("", qrCode.text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, R.string.activity_qr_code_copied, Toast.LENGTH_SHORT).show()
    }

    private fun searchOnInternet() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, qrCode.text)
        startActivityIfExists(intent)
    }

    private fun openLink() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(qrCode.text)
        startActivityIfExists(intent)
    }

    private fun startActivityIfExists(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}