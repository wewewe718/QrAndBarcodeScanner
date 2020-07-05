package com.example.barcodescanner.feature.tabs.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.example.barcodescanner.di.barcodeDatabase
import com.example.barcodescanner.model.Barcode
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class BarcodeHistoryViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        private const val PAGE_SIZE = 20
    }

    private val disposable = CompositeDisposable()
    val scanHistory = BehaviorSubject.create<PagedList<Barcode>>()
    val error = PublishSubject.create<Throwable>()
    val navigateToBarcodeScreenEvent = PublishSubject.create<Barcode>()

    init {
        loadHistory()
    }

    fun onBarcodeClicked(barcode: Barcode) {
        navigateToBarcodeScreenEvent.onNext(barcode)
    }

    private fun loadHistory() {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PAGE_SIZE)
            .build()

        RxPagedListBuilder<Int, Barcode>(barcodeDatabase.getAll(), config)
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