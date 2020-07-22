package com.example.barcodescanner.feature.tabs.create.qr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.barcodescanner.R
import com.example.barcodescanner.di.permissionsHelper
import com.example.barcodescanner.extension.orZero
import com.example.barcodescanner.feature.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_choose_location_on_map.*

class ChooseLocationOnMapActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        const val LATITUDE_KEY = "LATITUDE_KEY"
        const val LONGITUDE_KEY = "LONGITUDE_KEY"

        fun start(activity: AppCompatActivity, requestCode: Int, latitude: Double?, longitude: Double?) {
            val intent = Intent(activity, ChooseLocationOnMapActivity::class.java).apply {
                putExtra(LATITUDE_KEY, latitude)
                putExtra(LONGITUDE_KEY, longitude)
            }
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private lateinit var map: GoogleMap
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_location_on_map)

        map_view.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@ChooseLocationOnMapActivity)
        }

        toolbar.apply {
            setNavigationOnClickListener {
                finish()
            }
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_confirm -> finishWithResult()
                }
                return@setOnMenuItemClickListener true
            }
        }


    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        map_view.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }

    override fun onMapReady(map: GoogleMap?) {
        if (map == null) return

        this.map = map

        map.uiSettings?.apply {
            isRotateGesturesEnabled = true
            isScrollGesturesEnabled = true
            isTiltGesturesEnabled = true
            isZoomGesturesEnabled = true
            isZoomControlsEnabled = true
            isCompassEnabled = false
            isMapToolbarEnabled = false
        }

        map.setOnMapClickListener(this)
        showStartMarker()

        permissionsHelper.requestPermissions(this, LOCATION_PERMISSIONS, PERMISSION_REQUEST_CODE)
    }

    override fun onMapClick(position: LatLng?) {
        hideMarker()
        showMarker(position)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE && permissionsHelper.areAllPermissionsGranted(grantResults)) {
            map.isMyLocationEnabled = true
            map.uiSettings?.isMyLocationButtonEnabled = true
        }
    }

    private fun showStartMarker() {
        val latitude = intent?.getDoubleExtra(LATITUDE_KEY, 0.0).orZero()
        val longitude = intent?.getDoubleExtra(LONGITUDE_KEY, 0.0).orZero()
        showMarker(LatLng(latitude, longitude), true)
    }

    private fun showMarker(position: LatLng?, moveCameraToPosition: Boolean = false) {
        if (position == null) {
            return
        }

        val markerOptions = MarkerOptions().position(position)
        marker = map.addMarker(markerOptions)

        if (moveCameraToPosition.not()) {
            return
        }

        val newCameraPosition = CameraPosition
            .builder(map.cameraPosition)
            .target(position)
            .build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(newCameraPosition)
        map.animateCamera(cameraUpdate)
    }

    private fun hideMarker() {
        marker?.remove()
    }

    private fun finishWithResult() {
        val result = Intent().apply {
            putExtra(LATITUDE_KEY, marker?.position?.latitude.orZero())
            putExtra(LONGITUDE_KEY, marker?.position?.longitude.orZero())
        }
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}