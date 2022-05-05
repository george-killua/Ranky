package com.killua.data.utils

import androidx.annotation.StringRes

sealed class RepoResult<out T : Any> {
    object Loading : RepoResult<Nothing>()
    data class Success<T : Any>(val data: T) : RepoResult<T>()
    data class Failure<T : Any>(@StringRes val message: Int) : RepoResult<T>()
}

sealed class OperatorResult<out T : Any> {
    object Staging : OperatorResult<Nothing>()
    data class Done<T : Any>(val data: T? = null) : OperatorResult<T>()


    object Error : OperatorResult<Nothing>()
}


