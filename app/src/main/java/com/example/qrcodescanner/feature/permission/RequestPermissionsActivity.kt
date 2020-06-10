package com.example.qrcodescanner.feature.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrcodescanner.R
import com.example.qrcodescanner.feature.scan.ScanQrCodeActivity
import kotlinx.android.synthetic.main.activity_request_permissions.*

class RequestPermissionsActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101

        fun start(context: Context) {
            val intent = Intent(context, RequestPermissionsActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val permissions = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (areAllPermissionsGranted(permissions)) {
            navigateToNextScreen()
        }

        setContentView(R.layout.activity_request_permissions)
        handleToolbarBackClicked()
        handleRequestPermissionsButtonClicked()
    }

    private fun handleToolbarBackClicked() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun handleRequestPermissionsButtonClicked() {
        button_request_permissions.setOnClickListener {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != PERMISSION_REQUEST_CODE) {
            return
        }

        if (areAllPermissionsGranted(grantResults)) {
            navigateToNextScreen()
        } else {
            finish()
        }
    }

    private fun areAllPermissionsGranted(permissions: Array<out String>): Boolean {
        permissions.forEach { permission ->
            val checkResult = ContextCompat.checkSelfPermission(this, permission)
            if (checkResult != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun areAllPermissionsGranted(grantResults: IntArray): Boolean {
        grantResults.forEach { result ->
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun navigateToNextScreen() {
        ScanQrCodeActivity.start(this)
        finish()
    }
}