package com.example.barcodescanner.feature.tabs.create

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.barcodescanner.R
import com.example.barcodescanner.di.barcodeDatabase
import com.example.barcodescanner.di.settings
import com.example.barcodescanner.extension.showError
import com.example.barcodescanner.extension.toStringId
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.feature.barcode.BarcodeActivity
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_barcode.*

class CreateBarcodeActivity : BaseActivity() {

    companion object {
        private const val BARCODE_FORMAT_KEY = "BARCODE_FORMAT_KEY"
        private const val BARCODE_SCHEMA_KEY = "BARCODE_SCHEMA_KEY"

        fun start(context: Context, barcodeFormat: BarcodeFormat, barcodeSchema: BarcodeSchema? = null) {
            val intent = Intent(context, CreateBarcodeActivity::class.java).apply {
                putExtra(BARCODE_FORMAT_KEY, barcodeFormat.ordinal)
                putExtra(BARCODE_SCHEMA_KEY, barcodeSchema?.ordinal ?: -1)
            }
            context.startActivity(intent)
        }
    }

    private val disposable = CompositeDisposable()

    private val barcodeFormat by lazy {
        BarcodeFormat.values().getOrNull(intent?.getIntExtra(BARCODE_FORMAT_KEY, -1) ?: -1)
    }

    private val barcodeSchema by lazy {
        BarcodeSchema.values().getOrNull(intent?.getIntExtra(BARCODE_SCHEMA_KEY, -1) ?: -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_barcode)
        initToolbar()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    fun enableCreateBarcodeButton() {
        showConfirmMenuItemEnabled()
    }

    fun disableCreateBarcodeButton() {
        showConfirmMenuItemDisabled()
    }

    private fun initToolbar() {
        showToolbarTitle()
        showToolbarMenu()
        handleToolbarBackClicked()
        handleToolbarMenuItemClicked()
    }

    private fun showToolbarTitle() {
        val titleId = barcodeSchema?.toStringId() ?: barcodeFormat?.toStringId() ?: return
        toolbar.setTitle(titleId)
    }

    private fun showToolbarMenu() {
        toolbar.inflateMenu(R.menu.menu_create_barcode)
    }

    private fun handleToolbarBackClicked() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleToolbarMenuItemClicked() {
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.item_create_barcode) {
                createBarcode()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun showConfirmMenuItemEnabled() {
        toolbar.menu?.findItem(R.id.item_create_barcode)?.icon = getDrawable(R.drawable.ic_confirm_enabled)
    }

    private fun showConfirmMenuItemDisabled() {
        toolbar.menu?.findItem(R.id.item_create_barcode)?.icon = getDrawable(R.drawable.ic_confirm_disabled)
    }

    private fun createBarcode() {
        val barcode = Barcode(
            text = "",
            formattedText = "",
            format = barcodeFormat ?: BarcodeFormat.QR_CODE,
            schema = barcodeSchema ?: BarcodeSchema.OTHER,
            isGenerated = true,
            date = System.currentTimeMillis()
        )

        if (settings.saveCreatedBarcodesToHistory.not()) {
            navigateToBarcodeScreen(barcode)
            return
        }

        barcodeDatabase.save(barcode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { id ->
                    navigateToBarcodeScreen(barcode.copy(id = id))
                },
                ::showError
            )
            .addTo(disposable)
    }

    private fun navigateToBarcodeScreen(barcode: Barcode) {
        BarcodeActivity.start(this, barcode)
    }
}