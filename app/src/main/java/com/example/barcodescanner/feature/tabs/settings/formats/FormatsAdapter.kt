package com.example.barcodescanner.feature.tabs.settings.formats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.toStringId
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.item_barcode_format.view.*

class FormatsAdapter(
    private val listener: Listener,
    private val formats: List<BarcodeFormat>,
    private val formatSelection: List<Boolean>
) : RecyclerView.Adapter<FormatsAdapter.ViewHolder>() {

    interface Listener {
        fun onFormatChecked(format: BarcodeFormat, isChecked: Boolean)
    }

    override fun getItemCount(): Int {
        return formats.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_barcode_format, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.show(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun show(position: Int) {
            val format = formats[position]
            itemView.text_view_text.text = itemView.context.resources.getString(format.toStringId())
            itemView.delimiter.isInvisible = position == formats.lastIndex
            itemView.check_box.isChecked = formatSelection[position]

            itemView.check_box.setOnCheckedChangeListener { _, isChecked ->
               listener.onFormatChecked(format, isChecked)
            }

            itemView.setOnClickListener {
                itemView.check_box.toggle()
            }
        }
    }
}