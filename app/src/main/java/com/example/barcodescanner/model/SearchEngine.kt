package com.example.barcodescanner.model

enum class SearchEngine(val templateUrl: String) {
    NONE(""),
    ASK_EVERY_TIME(""),
    BING("https://www.bing.com/search?q="),
    DUCK_DUCK_GO("https://duckduckgo.com/?q="),
    GOOGLE("https://www.google.com/search?q="),
    QWANT("https://www.qwant.com/?q="),
    STARTPAGE("https://www.startpage.com/sp/search?query="),
    YAHOO("https://search.yahoo.com/search?p="),
    YANDEX("https://www.yandex.ru/search/?text="),
}