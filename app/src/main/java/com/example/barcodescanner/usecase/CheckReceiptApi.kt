package com.example.barcodescanner.usecase

import com.example.barcodescanner.BuildConfig
import io.reactivex.Single
import io.reactivex.SingleEmitter
import okhttp3.*
import java.io.IOException

class CheckReceiptApi {

    enum class Status {
        VALID,
        INVALID
    }

    private val client by lazy { OkHttpClient() }

    fun checkReceipt(
        type: Int,
        time: String,
        fiscalDriveNumber: String,
        fiscalDocumentNumber: String,
        fiscalSign: String,
        sum: String
    ): Single<Status> {
        return Single.create { emitter ->
            makeRequest(type, time, fiscalDriveNumber, fiscalDocumentNumber, fiscalSign, sum, emitter)
        }
    }

    private fun makeRequest(
        type: Int,
        time: String,
        fiscalDriveNumber: String,
        fiscalDocumentNumber: String,
        fiscalSign: String,
        sum: String,
        emitter: SingleEmitter<Status>
    ) {
        val newSum = formatSum(sum)
        val url = "${BuildConfig.CHECK_RECEIPT_API_URL}/fss/$fiscalDriveNumber/operations/$type/tickets/$fiscalDocumentNumber?fiscalSign=$fiscalSign&date=$time&sum=$newSum"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, emitter)
            }

            override fun onFailure(call: Call, error: IOException) {
                handleError(error, emitter)
            }
        })
    }

    private fun formatSum(sum: String): String {
        val delimiter = sum.indexOf('.')
        if (delimiter != -1) {
            return sum.removeRange(delimiter, delimiter + 1)
        }
        return sum
    }

    private fun handleResponse(response: Response, emitter: SingleEmitter<Status>) {
        response.use {
            if (response.isSuccessful) {
                emitter.onSuccess(Status.VALID)
            } else {
                emitter.onSuccess(Status.INVALID)
            }
        }
    }

    private fun handleError(error: IOException, emitter: SingleEmitter<Status>) {
        emitter.onError(error)
    }
}