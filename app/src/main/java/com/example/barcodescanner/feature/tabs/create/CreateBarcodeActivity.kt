package com.example.barcodescanner.feature.tabs.create

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.barcodescanner.R
import com.example.barcodescanner.di.barcodeDatabase
import com.example.barcodescanner.di.barcodeSchemaParser
import com.example.barcodescanner.di.settings
import com.example.barcodescanner.extension.showError
import com.example.barcodescanner.extension.toStringId
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.feature.barcode.BarcodeActivity
import com.example.barcodescanner.feature.tabs.create.qr.CreateQrCodeTextFragment
import com.example.barcodescanner.feature.tabs.create.qr.CreateQrCodeUrlFragment
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
            ?: throw IllegalArgumentException("No barcode format passed")
    }

    private val barcodeSchema by lazy {
        BarcodeSchema.values().getOrNull(intent?.getIntExtra(BARCODE_SCHEMA_KEY, -1) ?: -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_barcode)
        handleToolbarBackClicked()
        handleToolbarMenuItemClicked()
        showToolbarTitle()
        showFragment()
        disableCreateBarcodeButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    fun enableCreateBarcodeButton() {
        toolbar.menu?.findItem(R.id.item_create_barcode)?.apply {
            icon = getDrawable(R.drawable.ic_confirm_enabled)
            isEnabled = true
        }
    }

    fun disableCreateBarcodeButton() {
        toolbar.menu?.findItem(R.id.item_create_barcode)?.apply {
            icon = getDrawable(R.drawable.ic_confirm_disabled)
            isEnabled = false
        }
    }

    private fun handleToolbarBackClicked() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleToolbarMenuItemClicked() {
        toolbar.inflateMenu(R.menu.menu_create_barcode)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.item_create_barcode) {
                createBarcode()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun showToolbarTitle() {
        val titleId = barcodeSchema?.toStringId() ?: barcodeFormat.toStringId() ?: return
        toolbar.setTitle(titleId)
    }

    private fun showFragment() {
        val fragment = when {
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.OTHER -> CreateQrCodeTextFragment()
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.URL -> CreateQrCodeUrlFragment()
            else -> return
        }
        showFragment(fragment)
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun getCurrentFragment(): BaseCreateBarcodeFragment {
        return supportFragmentManager.findFragmentById(R.id.container) as BaseCreateBarcodeFragment
    }

    private fun createBarcode() {
        val barcodeText = getCurrentFragment().getBarcodeText()
        val formattedText = barcodeSchemaParser.getSchema(barcodeFormat, barcodeText).toFormattedText()

        val barcode = Barcode(
            text = barcodeText,
            formattedText = formattedText,
            format = barcodeFormat,
            schema = barcodeSchema ?: BarcodeSchema.OTHER,
            date = System.currentTimeMillis(),
            isGenerated = true
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
        BarcodeActivity.start(this, barcode, true)
    }
}