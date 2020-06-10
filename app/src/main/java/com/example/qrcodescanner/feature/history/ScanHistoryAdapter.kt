package com.example.qrcodescanner.feature.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodescanner.R
import com.example.qrcodescanner.model.QrCode
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_qr_code.view.*

class ScanHistoryAdapter : PagedListAdapter<QrCode, ScanHistoryAdapter.ViewHolder>(DiffUtilCallback) {
    val qrCodeClicked = PublishSubject.create<QrCode>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_qr_code, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.apply(holder::show)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun show(qrCode: QrCode) {
            itemView.setOnClickListener {
                qrCodeClicked.onNext(qrCode)
            }

            itemView.apply {
                text_view_qr_code_text.text = qrCode.text
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