package com.killua.data.networking

import com.killua.data.networking.model.ClubApi
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface ClubsServices {
    @GET("clubs.json")
    fun getAllClubs(): Single<List<ClubApi>>
}
