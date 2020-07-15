package com.example.barcodescanner.extension

inline fun <reified T : Enum<T>> valueOrNull(type: String): T? {
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: Exception) {
        null
    }
}