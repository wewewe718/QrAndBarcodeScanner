package com.example.barcodescanner.di

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.example.barcodescanner.App
import com.example.barcodescanner.usecase.*


val AppCompatActivity.barcodeSchemaParser
    get() = BarcodeSchemaParser

val AppCompatActivity.barcodeScanResultParser
    get() = BarcodeScanResultParser(barcodeSchemaParser)

val AppCompatActivity.barcodeImageScanner
    get() = BarcodeImageScanner

val AppCompatActivity.barcodeImageGenerator
    get() = BarcodeImageGenerator

val AppCompatActivity.barcodeImageSaver
    get() = BarcodeImageSaver

val AppCompatActivity.checkReceiptApi
    get() = CheckReceiptApi

val AppCompatActivity.wifiConnector
    get() = WifiConnector

val AppCompatActivity.barcodeDatabase
    get() = BarcodeDatabase.getInstance(this)

val AppCompatActivity.settings
    get() = Settings.getInstance(this)


val Fragment.scannerCameraHelper
    get() = ScannerCameraHelper

val Fragment.barcodeSchemaParser
    get() = BarcodeSchemaParser

val Fragment.barcodeScanResultParser
    get() = BarcodeScanResultParser(barcodeSchemaParser)

val Fragment.barcodeDatabase
    get() = BarcodeDatabase.getInstance(requireContext())

val Fragment.settings
    get() = Settings.getInstance(requireContext())


val AndroidViewModel.barcodeDatabase
    get() = BarcodeDatabase.getInstance(getApplication())
