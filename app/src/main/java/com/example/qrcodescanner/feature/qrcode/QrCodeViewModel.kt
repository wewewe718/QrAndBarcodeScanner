package com.example.qrcodescanner.feature.qrcode

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.qrcodescanner.common.db
import com.example.qrcodescanner.model.QrCode
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class QrCodeViewModel(app: Application) : AndroidViewModel(app) {
    private val disposable = CompositeDisposable()
    val isLoading = BehaviorSubject.create<Boolean>()
    val qrCodeDeleted = PublishSubject.create<Unit>()
    val error = PublishSubject.create<Throwable>()

    fun onDeleteClicked(qrCode: QrCode) {
        isLoading.onNext(true)

        db.delete(qrCode)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    qrCodeDeleted.onNext(Unit)
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