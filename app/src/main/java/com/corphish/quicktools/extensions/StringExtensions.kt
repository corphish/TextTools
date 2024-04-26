package com.corphish.quicktools.extensions

/**
 * Truncates text till a certain length
 */
fun String.truncate(len: Int = 16): String {
    val suffix = "..."
    return if (this.length <= len) {
        this
    } else {
        this.substring(0, len - suffix.length) + suffix
    }
}