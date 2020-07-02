package com.example.barcodescanner.di

import android.content.Context
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.example.barcodescanner.App
import com.example.barcodescanner.usecase.*


val Context.vibrator
    get() = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator


val AppCompatActivity.app
    get() = application as App

val AppCompatActivity.barcodeScanResultParser
    get() = BarcodeScanResultParser

val AppCompatActivity.barcodeImageScanner
    get() = BarcodeImageScanner

val AppCompatActivity.barcodeImageGenerator
    get() = BarcodeImageGenerator

val AppCompatActivity.barcodeImageSaver
    get() = app.barcodeImageSaver

val AppCompatActivity.checkReceiptApi
    get() = app.checkReceiptApi

val AppCompatActivity.wifiConnector
    get() = app.wifiConnector

val AppCompatActivity.barcodeDatabase
    get() = app.barcodeDatabase

val AppCompatActivity.settings
    get() = app.settings


val Fragment.parentActivity
    get() = requireActivity() as AppCompatActivity

val Fragment.scannerCameraHelper
    get() = ScannerCameraHelper

val Fragment.barcodeScanResultParser
    get() = BarcodeScanResultParser

val Fragment.barcodeDatabase
    get() = parentActivity.app.barcodeDatabase

val Fragment.settings
    get() = parentActivity.app.settings

val Fragment.vibratorHelper
    get() = VibratorHelper


val AndroidViewModel.barcodeDatabase
    get() = getApplication<App>().barcodeDatabase
