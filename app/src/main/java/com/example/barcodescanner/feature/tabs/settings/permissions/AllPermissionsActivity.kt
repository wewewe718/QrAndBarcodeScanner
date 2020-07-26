package com.example.barcodescanner.feature.tabs.settings.permissions

import android.content.*
import android.os.Bundle
import com.example.barcodescanner.*
import com.example.barcodescanner.extension.*
import com.example.barcodescanner.feature.BaseActivity
import kotlinx.android.synthetic.main.activity_all_permissions.*

class AllPermissionsActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AllPermissionsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_permissions)
        toolbar.setNavigationOnClickListener { finish() }
        scroll_view.makeSmoothScrollable()
    }
}