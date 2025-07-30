package com.zunza.buythedip_kotlin.news.util

fun parse(response: String): Map<String, String> {
    val result = mutableMapOf<String, String>()

    val lines = response.lines()
    for (line in lines) {
        when {
            line.startsWith("headline:") -> {
                val headline = line.removePrefix("headline:").trim()
                result["headline"] = headline
            }
            line.startsWith("summary:") -> {
                val summary = line.removePrefix("summary:").trim()
                result["summary"] = summary
            }
        }
    }

    return result
}
