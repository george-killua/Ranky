package com.killua.domain

import com.killua.data.IDatasourceRepo
import javax.inject.Inject

class GetClubUseCaseImpl @Inject constructor(
    private val dataSource: IDatasourceRepo
) : GetClubUseCase {
    override fun invoke(clubId: String) = dataSource.getClub(id = clubId)
}