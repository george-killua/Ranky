package com.killua.data.networking.model

import com.killua.data.cashing.model.ClubsEntity
import com.killua.data.models.Club

data class ClubApi(
    val country: String,
    val european_titles: Int,
    val id: String,
    val image: String,
    val location: Location,
    val name: String,
    val stadium: Stadium,
    val value: Int
)

fun ClubApi.toClub(): Club {
    return Club(country, european_titles, id, image, name, value)
}fun ClubApi.toClubEntity(): ClubsEntity {
    return ClubsEntity(id, name, image, european_titles, country, value)
}