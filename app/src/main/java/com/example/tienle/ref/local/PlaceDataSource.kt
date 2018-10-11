package com.example.tienle.ref.local

import com.example.tienle.ref.database.IPlaceDataSource
import com.example.tienle.ref.model.Place
import io.reactivex.Flowable

/**
* Created by tienle on 10/6/18.
*/

class PlaceDataSource(private val placeDAO: PlaceDAO):IPlaceDataSource {
    override val allPlaces: Flowable<List<Place>>
        get() = placeDAO.allPlaces

    override fun getPlaceById(placeId: Int): Flowable<Place> {
        return placeDAO.getPlaceById(placeId)
    }

    override fun insertPlace(vararg places: Place) {
        placeDAO.insertPlace(*places)
    }

    override fun updatePlace(vararg places: Place) {
        placeDAO.updatePlace(*places)
    }

    override fun deletePlace(places: Place) {
        placeDAO.deletePlace(places)
    }

    override fun deleteAllPlaces() {
        placeDAO.deleteAllPlaces()
    }

    companion object {
        private var mInstance: PlaceDataSource? = null
        fun getInstance(placeDAO: PlaceDAO): PlaceDataSource {
            if (mInstance == null) {
                mInstance = PlaceDataSource(placeDAO)
            }
            return mInstance!!
        }
    }
}