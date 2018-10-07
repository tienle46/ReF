package com.example.tienle.ref.Local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.tienle.ref.Local.PlaceDatabase.Companion.DATABASE_VERSION
import com.example.tienle.ref.Model.Place
import com.huma.room_for_asset.RoomAsset

/**
* Created by tienle on 10/6/18.
*/

@Database(entities = [(Place::class)],version = DATABASE_VERSION)
abstract class PlaceDatabase:RoomDatabase() {
    abstract fun placeDAO(): PlaceDAO

    companion object {
        const val DATABASE_VERSION = 2
        private const val DATABASE_NAME="RestaurantDB.sqlite"

        private var mInstance:PlaceDatabase?=null

        fun getInstance(context:Context): PlaceDatabase {
            if(mInstance == null)
                mInstance = RoomAsset.databaseBuilder(context,PlaceDatabase::class.java, DATABASE_NAME)
                        .build()
            return mInstance!!
        }
    }
}
