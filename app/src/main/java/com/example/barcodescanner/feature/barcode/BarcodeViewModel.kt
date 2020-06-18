package com.example.barcodescanner.feature.barcode

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.example.barcodescanner.di.barcodeDatabase
import com.example.barcodescanner.di.barcodeImageSaver
import com.example.barcodescanner.model.Barcode
import com.example.barcodescanner.model.ParsedBarcode
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class BarcodeViewModel(app: Application) : AndroidViewModel(app) {
    private val disposable = CompositeDisposable()
    val isLoading = BehaviorSubject.create<Boolean>()
    val barcodeDeleted = PublishSubject.create<Unit>()
    val error = PublishSubject.create<Throwable>()

    fun onDeleteClicked(barcode: ParsedBarcode) {
        isLoading.onNext(true)

        barcodeDatabase.delete(barcode.id)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    barcodeDeleted.onNext(Unit)
                },
                { error ->
                    isLoading.onNext(false)
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