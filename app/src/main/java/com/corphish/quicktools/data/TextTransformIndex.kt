package com.corphish.quicktools.data

import com.corphish.quicktools.repository.TextTransformRepositoryImpl
import com.corphish.quicktools.viewmodels.TextTransformViewModel

/**
 * Single source of truth for the primary text-transform option indices.
 * Both [TextTransformRepositoryImpl] (which maps an index to
 * the actual transformation) and [TextTransformViewModel]
 * (which maps an index to its UI options) must agree on this mapping, so it is defined once here
 * instead of being duplicated as separate companion objects in each class.
 */
object TextTransformIndex {
    const val INDEX_NONE = 0
    const val INDEX_WRAP_TEXT = 1
    const val INDEX_CHANGE_CASE = 2
    const val INDEX_SORT_LINES = 3
    const val INDEX_REPEAT_TEXT = 4
    const val INDEX_REMOVE_TEXT = 5
    const val INDEX_ADD_PREFIX_SUFFIX = 6
    const val INDEX_NUMBER_LINES = 7
    const val INDEX_PREPEND_LINES = 8
    const val INDEX_APPEND_LINES = 9
    const val INDEX_REVERSE_TEXT = 10
    const val INDEX_REVERSE_WORDS = 11
    const val INDEX_REVERSE_LINES = 12
    const val INDEX_DECORATE_TEXT = 13
    const val INDEX_LINE_BREAK = 14
    const val INDEX_SQUEEZE = 15
    const val INDEX_REPLACE_WHITESPACE = 16
}
