package com.dhkim.timecapsule.common

sealed interface CommonResult<out T> {

    data class Success<T>(val data: T?): CommonResult<T>
    data class Error<T>(val code: Int): CommonResult<T>
}