package com.example.barcodescanner.feature.barcode.receipt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.barcodescanner.R
import com.example.barcodescanner.di.checkReceiptApi
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.extension.orZero
import com.example.barcodescanner.extension.showError
import com.example.barcodescanner.feature.common.ErrorDialogFragment
import com.example.barcodescanner.usecase.CheckReceiptApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_check_receipt.*

class CheckReceiptActivity : BaseActivity(), ErrorDialogFragment.Listener {

    companion object {
        private const val TYPE_KEY = "TYPE_KEY"
        private const val TIME_KEY = "TIME_KEY"
        private const val FISCAL_DRIVE_NUMBER_KEY = "FISCAL_DRIVE_NUMBER_KEY"
        private const val FISCAL_DOCUMENT_NUMBER_KEY = "FISCAL_DOCUMENT_NUMBER_KEY"
        private const val FISCAL_SIGN_KEY = "FISCAL_SIGN_KEY"
        private const val SUM_KEY = "SUM_KEY"

        fun start(
            context: Context,
            type: Int,
            time: String,
            fiscalDriveNumber: String,
            fiscalDocumentNumber: String,
            fiscalSign: String,
            sum: String
        ) {
            val intent = Intent(context, CheckReceiptActivity::class.java).apply {
                putExtra(TYPE_KEY, type)
                putExtra(TIME_KEY, time)
                putExtra(FISCAL_DRIVE_NUMBER_KEY, fiscalDriveNumber)
                putExtra(FISCAL_DOCUMENT_NUMBER_KEY, fiscalDocumentNumber)
                putExtra(FISCAL_SIGN_KEY, fiscalSign)
                putExtra(SUM_KEY, sum)
            }
            context.startActivity(intent)
        }
    }

    private val disposable = CompositeDisposable()
    private val type by lazy { intent?.getIntExtra(TYPE_KEY, 0).orZero() }
    private val time by lazy { intent?.getStringExtra(TIME_KEY).orEmpty() }
    private val fiscalDriveNumber by lazy { intent?.getStringExtra(FISCAL_DRIVE_NUMBER_KEY).orEmpty() }
    private val fiscalDocumentNumber by lazy { intent?.getStringExtra(FISCAL_DOCUMENT_NUMBER_KEY).orEmpty() }
    private val fiscalSign by lazy { intent?.getStringExtra(FISCAL_SIGN_KEY).orEmpty() }
    private val sum by lazy { intent?.getStringExtra(SUM_KEY).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_receipt)
        handleToolbarBackClicked()
        handleShowReceiptClicked()
        handleDownloadReceiptClicked()
        checkReceipt()
    }

    override fun onErrorDialogPositiveButtonClicked() {
        finish()
    }

    private fun handleToolbarBackClicked() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleShowReceiptClicked() {
        button_show_receipt.setOnClickListener {
            navigateToReceiptScreen()
        }
    }

    private fun handleDownloadReceiptClicked() {
        button_download_receipt.setOnClickListener {
            downloadReceipt()
        }
    }

    private fun checkReceipt() {
        checkReceiptApi
            .checkReceipt(type, time, fiscalDriveNumber, fiscalDocumentNumber, fiscalSign, sum)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::showResult, ::showError)
            .addTo(disposable)
    }

    private fun showResult(result: CheckReceiptApi.Status) {
        when (result) {
            CheckReceiptApi.Status.VALID -> showReceiptIsValid()
            CheckReceiptApi.Status.INVALID -> showReceiptIsInvalid()
        }
    }

    private fun showReceiptIsValid() {
        progress_bar_loading.isVisible = false
        group_receipt_is_valid.isVisible = true
    }

    private fun showReceiptIsInvalid() {
        progress_bar_loading.isVisible = false
        group_receipt_is_invalid.isVisible = true
    }

    private fun navigateToReceiptScreen() {
        ReceiptActivity.start(this, fiscalDriveNumber, fiscalDocumentNumber, fiscalSign)
    }

    private fun downloadReceipt() {
        checkReceiptApi.downloadReceipt(fiscalDriveNumber, fiscalDocumentNumber, fiscalSign)
        showToast(R.string.activity_check_receipt_download_started)
    }

    private fun showToast(stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show()
    }
}