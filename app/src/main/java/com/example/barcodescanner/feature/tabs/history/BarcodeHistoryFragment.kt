package com.example.barcodescanner.feature.tabs.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barcodescanner.R
import com.example.barcodescanner.feature.barcode.BarcodeActivity
import com.example.barcodescanner.extension.showError
import com.example.barcodescanner.feature.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_barcode_history.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class BarcodeHistoryFragment : Fragment() {
    private val disposable = CompositeDisposable()
    private val scanHistoryAdapter = BarcodeHistoryAdapter()
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(BarcodeHistoryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? BaseActivity)?.setWhiteStatusBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_barcode_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        subscribeToViewModel()
    }

    override fun onPause() {
        super.onPause()
        unsubscribeFromViewModel()
    }

    private fun initRecyclerView() {
        recycler_view_history.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scanHistoryAdapter
        }
        OverScrollDecoratorHelper.setUpOverScroll(recycler_view_history, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
    }

    private fun subscribeToViewModel() {
        subscribeToBarcodeClicks()
        subscribeToHistoryDataChanged()
        subscribeToHistory()
        subscribeToError()
        subscribeToNavigateToBarcodeScreen()
    }

    private fun subscribeToBarcodeClicks() {
        scanHistoryAdapter.barcodeClicked
            .subscribe(viewModel::onBarcodeClicked)
            .addTo(disposable)
    }

    private fun subscribeToHistoryDataChanged() {
        scanHistoryAdapter.dataChanged
            .subscribe {
                recycler_view_history.layoutManager?.scrollToPosition(0)
            }
            .addTo(disposable)
    }

    private fun subscribeToHistory() {
        viewModel.scanHistory
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(scanHistoryAdapter::submitList)
            .addTo(disposable)
    }

    private fun subscribeToError() {
        viewModel.error
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::showError)
            .addTo(disposable)
    }

    private fun subscribeToNavigateToBarcodeScreen() {
        viewModel.navigateToBarcodeScreenEvent
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { barcode ->
                BarcodeActivity.start(requireActivity(), barcode)
            }
            .addTo(disposable)
    }

    private fun unsubscribeFromViewModel() {
        disposable.clear()
    }
}