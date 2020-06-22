package com.example.barcodescanner.usecase

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.example.barcodescanner.BuildConfig
import com.example.barcodescanner.R
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

    fun downloadReceipt(context: Context, fiscalDriveNumber: String, fiscalDocumentNumber: String, fiscalSign: String) {
        val url = "${BuildConfig.OFD_URL}?FnNumber=$fiscalDriveNumber&DocNumber=$fiscalDocumentNumber&DocFiscalSign=$fiscalSign&format=pdf"
        val uri = Uri.parse(url)
        val fileName = "${context.getString(R.string.activity_check_receipt_download_file_name)}.pdf"

        val request = DownloadManager.Request(uri).apply {
            setTitle(context.getString(R.string.activity_check_receipt_download_title))
            setDescription(context.getString(R.string.activity_check_receipt_download_description))
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setVisibleInDownloadsUi(true)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    fun getReceiptUrl(fiscalDriveNumber: String, fiscalDocumentNumber: String, fiscalSign: String): String {
        return "${BuildConfig.OFD_URL}?FnNumber=$fiscalDriveNumber&DocNumber=$fiscalDocumentNumber&DocFiscalSign=$fiscalSign&format=html"
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