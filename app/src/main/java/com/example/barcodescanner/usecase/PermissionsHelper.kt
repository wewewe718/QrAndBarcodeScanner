package com.example.barcodescanner.usecase

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionsHelper {

    fun requestPermissions(activity: AppCompatActivity, permissions: Array<out String>, requestCode: Int) {
        if (areAllPermissionsGranted(activity, permissions)) {
            activity.onRequestPermissionsResult(
                requestCode,
                permissions,
                IntArray(permissions.size) { PackageManager.PERMISSION_GRANTED }
            )
            return
        }

        val notGrantedPermissions = permissions.filterNot { isPermissionGranted(activity, it) }
        ActivityCompat.requestPermissions(activity, notGrantedPermissions.toTypedArray(), requestCode)
    }

    fun requestNotGrantedPermissions(activity: AppCompatActivity, permissions: Array<out String>, requestCode: Int) {
        val notGrantedPermissions = permissions.filterNot { isPermissionGranted(activity, it) }
        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, notGrantedPermissions.toTypedArray(), requestCode)
        }
    }

    fun areAllPermissionsGranted(context: Context, permissions: Array<out String>): Boolean {
        permissions.forEach { permission ->
            if (isPermissionGranted(context, permission).not()) {
                return false
            }
        }
        return true
    }

    fun areAllPermissionsGranted(grantResults: IntArray): Boolean {
        grantResults.forEach { result ->
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}