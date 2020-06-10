package com.example.qrcodescanner.feature.scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.qrcodescanner.common.db
import com.example.qrcodescanner.model.QrCode
import io.reactivex.disposables.CompositeDisposable
import com.google.zxing.Result
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ScanQrCodeViewModel(app: Application) : AndroidViewModel(app) {
    private val disposable = CompositeDisposable()
    val isLoading = BehaviorSubject.create<Boolean>()
    val error = PublishSubject.create<Throwable>()
    val qrCodeSaved = PublishSubject.create<QrCode>()

    fun onScanResult(result: Result) {
        isLoading.onNext(true)
        val qrCode = QrCode(result)
        db.save(qrCode)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    qrCodeSaved.onNext(qrCode)
                },
                { e ->
                    isLoading.onNext(false)
                    error.onNext(e)
                }
            )
            .addTo(disposable)
    }

    fun onScanError(error: Throwable) {
        this.error.onNext(error)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}