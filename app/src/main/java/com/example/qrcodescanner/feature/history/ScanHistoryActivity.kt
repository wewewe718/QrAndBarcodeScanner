package com.example.qrcodescanner.feature.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrcodescanner.R
import com.example.qrcodescanner.common.showError
import com.example.qrcodescanner.feature.permission.RequestPermissionsActivity
import com.example.qrcodescanner.feature.qrcode.QrCodeActivity
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_scan_history.*

class ScanHistoryActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()
    private val scanHistoryAdapter = ScanHistoryAdapter()
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ScanHistoryActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_history)
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
        recycler_view_scan_history.apply {
            layoutManager = LinearLayoutManager(this@ScanHistoryActivity)
            adapter = scanHistoryAdapter
        }
    }

    private fun subscribeToViewModel() {
        subscribeToQrCodeClicks()
        subscribeToScanHistoryDataChanged()
        subscribeToScanButtonClicks()
        subscribeToScanHistory()
        subscribeToError()
        subscribeToNavigateToQrCodeScreen()
        subscribeToNavigateToRequestPermissionsScreenEvent()
    }

    private fun subscribeToQrCodeClicks() {
        scanHistoryAdapter.qrCodeClicked
            .subscribe(viewModel::onQrCodeClicked)
            .addTo(disposable)
    }

    private fun subscribeToScanHistoryDataChanged() {
        scanHistoryAdapter.dataChanged
            .subscribe {
                recycler_view_scan_history.layoutManager?.scrollToPosition(0)
            }
            .addTo(disposable)
    }

    private fun subscribeToScanButtonClicks() {
        fab_scan_qr_code.clicks()
            .subscribe {
                viewModel.onScanQrCodeClicked()
            }
            .addTo(disposable)
    }

    private fun subscribeToScanHistory() {
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

    private fun subscribeToNavigateToQrCodeScreen() {
        viewModel.navigateToQrCodeScreenEvent
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { qrCode ->
                QrCodeActivity.start(this, qrCode)
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
