package com.example.qrcodescanner

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel


val AppCompatActivity.app
    get() = application as App

val AppCompatActivity.barcodeSchemaParser
    get() = app.barcodeSchemaParser

val AppCompatActivity.barcodeImageGenerator
    get() = app.barcodeImageGenerator


val AndroidViewModel.db
    get() = getApplication<App>().db

val AndroidViewModel.barcodeSchemaParser
    get() = getApplication<App>().barcodeSchemaParser