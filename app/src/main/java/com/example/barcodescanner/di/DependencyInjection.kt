package com.example.barcodescanner.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import com.example.barcodescanner.App


val AppCompatActivity.app
    get() = application as App

val AppCompatActivity.barcodeSchemaParser
    get() = app.barcodeSchemaParser

val AppCompatActivity.barcodeImageGenerator
    get() = app.barcodeImageGenerator

val AppCompatActivity.barcodeImageSaver
    get() = app.barcodeImageSaver


val AndroidViewModel.barcodeDatabase
    get() = getApplication<App>().barcodeDatabase

val AndroidViewModel.barcodeSchemaParser
    get() = getApplication<App>().barcodeSchemaParser

val AndroidViewModel.barcodeImageSaver
    get() = getApplication<App>().barcodeImageSaver