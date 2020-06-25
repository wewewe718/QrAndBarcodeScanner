package com.example.barcodescanner.di

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.example.barcodescanner.App


val AppCompatActivity.app
    get() = application as App

val AppCompatActivity.barcodeImageGenerator
    get() = app.barcodeImageGenerator

val AppCompatActivity.barcodeImageSaver
    get() = app.barcodeImageSaver

val AppCompatActivity.checkReceiptApi
    get() = app.checkReceiptApi

val AppCompatActivity.wifiConnector
    get() = app.wifiConnector


val Fragment.parentActivity
    get() = requireActivity() as AppCompatActivity

val Fragment.scannerCameraHelper
    get() = parentActivity.app.scannerCameraHelper

val Fragment.barcodeSchemaParser
    get() = parentActivity.app.barcodeSchemaParser

val Fragment.barcodeDatabase
    get() = parentActivity.app.barcodeDatabase


val AndroidViewModel.barcodeDatabase
    get() = getApplication<App>().barcodeDatabase
