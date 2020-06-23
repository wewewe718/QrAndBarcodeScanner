package com.example.barcodescanner.feature.tabs.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.barcodescanner.R
import com.example.barcodescanner.feature.common.toImageId
import com.example.barcodescanner.feature.common.toStringId
import com.example.barcodescanner.model.Barcode
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_barcode.view.*
import java.text.SimpleDateFormat
import java.util.*

class BarcodeHistoryAdapter : PagedListAdapter<Barcode, BarcodeHistoryAdapter.ViewHolder>(DiffUtilCallback) {
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    val barcodeClicked = PublishSubject.create<Barcode>()
    val dataChanged = PublishSubject.create<Unit>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_barcode, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.apply(holder::show)
    }

    override fun onCurrentListChanged(currentList: PagedList<Barcode>?) {
        super.onCurrentListChanged(currentList)
        dataChanged.onNext(Unit)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun show(barcode: Barcode) {
            itemView.apply {
                text_view_date.text = dateFormatter.format(barcode.date)
                text_view_format.setText(barcode.format.toStringId())
                text_view_text.text = barcode.text
                image_view_schema.setBackgroundResource(barcode.schema.toImageId())
                setOnClickListener {
                    barcodeClicked.onNext(barcode)
                }
            }
        }
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<Barcode>() {

        override fun areItemsTheSame(oldItem: Barcode, newItem: Barcode): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Barcode, newItem: Barcode): Boolean {
            return oldItem == newItem
        }
    }
}