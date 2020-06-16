package com.example.barcodescanner.feature.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barcodescanner.R
import com.example.barcodescanner.feature.common.showError
import com.example.barcodescanner.feature.permission.RequestPermissionsActivity
import com.example.barcodescanner.feature.barcode.BarcodeActivity
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_barcode_history.*

class BarcodeHistoryActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()
    private val scanHistoryAdapter = BarcodeHistoryAdapter()
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(BarcodeHistoryActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_history)
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        subscribeToViewModel()
    }

    override fun onPause() {
        super.onPause()
        unsubscribeFromViewModel()
    }

    private fun initRecyclerView() {
        recycler_view_history.apply {
            layoutManager = LinearLayoutManager(this@BarcodeHistoryActivity)
            adapter = scanHistoryAdapter
        }
    }

    private fun subscribeToViewModel() {
        subscribeToBarcodeClicks()
        subscribeToHistoryDataChanged()
        subscribeToScanButtonClicks()
        subscribeToHistory()
        subscribeToError()
        subscribeToNavigateToBarcodeScreen()
        subscribeToNavigateToRequestPermissionsScreenEvent()
    }

    private fun subscribeToBarcodeClicks() {
        scanHistoryAdapter.barcodeClicked
            .subscribe(viewModel::onBarcodeClicked)
            .addTo(disposable)
    }

    private fun subscribeToHistoryDataChanged() {
        scanHistoryAdapter.dataChanged
            .subscribe {
                recycler_view_history.layoutManager?.scrollToPosition(0)
            }
            .addTo(disposable)
    }

    private fun subscribeToScanButtonClicks() {
        fab_scan_barcode.clicks()
            .subscribe {
                viewModel.onScanBarcodeClicked()
            }
            .addTo(disposable)
    }

    private fun subscribeToHistory() {
        viewModel.scanHistory
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(scanHistoryAdapter::submitList)
            .addTo(disposable)
    }

    private fun subscribeToError() {
        viewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::showError)
            .addTo(disposable)
    }

    private fun subscribeToNavigateToBarcodeScreen() {
        viewModel.navigateToBarcodeScreenEvent
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { barcode ->
                BarcodeActivity.start(this, barcode)
            }
            .addTo(disposable)
    }

    private fun subscribeToNavigateToRequestPermissionsScreenEvent() {
        viewModel.navigateToRequestPermissionsScreenEvent
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                RequestPermissionsActivity.start(this)
            }
            .addTo(disposable)
    }

    private fun unsubscribeFromViewModel() {
        disposable.clear()
    }
}
