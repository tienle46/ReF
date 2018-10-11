package com.example.tienle.ref

import android.app.Fragment
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.tienle.ref.Database.PlaceRepository
import com.example.tienle.ref.Local.PlaceDataSource
import com.example.tienle.ref.Local.PlaceDatabase
import com.example.tienle.ref.Model.Place
import com.example.tienle.ref.Model.PlaceWithImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
* Created by tienle on 10/6/18.
*/

class RestaurantListFragment: Fragment() {

    private lateinit var adapter:RestaurantAdapter
     private var placeList: MutableList<Place> = ArrayList()
    private var placeWithImageList = ArrayList<PlaceWithImage>()

    private var compositeDisposable: CompositeDisposable?=null
    private var placeRepository:PlaceRepository?=null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView:View = inflater!!.inflate(R.layout.fragment_restaurent_list, container, false)
        compositeDisposable = CompositeDisposable()
        adapter = RestaurantAdapter(activity,placeWithImageList)
        val listRestaurant:ListView = rootView.findViewById(R.id.listRestaurant)
        listRestaurant.adapter = adapter

        val placeDatabase = PlaceDatabase.getInstance(activity)
        placeRepository = PlaceRepository.getInstance(PlaceDataSource.getInstance(placeDatabase.placeDAO()))

        loadData()


        listRestaurant.setOnItemClickListener { _, _, position, _ ->
                val clickedPlace = placeDatabase.placeDAO().getPlaceById(position+1)
                clickedPlace.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({place ->
                            val intent = Intent(activity, MapsActivity::class.java)
                            intent.putExtra("clickedPlace",place)
                            startActivity(intent)
                        })
        }

        return rootView
    }

    private fun loadData() {
        val disposable = placeRepository!!.allPlaces
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({places -> onGetAllPlaceSuccess(places)})
                {
                    throwable -> Toast.makeText(activity,""+throwable.message, Toast.LENGTH_LONG).show()
                }
        compositeDisposable!!.add(disposable)
    }

    private fun onGetAllPlaceSuccess(places: List<Place>) {
        placeList.clear()
        placeList.addAll(places)
        for (place in placeList) {
            val imageName = place.path.split("/")[2]
            val imageInputStream = activity.resources.assets.open(imageName)
            val image:Drawable = Drawable.createFromStream(imageInputStream,null)
            val placeWithImage = PlaceWithImage(place.id,place.name,place.address,image)
            placeWithImageList.add(placeWithImage)
        }

        adapter.notifyDataSetChanged()

    }

}