package com.killua.data.cashing.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.killua.data.models.Club
import java.util.*


@Entity(tableName = "clubs")
data class ClubsEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val image: String,
    val european_titles: Int,
    val country: String,
    val value: Int,
    val createDate: Calendar = Calendar.getInstance().apply {
        this.timeInMillis = System.currentTimeMillis()
    }
)

fun Club.toEntity() = ClubsEntity(
    id = this.id,
    country = this.country,
    name = this.name,
    image = this.image,
    european_titles = this.european_titles,
    createDate = Calendar.getInstance(),
    value = this.value,

    )

fun ClubsEntity.toClub() = Club(
    id = this.id,
    country = this.country,
    name = this.name,
    image = this.image,
    european_titles = this.european_titles,
    value = this.value,

    )

