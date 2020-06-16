package com.example.barcodescanner.feature.barcode

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.barcodescanner.di.db
import com.example.barcodescanner.model.Barcode
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

    fun onDeleteClicked(barcode: Barcode) {
        isLoading.onNext(true)

        db.delete(barcode)
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