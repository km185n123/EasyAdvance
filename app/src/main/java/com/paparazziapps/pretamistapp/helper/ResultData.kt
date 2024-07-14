package com.paparazziapps.pretamistapp.helper

sealed class ResultData<T> {
    data class Success<T>(val value: T) : ResultData<T>()
    data class Failure<T>(val throwable: Throwable) : ResultData<T>()
    data class Loading<T>(val isLoading: Boolean = true) : ResultData<T>()
}
