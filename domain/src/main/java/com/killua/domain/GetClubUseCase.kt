package com.killua.domain

import com.killua.data.models.Club
import io.reactivex.rxjava3.core.Single

interface GetClubUseCase {
    operator fun invoke(clubId: String): Single<Club>
}

