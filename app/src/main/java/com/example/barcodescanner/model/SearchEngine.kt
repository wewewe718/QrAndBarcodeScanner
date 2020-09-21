package com.example.barcodescanner.model

enum class SearchEngine(val templateUrl: String) {
    NONE(""),
    ASK_EVERY_TIME(""),
    GOOGLE("https://www.google.com/search?q="),
    DUCK_DUCK_GO("https://duckduckgo.com/?q="),
    YANDEX("https://www.yandex.ru/search/?text="),
    BING("https://www.bing.com/search?q="),
    YAHOO("https://search.yahoo.com/search?p="),
}