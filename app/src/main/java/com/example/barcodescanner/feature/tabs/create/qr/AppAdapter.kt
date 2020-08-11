package com.example.barcodescanner.feature.tabs.create.qr

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.barcodescanner.R
import kotlinx.android.synthetic.main.item_app.view.*

class AppAdapter(private val listener: Listener) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    interface Listener {
        fun onAppClicked(packageName: String)
    }

    var apps: List<ResolveInfo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_app, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        val isLastPosition = position == apps.lastIndex
        holder.show(app, isLastPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val packageManager: PackageManager
            get() = itemView.context.applicationContext.packageManager

        fun show(app: ResolveInfo, isLastPosition: Boolean) {
            showName(app)
            showIcon(app)
            showDelimiter(isLastPosition)
            handleItemClicked(app)
        }

        private fun showName(app: ResolveInfo) {
            itemView.text_view.text = app.loadLabel(packageManager)
        }

        private fun showIcon(app: ResolveInfo) {
            itemView.image_view.setImageDrawable(app.loadIcon(packageManager))
        }

        private fun showDelimiter(isLastPosition: Boolean) {
            itemView.delimiter.isInvisible = isLastPosition
        }

        private fun handleItemClicked(app: ResolveInfo) {
            itemView.setOnClickListener {
                listener.onAppClicked(app.activityInfo?.packageName.orEmpty())
            }
        }
    }
}