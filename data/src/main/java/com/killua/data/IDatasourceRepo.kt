package com.killua.data


import androidx.annotation.StringRes
import com.killua.data.models.Club
import com.killua.data.utils.RepoResult
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface IDatasourceRepo {

    fun getClub(id: String): Single<Club>

    fun getAndCheckClubs(
        isConnected: Boolean,
        @StringRes databaseUpdate: Int
    ): Observable<RepoResult<List<Club>>>

    fun dispose()
}