package com.dhkim.database

import androidx.room.TypeConverter
import com.google.gson.Gson

class RoomConverter {

    @TypeConverter
    fun listToJson(value: List<String>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String>? {
        return Gson().fromJson(value, Array<String>::class.java)?.toList()
    }

    @TypeConverter
    fun tripImageToJson(value: List<TripImageDto>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToTripImage(value: String?): List<TripImageDto>? {
        return if (value != null) {
            Gson().fromJson(value, Array<TripImageDto>::class.java)?.toList()
        } else {
            null
        }
    }

    @TypeConverter
    fun tripVideoToJson(value: List<TripVideoDto>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToTripVideo(value: String?): List<TripVideoDto>? {
        return if (value != null) {
            Gson().fromJson(value, Array<TripVideoDto>::class.java)?.toList()
        } else {
            null
        }
    }
}