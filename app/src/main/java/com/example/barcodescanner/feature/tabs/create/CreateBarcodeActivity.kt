package com.example.barcodescanner.feature.tabs.create

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import com.example.barcodescanner.R
import com.example.barcodescanner.di.barcodeDatabase
import com.example.barcodescanner.di.settings
import com.example.barcodescanner.extension.showError
import com.example.barcodescanner.extension.toStringId
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.feature.barcode.BarcodeActivity
import com.example.barcodescanner.feature.tabs.create.qr.*
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
        private const val CHOOSE_PHONE_REQUEST_CODE = 1
        private const val CHOOSE_LOCATION_REQUEST_CODE = 2

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

    var isCreateBarcodeButtonEnabled: Boolean
        get() = false
        set(enabled) {
            val iconId = if (enabled) {
                R.drawable.ic_confirm_enabled
            } else {
                R.drawable.ic_confirm_disabled
            }

            toolbar.menu?.findItem(R.id.item_create_barcode)?.apply {
                icon = getDrawable(iconId)
                isEnabled = enabled
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_barcode)
        handleToolbarBackClicked()
        handleToolbarMenuItemClicked()
        showToolbarTitle()
        showToolbarMenu()
        showFragment()
        isCreateBarcodeButtonEnabled = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            CHOOSE_PHONE_REQUEST_CODE -> showChosenPhone(data)
            CHOOSE_LOCATION_REQUEST_CODE -> showChosenLocation(data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    private fun handleToolbarBackClicked() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleToolbarMenuItemClicked() {
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_contacts -> choosePhone()
                R.id.item_map -> chooseLocationOnMap()
                R.id.item_create_barcode -> createBarcode()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun showToolbarTitle() {
        val titleId = barcodeSchema?.toStringId() ?: barcodeFormat.toStringId() ?: return
        toolbar.setTitle(titleId)
    }

    private fun showToolbarMenu() {
        val menuId = when (barcodeSchema) {
            BarcodeSchema.PHONE, BarcodeSchema.SMS, BarcodeSchema.MMS -> R.menu.menu_create_qr_code_contacts
            BarcodeSchema.GEO -> R.menu.menu_create_qr_code_map
            else -> R.menu.menu_create_barcode
        }
        toolbar.inflateMenu(menuId)
    }

    private fun showFragment() {
        val fragment = when {
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.OTHER -> CreateQrCodeTextFragment()
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.URL -> CreateQrCodeUrlFragment()
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.BOOKMARK -> CreateQrCodeBookmarkFragment()
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.PHONE -> CreateQrCodePhoneFragment()
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.WIFI -> CreateQrCodeWifiFragment()
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.EMAIL -> CreateQrCodeEmailFragment()
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.SMS -> CreateQrCodeSmsFragment()
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.MMS -> CreateQrCodeMmsFragment()
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.CRYPTOCURRENCY -> CreateQrCodeCryptocurrencyFragment()
            barcodeFormat == BarcodeFormat.QR_CODE && barcodeSchema == BarcodeSchema.GEO -> CreateQrCodeLocationFragment()
            else -> return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun choosePhone() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        startActivityForResultIfExists(intent, CHOOSE_PHONE_REQUEST_CODE)
    }

    private fun showChosenPhone(data: Intent?) {
        val contactUri = data?.data ?: return
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val phone = contentResolver.query(contactUri, projection, null, null, null).use { cursor ->
            if (cursor == null || cursor.moveToFirst().not()) {
                return
            }
            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            cursor.getString(phoneIndex)
        }
        getCurrentFragment().showPhone(phone)
    }

    private fun chooseLocationOnMap() {
        val fragment = getCurrentFragment()
        val latitude = fragment.latitude
        val longitude = fragment.longitude
        ChooseLocationOnMapActivity.start(this, CHOOSE_LOCATION_REQUEST_CODE, latitude, longitude)
    }

    private fun showChosenLocation(data: Intent?) {
        val latitude = data?.getDoubleExtra(ChooseLocationOnMapActivity.LATITUDE_KEY, 0.0)
        val longitude = data?.getDoubleExtra(ChooseLocationOnMapActivity.LONGITUDE_KEY, 0.0)
        getCurrentFragment().showLocation(latitude, longitude)
    }

    private fun startActivityForResultIfExists(intent: Intent, requestCode: Int) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, requestCode)
        } else {
            Toast.makeText(this, R.string.activity_barcode_no_app, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createBarcode() {
        val schema = getCurrentFragment().getBarcodeSchema()

        val barcode = Barcode(
            text = schema.toBarcodeText(),
            formattedText = schema.toFormattedText(),
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

    private fun getCurrentFragment(): BaseCreateBarcodeFragment {
        return supportFragmentManager.findFragmentById(R.id.container) as BaseCreateBarcodeFragment
    }

    private fun navigateToBarcodeScreen(barcode: Barcode) {
        BarcodeActivity.start(this, barcode, true)
    }
}