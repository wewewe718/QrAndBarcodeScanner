package com.example.barcodescanner.feature.tabs

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.barcodescanner.BuildConfig
import com.example.barcodescanner.R
import com.example.barcodescanner.extension.applySystemWindowInsets
import com.example.barcodescanner.feature.BaseActivity
import com.example.barcodescanner.feature.tabs.create.CreateBarcodeFragment
import com.example.barcodescanner.feature.tabs.history.BarcodeHistoryFragment
import com.example.barcodescanner.feature.tabs.scan.ScanBarcodeFromCameraFragment
import com.example.barcodescanner.feature.tabs.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_bottom_tabs.*

class BottomTabsActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val ACTION_CREATE_BARCODE = "${BuildConfig.APPLICATION_ID}.CREATE_BARCODE"
        private const val ACTION_HISTORY = "${BuildConfig.APPLICATION_ID}.HISTORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_tabs)

        supportEdgeToEdge()
        initBottomNavigationView()

        if (savedInstanceState == null) {
            showInitialFragment()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == bottom_navigation_view.selectedItemId) {
            return false
        }
        showFragment(item.itemId)
        return true
    }

    override fun onBackPressed() {
        if (bottom_navigation_view.selectedItemId == R.id.item_scan) {
            super.onBackPressed()
        } else {
            bottom_navigation_view.selectedItemId = R.id.item_scan
        }
    }

    private fun supportEdgeToEdge() {
        bottom_navigation_view.applySystemWindowInsets(applyBottom = true)
    }

    private fun initBottomNavigationView() {
        bottom_navigation_view.apply {
            setOnNavigationItemSelectedListener(this@BottomTabsActivity)
        }
    }

    private fun showInitialFragment() {
        when (intent?.action) {
            ACTION_CREATE_BARCODE -> bottom_navigation_view.selectedItemId = R.id.item_create
            ACTION_HISTORY -> bottom_navigation_view.selectedItemId = R.id.item_history
            else -> showFragment(R.id.item_scan)
        }
    }

    private fun showFragment(bottomItemId: Int) {
        val fragment = when (bottomItemId) {
            R.id.item_scan -> ScanBarcodeFromCameraFragment()
            R.id.item_create -> CreateBarcodeFragment()
            R.id.item_history -> BarcodeHistoryFragment()
            R.id.item_settings -> SettingsFragment()
            else -> null
        }
        fragment?.apply(::replaceFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_fragment_container, fragment)
            .setReorderingAllowed(true)
            .commit()
    }
}