package com.example.qrcodescanner.feature.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.example.qrcodescanner.common.db
import com.example.qrcodescanner.model.QrCode
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ScanHistoryActivityViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private val disposable = CompositeDisposable()
    val scanHistory = BehaviorSubject.create<PagedList<QrCode>>()
    val error = PublishSubject.create<Throwable>()
    val navigateToQrCodeScreenEvent = PublishSubject.create<QrCode>()
    val navigateToRequestPermissionsScreenEvent = PublishSubject.create<Unit>()

    init {
        loadScanHistory()
    }

    fun onQrCodeClicked(qrCode: QrCode) {
        navigateToQrCodeScreenEvent.onNext(qrCode)
    }

    fun onScanQrCodeClicked() {
        navigateToRequestPermissionsScreenEvent.onNext(Unit)
    }

    private fun loadScanHistory() {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_SIZE)
            .build()

        RxPagedListBuilder<Int, QrCode>(db.getAll(), config)
            .buildFlowable(BackpressureStrategy.LATEST)
            .subscribe(
                { scanHistory ->
                    this.scanHistory.onNext(scanHistory)
                },
                { error ->
                    this.error.onNext(error)
                }
            )
            .addTo(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}