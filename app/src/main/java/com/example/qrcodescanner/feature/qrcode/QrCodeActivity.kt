package com.example.qrcodescanner.feature.qrcode

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.qrcodescanner.R
import com.example.qrcodescanner.model.QrCode
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.Result
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_qr_code.*
import net.glxn.qrgen.android.QRCode
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

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)

    private val qrCode by lazy {
        intent?.getParcelableExtra(QR_CODE_KEY) as? QrCode ?: throw IllegalArgumentException("No QR Code passed")
    }

    private val clipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
        handleToolbarBackPressed()
        handleToolbarMenuClicked()
        handleCopyClicked()
        showQrCode()
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

            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun handleCopyClicked() {
        button_copy.setOnClickListener {
            copyQrCodeToClipboard()
        }
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
        val date = Date(qrCode.date)
        text_view_qr_code_date.text = dateFormatter.format(date)
    }

    private fun showQrCodeFormat() {
        val text = when (qrCode.format) {
            BarcodeFormat.AZTEC -> R.string.activity_qr_code_format_aztec
            BarcodeFormat.CODABAR -> R.string.activity_qr_code_format_codabar
            BarcodeFormat.CODE_39 -> R.string.activity_qr_code_format_code_39
            BarcodeFormat.CODE_93 -> R.string.activity_qr_code_format_code_93
            BarcodeFormat.CODE_128 -> R.string.activity_qr_code_format_code_128
            BarcodeFormat.DATA_MATRIX -> R.string.activity_qr_code_format_data_matrix
            BarcodeFormat.EAN_8 -> R.string.activity_qr_code_format_ean_8
            BarcodeFormat.EAN_13 -> R.string.activity_qr_code_format_ean_13
            BarcodeFormat.ITF -> R.string.activity_qr_code_format_itf
            BarcodeFormat.MAXICODE -> R.string.activity_qr_code_format_maxi_code
            BarcodeFormat.PDF_417 -> R.string.activity_qr_code_format_pdf_417
            BarcodeFormat.QR_CODE -> R.string.activity_qr_code_format_qr_code
            BarcodeFormat.RSS_14 -> R.string.activity_qr_code_format_rss_14
            BarcodeFormat.RSS_EXPANDED -> R.string.activity_qr_code_format_rss_expanded
            BarcodeFormat.UPC_A -> R.string.activity_qr_code_format_upc_a
            BarcodeFormat.UPC_E -> R.string.activity_qr_code_format_upc_e
            BarcodeFormat.UPC_EAN_EXTENSION -> R.string.activity_qr_code_format_upc_ean
        }
        text_view_qr_code_format.setText(text)
    }

    private fun showQrCodeText() {
        text_view_qr_code_text.text = qrCode.text
    }

    private fun copyQrCodeToClipboard() {
        val clipData = ClipData.newPlainText("", qrCode.text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, R.string.activity_qr_code_copied, Toast.LENGTH_SHORT).show()
    }
}