package com.killua.data.utils

import androidx.room.TypeConverter
import java.util.*

object Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? = value?.let { milliSec ->
        Calendar.getInstance().apply {
            timeInMillis = milliSec
        }
    }

    @TypeConverter
    fun toTimestamp(timestamp: Calendar?): Long? = timestamp?.timeInMillis
}