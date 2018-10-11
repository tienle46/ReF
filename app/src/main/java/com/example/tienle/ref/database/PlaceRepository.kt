package com.example.tienle.ref.Database

import com.example.tienle.ref.Model.Place
import io.reactivex.Flowable

/**
 * Created by tienle on 10/6/18.
 */
class PlaceRepository(private val mLocationDataSource: IPlaceDataSource):IPlaceDataSource {
    override val allPlaces: Flowable<List<Place>>
        get() = mLocationDataSource.allPlaces

    override fun getPlaceById(placeId: Int): Flowable<Place> {
        return mLocationDataSource.getPlaceById(placeId)
    }

    override fun insertPlace(vararg places: Place) {
        mLocationDataSource.insertPlace(*places)
    }

    override fun updatePlace(vararg places: Place) {
        mLocationDataSource.updatePlace(*places)
    }

    override fun deletePlace(places: Place) {
        mLocationDataSource.deletePlace(places)
    }

    override fun deleteAllPlaces() {
        mLocationDataSource.deleteAllPlaces()
    }

    companion object {
        private var mInstance: PlaceRepository?=null
        fun getInstance(mLocationDataSource: IPlaceDataSource):PlaceRepository{
            if(mInstance == null) {
                mInstance = PlaceRepository(mLocationDataSource)
            }
            return mInstance!!
        }
    }

}