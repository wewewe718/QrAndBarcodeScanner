package com.example.qrcodescanner.feature.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodescanner.R
import com.example.qrcodescanner.feature.common.toImageId
import com.example.qrcodescanner.feature.common.toStringId
import com.example.qrcodescanner.model.QrCode
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_qr_code.view.*
import java.text.SimpleDateFormat
import java.util.*

class ScanHistoryAdapter : PagedListAdapter<QrCode, ScanHistoryAdapter.ViewHolder>(DiffUtilCallback) {
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    val qrCodeClicked = PublishSubject.create<QrCode>()
    val dataChanged = PublishSubject.create<Unit>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_qr_code, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.apply(holder::show)
    }

    override fun onCurrentListChanged(currentList: PagedList<QrCode>?) {
        super.onCurrentListChanged(currentList)
        dataChanged.onNext(Unit)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun show(qrCode: QrCode) {
            itemView.apply {
                text_view_qr_code_date.text = dateFormatter.format(qrCode.date)
                text_view_qr_code_format.setText(qrCode.format.toStringId())
                text_view_qr_code_text.text = qrCode.text
                image_view_qr_code_schema.setBackgroundResource(qrCode.schema.toImageId())
                setOnClickListener {
                    qrCodeClicked.onNext(qrCode)
                }
            }
        }
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<QrCode>() {

        override fun areItemsTheSame(oldItem: QrCode, newItem: QrCode): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QrCode, newItem: QrCode): Boolean {
            return oldItem == newItem
        }
    }
}