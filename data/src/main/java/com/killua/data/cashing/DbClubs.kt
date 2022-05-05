package com.killua.data.cashing

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.killua.data.cashing.model.ClubsEntity
import com.killua.data.utils.Converters

@Database(entities = [ClubsEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DbClubs : RoomDatabase() {
    abstract fun clubsDao(): ClubsDao
}

