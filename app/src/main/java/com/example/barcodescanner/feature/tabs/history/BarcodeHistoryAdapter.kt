package com.example.barcodescanner.feature.tabs.history

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.toColorId
import com.example.barcodescanner.extension.toImageId
import com.example.barcodescanner.extension.toStringId
import com.example.barcodescanner.model.Barcode
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_barcode_history.view.*
import java.text.SimpleDateFormat
import java.util.*

class BarcodeHistoryAdapter : PagedListAdapter<Barcode, BarcodeHistoryAdapter.ViewHolder>(DiffUtilCallback) {
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    val barcodeClicked = PublishSubject.create<Barcode>()
    val dataChanged = PublishSubject.create<Unit>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_barcode_history, parent, false)
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
            showDate(barcode)
            showFormat(barcode)
            showText(barcode)
            showImage(barcode)
            showImageBackgroundColor(barcode)
            setClickListener(barcode)
        }

        private fun showDate(barcode: Barcode) {
            itemView.text_view_date.text = dateFormatter.format(barcode.date)
        }

        private fun showFormat(barcode: Barcode) {
            itemView.text_view_format.setText(barcode.format.toStringId())
        }

        private fun showText(barcode: Barcode) {
            itemView.text_view_text.text = barcode.formattedText
        }

        private fun showImage(barcode: Barcode) {
            val imageId = barcode.schema.toImageId() ?: barcode.format.toImageId()
            itemView.apply {
                image_view_schema.setImageDrawable(context.resources.getDrawable(imageId))
            }
        }

        private fun showImageBackgroundColor(barcode: Barcode) {
            val colorId = barcode.format.toColorId()
            val color = itemView.context.resources.getColor(colorId)
            (itemView.layout_image.background.mutate() as GradientDrawable).setColor(color)
        }

        private fun setClickListener(barcode: Barcode) {
            itemView.setOnClickListener {
                barcodeClicked.onNext(barcode)
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