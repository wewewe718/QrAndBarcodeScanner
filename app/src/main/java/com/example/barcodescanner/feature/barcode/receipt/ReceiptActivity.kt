package com.example.barcodescanner.feature.barcode.receipt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.barcodescanner.R
import com.example.barcodescanner.di.checkReceiptApi
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.feature.common.showError
import com.example.barcodescanner.feature.error.ErrorDialogFragment
import kotlinx.android.synthetic.main.activity_receipt.*

class ReceiptActivity : BaseActivity(), ErrorDialogFragment.Listener {

    companion object {
        private const val FISCAL_DRIVE_NUMBER_KEY = "FISCAL_DRIVE_NUMBER_KEY"
        private const val FISCAL_DOCUMENT_NUMBER_KEY = "FISCAL_DOCUMENT_NUMBER_KEY"
        private const val FISCAL_SIGN_KEY = "FISCAL_SIGN_KEY"

        fun start(
            context: Context,
            fiscalDriveNumber: String,
            fiscalDocumentNumber: String,
            fiscalSign: String
        ) {
            val intent = Intent(context, ReceiptActivity::class.java).apply {
                putExtra(FISCAL_DRIVE_NUMBER_KEY, fiscalDriveNumber)
                putExtra(FISCAL_DOCUMENT_NUMBER_KEY, fiscalDocumentNumber)
                putExtra(FISCAL_SIGN_KEY, fiscalSign)
            }
            context.startActivity(intent)
        }
    }

    private val fiscalDriveNumber by lazy { intent?.getStringExtra(FISCAL_DRIVE_NUMBER_KEY).orEmpty() }
    private val fiscalDocumentNumber by lazy { intent?.getStringExtra(FISCAL_DOCUMENT_NUMBER_KEY).orEmpty() }
    private val fiscalSign by lazy { intent?.getStringExtra(FISCAL_SIGN_KEY).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
        handleToolbarBackClicked()
        showReceipt()
    }

    override fun onErrorDialogPositiveButtonClicked() {
        finish()
    }

    private fun handleToolbarBackClicked() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showReceipt() {
        val url = checkReceiptApi.getReceiptUrl(fiscalDriveNumber, fiscalDocumentNumber, fiscalSign)
        web_view.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress_bar_loading.isVisible = false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                showError(Exception())
            }
        }
        web_view.loadUrl(url)
    }
}