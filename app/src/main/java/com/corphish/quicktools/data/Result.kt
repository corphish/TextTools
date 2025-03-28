package com.corphish.quicktools.data

sealed class Result<out T> {
    // Called when we have a successful call of operation with a value
    class Success<out T>(val value: T): Result<T>()

    // Called when there is an error while performing an operation
    data object Error : Result<Nothing>()

    // Initial value
    data object Initial: Result<Nothing>()
}