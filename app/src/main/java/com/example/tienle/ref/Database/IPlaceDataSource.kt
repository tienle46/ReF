package com.example.tienle.ref.Database

import com.example.tienle.ref.Model.Place
import io.reactivex.Flowable

/**
 * Created by tienle on 10/6/18.
 */
interface IPlaceDataSource {
    val allPlaces:Flowable<List<Place>>
    fun getPlaceById(placeId:Int):Flowable<Place>
    fun insertPlace (vararg places:Place)
    fun updatePlace (vararg places:Place)
    fun deletePlace (places:Place)
    fun deleteAllPlaces()

}