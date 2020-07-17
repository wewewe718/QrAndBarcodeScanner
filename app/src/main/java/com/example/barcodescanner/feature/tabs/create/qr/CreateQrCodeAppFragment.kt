package com.example.barcodescanner.feature.tabs.create.qr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.makeSmoothScrollable
import com.example.barcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.example.barcodescanner.model.schema.App
import com.example.barcodescanner.model.schema.Schema
import kotlinx.android.synthetic.main.fragment_create_qr_code_app.*

class CreateQrCodeAppFragment : BaseCreateBarcodeFragment() {
    private val appAdapter by lazy { AppAdapter(parentActivity) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_qr_code_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        showApps()
    }

    override fun getBarcodeSchema(): Schema {
        return App.fromPackage("")
    }

    private fun initRecyclerView() {
        recycler_view_apps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appAdapter
            makeSmoothScrollable()
        }
    }

    private fun showApps() {
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        appAdapter.apps = requireContext().packageManager
            .queryIntentActivities(mainIntent, 0)
            .filter { it.activityInfo?.packageName != null }
    }
}