package com.example.tienle.ref.local

import android.arch.persistence.room.*
import com.example.tienle.ref.model.Place
import io.reactivex.Flowable

/**
* Created by tienle on 10/6/18.
*/

@Dao
interface PlaceDAO {
    @get:Query("SELECT * FROM places")
    val allPlaces:Flowable<List<Place>>

    @Query("SELECT * FROM places WHERE id=:placeId")
    fun getPlaceById(placeId: Int):Flowable<Place>

    @Insert
    fun insertPlace(vararg places:Place)

    @Update
    fun updatePlace(vararg places:Place)

    @Delete
    fun deletePlace(vararg places:Place)

    @Query("DELETE FROM places")
    fun deleteAllPlaces()
}