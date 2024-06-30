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

/*    @TypeConverter
    fun petToJson(value: Pet?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToPet(value: String?): Pet? {
        return if (value != null) {
            Gson().fromJson(value, Pet::class.java)
        } else {
            null
        }
    }*/
}