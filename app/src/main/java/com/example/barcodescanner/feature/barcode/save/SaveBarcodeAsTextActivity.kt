package com.example.barcodescanner.feature.barcode.save

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.barcodescanner.R
import com.example.barcodescanner.di.barcodeSaver
import com.example.barcodescanner.extension.showError
import com.example.barcodescanner.extension.unsafeLazy
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.model.Barcode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_save_barcode_as_text.*

class SaveBarcodeAsTextActivity : BaseActivity() {

    companion object {
        private const val BARCODE_KEY = "BARCODE_KEY"

        fun start(context: Context, barcode: Barcode) {
            val intent = Intent(context, SaveBarcodeAsTextActivity::class.java).apply {
                putExtra(BARCODE_KEY, barcode)
            }
            context.startActivity(intent)
        }
    }

    private val barcode by unsafeLazy {
        intent?.getSerializableExtra(BARCODE_KEY) as? Barcode ?: throw IllegalArgumentException("No barcode passed")
    }

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_barcode_as_text)
        initToolbar()
        initFormatSpinner()
        initSaveButton()
    }

    private fun initToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initFormatSpinner() {
        spinner_save_as.adapter = ArrayAdapter.createFromResource(
            this, R.array.activity_save_barcode_as_text_formats, android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun initSaveButton() {
        button_save.setOnClickListener {
            saveBarcode()
        }
    }

    private fun saveBarcode() {
        val saveFunc = when (spinner_save_as.selectedItemPosition) {
            0 -> barcodeSaver::saveBarcodeAsCsv
            1 -> barcodeSaver::saveBarcodeAsJson
            else -> return
        }

        showLoading(true)

        saveFunc(this, barcode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { showBarcodeSaved() },
                { error ->
                    showLoading(false)
                    showError(error)
                }
            )
            .addTo(disposable)
    }

    private fun showLoading(isLoading: Boolean) {
        progress_bar_loading.isVisible = isLoading
        scroll_view.isVisible = isLoading.not()
    }

    private fun showBarcodeSaved() {
        Toast.makeText(this, R.string.activity_save_barcode_as_text_file_name_saved, Toast.LENGTH_LONG).show()
        finish()
    }
}