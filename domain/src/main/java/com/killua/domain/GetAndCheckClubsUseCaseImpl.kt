package com.killua.domain

import androidx.annotation.StringRes
import com.killua.data.IDatasourceRepo
import com.killua.data.models.Club
import com.killua.data.utils.RepoResult
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetAndCheckClubsUseCaseImpl @Inject constructor(
    private val dataSource: IDatasourceRepo
) : GetAndCheckClubsUseCase {
    override fun invoke(
        isConnected: Boolean,
        @StringRes databaseUpdate: Int
    ): Observable<RepoResult<List<Club>>> =
        dataSource.getAndCheckClubs(isConnected, databaseUpdate)
}