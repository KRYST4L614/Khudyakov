package com.example.khudyakovlab

class Utils {
    companion object {
        fun removeDoubleQuotes(string: String): String =
            string.replace("^\"|\"$".toRegex(), "")

        fun removeSquareBrackets(string: String): String =
            string.replace("^\\[|\\]$".toRegex(), "")
    }
}