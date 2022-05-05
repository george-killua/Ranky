package com.killua.domain

import androidx.annotation.StringRes
import com.killua.data.models.Club
import com.killua.data.utils.RepoResult
import io.reactivex.rxjava3.core.Observable

interface GetAndCheckClubsUseCase {
    operator fun invoke(
        isConnected: Boolean,
        @StringRes databaseUpdate: Int
    ): Observable<RepoResult<List<Club>>>
}

