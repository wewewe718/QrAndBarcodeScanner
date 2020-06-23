package com.example.barcodescanner.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import com.example.barcodescanner.App


val AppCompatActivity.app
    get() = application as App

val AppCompatActivity.scannerCameraHelper
    get() = app.scannerCameraHelper

val AppCompatActivity.barcodeSchemaParser
    get() = app.barcodeSchemaParser

val AppCompatActivity.barcodeDatabase
    get() = app.barcodeDatabase

val AppCompatActivity.barcodeImageGenerator
    get() = app.barcodeImageGenerator

val AppCompatActivity.barcodeImageSaver
    get() = app.barcodeImageSaver

val AppCompatActivity.checkReceiptApi
    get() = app.checkReceiptApi

val AppCompatActivity.wifiConnector
    get() = app.wifiConnector


val AndroidViewModel.barcodeDatabase
    get() = getApplication<App>().barcodeDatabase

val AndroidViewModel.barcodeSchemaParser
    get() = getApplication<App>().barcodeSchemaParser
