package com.example.barcodescanner.feature.scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.barcodescanner.di.barcodeSchemaParser
import com.example.barcodescanner.di.barcodeDatabase
import com.example.barcodescanner.model.Barcode
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class ScanBarcodeViewModel(app: Application) : AndroidViewModel(app) {
    private val disposable = CompositeDisposable()
    val isLoading = BehaviorSubject.create<Boolean>()
    val error = PublishSubject.create<Throwable>()
    val barcodeSaved = PublishSubject.create<Barcode>()

    fun onScanResult(result: Result) {
        val barcode = Barcode(
            text = result.text,
            format = result.barcodeFormat,
            schema = barcodeSchemaParser.parseSchema(result.text),
            date = result.timestamp,
            errorCorrectionLevel = result.resultMetadata?.get(ResultMetadataType.ERROR_CORRECTION_LEVEL) as? String
        )

        isLoading.onNext(true)

        barcodeDatabase.save(barcode)
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    barcodeSaved.onNext(barcode)
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