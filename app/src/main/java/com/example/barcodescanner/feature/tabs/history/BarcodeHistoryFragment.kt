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
import com.example.barcodescanner.feature.common.showError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_barcode_history.*

class BarcodeHistoryFragment : Fragment(), DeleteHistoryConfirmationDialogFragment.Listener {
    private val disposable = CompositeDisposable()
    private val scanHistoryAdapter = BarcodeHistoryAdapter()
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(BarcodeHistoryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_barcode_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbarMenu()
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

    override fun onDeleteHistoryPositiveButtonClicked() {
        viewModel.onDeleteHistoryConfirmed()
    }

    private fun initToolbarMenu() {
        toolbar.inflateMenu(R.menu.menu_barcode_history)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.item_delete_all) {
                viewModel.onDeleteHistoryClicked()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun initRecyclerView() {
        recycler_view_history.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scanHistoryAdapter
        }
    }

    private fun subscribeToViewModel() {
        subscribeToBarcodeClicks()
        subscribeToHistoryDataChanged()
        subscribeToHistory()
        subscribeToDeleteHistoryDialog()
        subscribeToLoading()
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

    private fun subscribeToDeleteHistoryDialog() {
        viewModel.showDeleteHistoryConfirmationEvent
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { showDeleteHistoryConfirmationDialog() }
            .addTo(disposable)
    }

    private fun subscribeToLoading() {
        viewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::showLoading)
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

    private fun showDeleteHistoryConfirmationDialog() {
        val dialog = DeleteHistoryConfirmationDialogFragment()
        dialog.show(childFragmentManager, "")
    }

    private fun showLoading(isLoading: Boolean) {
        progress_bar_loading.isVisible = isLoading
        recycler_view_history.isVisible = isLoading.not()
    }
}