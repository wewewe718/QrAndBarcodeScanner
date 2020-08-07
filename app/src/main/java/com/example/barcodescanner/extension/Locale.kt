package com.example.barcodescanner.extension

import java.util.*

val Locale?.isRussian: Boolean
    get() = this?.language == "ru"