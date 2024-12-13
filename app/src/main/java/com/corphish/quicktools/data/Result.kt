package com.corphish.quicktools.data

sealed class Result<out T> {
    // Called when we have a successful call of operation with a value
    class Success<out T>(val value: T): Result<T>()

    // Called when there is an error while performing an operation
    object Error : Result<Nothing>()

    // Initial value
    object Initial: Result<Nothing>()
}